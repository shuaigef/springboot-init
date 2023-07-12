package com.shuaigef.springbootinit.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Data
public class UserLoginRequest implements Serializable {

    private String userAccount;

    private String userPassword;

    private static final long serialVersionUID = 1L;

}
