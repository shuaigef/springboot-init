package com.shuaigef.springbootinit.model.dto.user;

import com.shuaigef.springbootinit.common.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel(description = "用户注册请求")
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 10, message = "用户名必须为4-10位")
    @Pattern(regexp = RegexUtils.USERNAME_REGEX, message = "用户名错误")
    private String username;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = RegexUtils.EMAIL_REGEX, message = "邮箱错误")
    private String email;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = RegexUtils.PASSWORD_REGEX, message = "密码错误")
    private String password;

    /**
     * 确认密码
     */
    @ApiModelProperty(value = "确认密码", required = true)
    @NotBlank(message = "确认密码不能为空")
    @Pattern(regexp = RegexUtils.PASSWORD_REGEX, message = "确认密码错误")
    private String checkPassword;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;

    private static final long serialVersionUID = 1L;

}
