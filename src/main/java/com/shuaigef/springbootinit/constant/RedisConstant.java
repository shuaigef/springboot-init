package com.shuaigef.springbootinit.constant;

import java.util.concurrent.TimeUnit;

/**
 * redis 常量
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface RedisConstant {

    /**
     * 用户登录 key
     */
    String LOGIN_USER = "login_user:";

    /**
     * 用户登录有效期 1 天
     */
    Long LOGIN_USER_TIME = 1l;
    TimeUnit LOGIN_USER_TIME_UNIT = TimeUnit.DAYS;

}
