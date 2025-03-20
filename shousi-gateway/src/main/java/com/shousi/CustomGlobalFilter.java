package com.shousi;

import cn.hutool.core.text.CharSequenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shousi.shousiageteway.exception.BusinessException;
import com.shousi.shousiageteway.utils.RedissonLockUtil;
import com.shousi.shousicommon.common.ErrorCode;
import com.shousi.shousicommon.model.dto.interfaceinfo.RequestParamsField;
import com.shousi.shousicommon.model.entity.InterfaceInfo;
import com.shousi.shousicommon.model.enums.InterfaceInfoStatusEnum;
import com.shousi.shousicommon.model.vo.UserVO;
import com.shousi.shousicommon.service.InnerInterfaceInfoService;
import com.shousi.shousicommon.service.InnerUserInterfaceInfoService;
import com.shousi.shousicommon.service.InnerUserService;
import com.shousi.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bouncycastle.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.shousi.CacheBodyGatewayFilter.CACHE_REQUEST_BODY_OBJECT_KEY;
import static com.shousi.shousiageteway.utils.NetUtils.getIp;

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private RedissonLockUtil redissonLockUtil;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserService innerUserService;

    /**
     * 请求白名单
     */
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    /**
     * 过期时间
     */
    private static final long EXPIRE_SECONDS = 5 * 60L;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.请求日志
        // 日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一id：" + request.getId());
        log.info("请求方法：" + request.getMethod());
        log.info("请求路径：" + request.getPath());
        log.info("网关本地地址：" + request.getLocalAddress());
        log.info("请求远程地址：" + request.getRemoteAddress());
        log.info("接口请求IP：" + getIp(request));
        log.info("url:" + request.getURI());
        return verifyParameters(exchange, chain);
    }

    /**
     * 验证请求参数
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> verifyParameters(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 2.黑白名单
//        ServerHttpResponse response = exchange.getResponse();
//        if (!IP_WHITE_LIST.contains(hostString)) {
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//            // 结束请求
//            return response.setComplete();
//        }
        // 3.用户鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        // 防止中文乱码
        String body = headers.getFirst("body");
        // 请求参数必须完整
        if (StringUtils.isAnyBlank(body, accessKey, nonce, timestamp, sign)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "参数有误");
        }
        try {
            body = URLDecoder.decode(body, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (Long.parseLong(nonce) > 10000L) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求次数过多");
        }
        // 时间不能超过5分钟
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - Long.parseLong(timestamp) > EXPIRE_SECONDS) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "会话已经过期，请重试");
        }
        // 根据实际情况去数据库中查是否已分配给用户，在方法中已经进行判空
        try {
            UserVO invokeUser = innerUserService.getInvokeUser(accessKey);
            if (invokeUser == null) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户不存在");
            }
            String secretKey = invokeUser.getSecretKey();
            if (!CharSequenceUtil.equals(secretKey, invokeUser.getSecretKey())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请先获取密钥");
            }
            String severSign = SignUtils.genSign(body, secretKey);
            if (!CharSequenceUtil.equals(severSign, sign)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "签名错误");
            }
            // 请求方法
            String method = Objects.requireNonNull(request.getMethod()).toString();
            if (StringUtils.isAnyBlank(method)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求方法有误");
            }
            String uri = Objects.requireNonNull(request.getURI().toString());
            if (StringUtils.isAnyBlank(uri)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求地址有误");
            }
            // 4.请求的模拟接口是否存在，在方法中已经进行判空
            InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(uri, method);
            if (interfaceInfo == null) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "接口不存在");
            }
            if (interfaceInfo.getStatus() == InterfaceInfoStatusEnum.AUDITING.getValue()) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "接口审核中");
            }
            if (interfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "接口已关闭");
            }
            // 请求参数
            MultiValueMap<String, String> queryParams = request.getQueryParams();
            String requestParams = interfaceInfo.getRequestParams();
            List<RequestParamsField> requestParamsFieldList = new Gson().fromJson(requestParams, new TypeToken<List<RequestParamsField>>() {
            }.getType());
            // POST请求
            if ("POST".equals(method)) {
                Object cacheBody = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
                String requestBody = getPostRequestBody((Flux<DataBuffer>) cacheBody);
                Map<String, Object> requestBodyMap = new Gson().fromJson(requestBody, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                if (StringUtils.isNotBlank(requestParams)) {
                    for (RequestParamsField requestParamsField : requestParamsFieldList) {
                        if ("是".equals(requestParamsField.getRequired())) {
                            if (requestBodyMap.get(requestParamsField.getFieldName()) == null) {
                                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求参数有误，" + requestParamsField.getFieldName() + "为必选项");
                            }
                        }
                    }
                }
            } else { // GET请求
                if (StringUtils.isNotBlank(requestParams)) {
                    for (RequestParamsField requestParamsField : requestParamsFieldList) {
                        if ("是".equals(requestParamsField.getRequired())) {
                            if (queryParams.getFirst(requestParamsField.getFieldName()) == null) {
                                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求参数有误，" + requestParamsField.getFieldName() + "为必选项");
                            }
                        }
                    }
                }
            }
            return handleResponse(exchange, chain, invokeUser, interfaceInfo);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        }
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, UserVO user, InterfaceInfo interfaceInfo) {
        try {
            // 从交换机拿到原始response
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓冲区工厂 拿到缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到状态码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 对象是响应式的
                        if (body instanceof Flux) {
                            // 我们拿到真正的body
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里面写数据
                            // 拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 7. 调用成功，接口调用次数+1
                                boolean result = innerUserInterfaceInfoService.invokeCount(interfaceInfo.getId(), user.getId());
                                if (!result) {
                                    Mono.error(new RuntimeException("接口调用次数不足"));
                                }
                                // data从这个content中读取
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);// 释放掉内存
                                // 6.构建日志
                                String data = new String(content, StandardCharsets.UTF_8);// data
                                log.info("响应结果：" + data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            // 8.调用失败返回错误状态码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);// 降级处理返回数据
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }

    /**
     * 获取post请求正文
     *
     * @param body 身体
     * @return {@link String}
     */
    private String getPostRequestBody(Flux<DataBuffer> body) {
        AtomicReference<String> getBody = new AtomicReference<>();
        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            getBody.set(Strings.fromUTF8ByteArray(bytes));
        });
        return getBody.get();
    }

    @Override
    public int getOrder() {
        return 100;
    }
}