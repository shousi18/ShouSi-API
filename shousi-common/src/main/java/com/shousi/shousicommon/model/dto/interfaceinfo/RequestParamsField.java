package com.shousi.shousicommon.model.dto.interfaceinfo;

import lombok.Data;

/**
 * @author ：shousi
 * <p>
 * 请求参数
 */
@Data
public class RequestParamsField {

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

    /**
     * 是否必填
     */
    private String required;
}
