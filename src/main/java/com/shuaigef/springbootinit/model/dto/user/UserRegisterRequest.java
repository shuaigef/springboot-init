package com.shuaigef.springbootinit.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Data
public class UserRegisterRequest implements Serializable {

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private static final long serialVersionUID = 1L;

}
