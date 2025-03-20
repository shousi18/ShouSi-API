package com.shousi.shousibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shousi.shousicommon.model.entity.UserInterfaceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 86172
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
* @createDate 2024-12-17 20:20:04
* @Entity com.yupi.project.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    /**
     * 查询最热门的接口信息
     * @param limit 接口数量
     * @return 接口信息
     */
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(@Param("limit") int limit);
}




