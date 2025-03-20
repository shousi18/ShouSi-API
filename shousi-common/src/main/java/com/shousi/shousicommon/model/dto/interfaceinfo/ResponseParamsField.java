package com.shousi.shousicommon.model.dto.interfaceinfo;

import lombok.Data;

/**
 * @author shoushui
 *
 * 响应参数字段
 */
@Data
public class ResponseParamsField {

    private String id;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段描述
     */
    private String desc;
}