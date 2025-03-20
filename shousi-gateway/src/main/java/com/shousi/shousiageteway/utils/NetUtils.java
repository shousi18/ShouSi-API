package com.shousi.shousiageteway.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * @author shousi18
 *
 * 获取ip
 */
public class NetUtils {

    /**
     * 未知ip
     */
    private static final String IP_UNKNOWN = "unknown";

    /**
     * 本地ip
     */
    private static final String IP_LOCAL = "127.0.0.1";

    /**
     * 获取客户端真实ip
     *
     * @param request request
     * @return 返回ip
     */
    public static String getIp(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        // 获取标准代理头中的ip，记录客户端到服务器的所有经过IP。
        String ipAddress = headers.getFirst("x-forwarded-for");
        // 如果为空，获取Apache代理服务器的自定义头。
        if (ipAddress == null || ipAddress.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst("Proxy-Client-IP");
        }
        // 如果为空，获取WebLogic代理服务器的自定义头。
        if (ipAddress == null || ipAddress.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst("WL-Proxy-Client-IP");
        }
        // 如果为空，从远程的请求地址获取
        if (ipAddress == null || ipAddress.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = Optional.ofNullable(request.getRemoteAddress())
                    .map(address -> address.getAddress().getHostAddress())
                    .orElse("");
            // 处理本地回环地址
            if (IP_LOCAL.equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                try {
                    // 获取实际的内网ip
                    InetAddress inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    // ignore
                }
            }
        }
        // 直接按逗号分割，如果有逗号，则有多个ip
        if (ipAddress.contains(",")) {
            int index = ipAddress.indexOf(",");
            if (index > 0) {
                // 获取第一个ip
                ipAddress = ipAddress.substring(0, index);
            }
        }
        return ipAddress;
    }
}
