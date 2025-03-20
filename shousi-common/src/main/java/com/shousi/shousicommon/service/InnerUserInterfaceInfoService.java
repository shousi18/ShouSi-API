package com.shousi.shousicommon.service;

/**
 * @author 86172
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
 * @createDate 2024-12-17 20:20:04
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     *
     * @param interfaceInfoId 接口id
     * @param userId          用户id
     * @return 调用次数
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
