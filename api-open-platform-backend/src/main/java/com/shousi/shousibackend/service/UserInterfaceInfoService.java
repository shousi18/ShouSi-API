package com.shousi.shousibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shousi.shousicommon.model.entity.UserInterfaceInfo;

/**
 * @author shousi
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    boolean invokeCount(long interfaceInfoId, long userId);

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);
}
