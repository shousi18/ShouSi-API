package com.shousi.shousicommon.model.vo;

import com.shousi.shousicommon.model.entity.InterfaceInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息封装类
 */
@Data
public class InterfaceInfoVO extends InterfaceInfo implements Serializable {

    /**
     * 调用次数
     */
    private Long totalNum;

    private static final long serialVersionUID = 1L;
}
