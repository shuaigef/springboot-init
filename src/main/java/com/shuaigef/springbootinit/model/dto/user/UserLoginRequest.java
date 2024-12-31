package com.shuaigef.springbootinit.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 用户登录请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel(description = "用户登录请求")
@Data
public class UserLoginRequest implements Serializable {

    @ApiModelProperty(value = "用户名或邮箱", required = true)
    @NotBlank(message = "用户名或邮箱不能为空")
    private String usernameOrEmail;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码必须为6-20位")
    private String password;

    private static final long serialVersionUID = 1L;

}
