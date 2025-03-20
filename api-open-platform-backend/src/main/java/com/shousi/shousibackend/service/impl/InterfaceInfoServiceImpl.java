package com.shousi.shousibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.shousicommon.common.ErrorCode;
import com.shousi.shousicommon.model.entity.InterfaceInfo;
import com.shousi.shousibackend.exception.BusinessException;
import com.shousi.shousibackend.mapper.InterfaceInfoMapper;
import com.shousi.shousibackend.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author shousi
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        Integer status = interfaceInfo.getStatus();
        String method = interfaceInfo.getMethod();
        Long userId = interfaceInfo.getUserId();
        if (add) {
            if (StringUtils.isAnyBlank(name, description, url, requestHeader, responseHeader, method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }
}




