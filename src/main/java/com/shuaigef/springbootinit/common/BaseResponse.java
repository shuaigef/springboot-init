package com.shuaigef.springbootinit.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 通用返回类
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 *
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
