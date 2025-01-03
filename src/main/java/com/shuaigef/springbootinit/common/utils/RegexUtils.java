package com.shuaigef.springbootinit.common.utils;

import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 **/
public class RegexUtils {

    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]{4,10}$";

    public static final String PASSWORD_REGEX = "^[\\w!@#$%^&*()+-=.]{6,18}$";

    public static boolean isEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

}
