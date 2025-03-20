package com.shousi.shousibackend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shousi.shousicommon.common.ErrorCode;
import com.shousi.shousicommon.model.entity.InterfaceInfo;
import com.shousi.shousicommon.service.InnerInterfaceInfoService;
import com.shousi.shousibackend.exception.BusinessException;
import com.shousi.shousibackend.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        if (StringUtils.isAnyBlank(path, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 如果携带参数，则去掉后面的参数
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }

        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("url", path);
        queryWrapper.eq("method", method);
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(queryWrapper);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        return interfaceInfo;
    }
}
