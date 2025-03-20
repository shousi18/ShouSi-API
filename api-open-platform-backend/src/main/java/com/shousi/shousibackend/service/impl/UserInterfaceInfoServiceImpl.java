package com.shousi.shousibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.shousicommon.common.ErrorCode;
import com.shousi.shousicommon.model.entity.UserInterfaceInfo;
import com.shousi.shousibackend.exception.BusinessException;
import com.shousi.shousibackend.mapper.UserInterfaceInfoMapper;
import com.shousi.shousibackend.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
 * @author shousi
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
 * @createDate 2024-12-17 20:20:04
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userInterfaceInfo.getUserId();
        Long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
        Long totalNum = userInterfaceInfo.getTotalNum();
        Integer leftNum = userInterfaceInfo.getLeftNum();
        if (add) {
            if (userId <= 0 || interfaceInfoId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (totalNum < 0 || leftNum < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于0");
        }
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        // 判断参数是否正确
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 构建查询条件
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId)
                .eq("userId", userId)
                .gt("leftNum", 0);
        // 计算更新数据，不拼接sql语句，防止sql注入
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);
        if (userInterfaceInfo != null) {
            userInterfaceInfo.setLeftNum(userInterfaceInfo.getLeftNum() - 1);
            userInterfaceInfo.setTotalNum(userInterfaceInfo.getTotalNum() + 1);
            return this.updateById(userInterfaceInfo);
        }
        return false;
    }
}




