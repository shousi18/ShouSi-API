package com.shousi.shousibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shousi.shousicommon.model.entity.InterfaceInfo;

/**
 * @author 86172
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
