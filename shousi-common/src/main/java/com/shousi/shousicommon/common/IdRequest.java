package com.shousi.shousicommon.common;

import lombok.Data;

import java.io.Serializable;

/**
 * Id请求
 *
 * @author shousi
 */
@Data
public class IdRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}