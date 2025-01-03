package com.shuaigef.springbootinit.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 验证码业务类型枚举
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public enum VerificationCodeBizEnum {

    EMAIL_REGISTER("邮箱注册", "email_register"),
    EMAIL_UPDATE("邮箱修改", "email_update");

    private final String text;

    private final String value;

    VerificationCodeBizEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static VerificationCodeBizEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (VerificationCodeBizEnum anEnum : VerificationCodeBizEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
