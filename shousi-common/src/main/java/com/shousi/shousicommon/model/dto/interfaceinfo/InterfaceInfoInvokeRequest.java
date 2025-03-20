package com.shousi.shousicommon.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    private static final long serialVersionUID = -8385309861501955555L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;

    /**
     * 请求参数
     */
    private List<Field> requestParams;

    @Data
    public static class Field {
        private String fieldName;
        private String value;
    }
}