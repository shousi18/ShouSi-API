package com.shousi.shousicommon.service;

import com.shousi.shousicommon.model.vo.UserVO;


/**
 * 用户服务
 *
 * @author yupi
 */
public interface InnerUserService {

    /**
     * 数据库中查询是否已分配给用户密钥（accessKey, secretKey）
     *
     * @param accessKey accessKey
     * @return 用户信息
     */
    UserVO getInvokeUser(String accessKey);
}
