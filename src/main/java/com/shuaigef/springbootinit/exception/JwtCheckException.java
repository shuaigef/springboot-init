package com.shuaigef.springbootinit.exception;

import com.shuaigef.springbootinit.common.code.ErrorCode;

/**
 * JWT 校验异常
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public class JwtCheckException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code = ErrorCode.FORBIDDEN_ERROR.getCode();

    public JwtCheckException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }
}
