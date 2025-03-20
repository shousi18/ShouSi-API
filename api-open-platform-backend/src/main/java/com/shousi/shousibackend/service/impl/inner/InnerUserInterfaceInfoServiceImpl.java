package com.shousi.shousibackend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shousi.shousicommon.common.ErrorCode;
import com.shousi.shousicommon.model.entity.UserInterfaceInfo;
import com.shousi.shousicommon.service.InnerUserInterfaceInfoService;
import com.shousi.shousibackend.exception.BusinessException;
import com.shousi.shousibackend.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Transactional
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        // 不存在就创建一条记录
        boolean invokeResult;
        if (userInterfaceInfo == null) {
            userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setTotalNum(1L);
            invokeResult = userInterfaceInfoService.save(userInterfaceInfo);
        } else {
            UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("interfaceInfoId", interfaceInfoId);
            updateWrapper.eq("userId", userId);
            updateWrapper.setSql("totalInvokes = totalInvokes + 1");
            invokeResult = userInterfaceInfoService.update(updateWrapper);
        }
        if (!invokeResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用失败");
        }
        return true;
    }
}
