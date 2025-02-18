package com.shuaigef.springbootinit.model.dto.user;

import com.shuaigef.springbootinit.common.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 用户新增请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel(description = "用户新增请求")
@Data
public class SysUserAddRequest implements Serializable {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 10, message = "用户名必须为4-10位")
    @Pattern(regexp = RegexUtils.USERNAME_REGEX, message = "用户名错误")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = RegexUtils.PASSWORD_REGEX, message = "密码错误")
    private String password;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", required = true)
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 10, message = "昵称长度必须为 2 - 10 位")
    private String nickname;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @NotNull(message = "性别不能为空")
    private Integer gender;

    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    @NotNull(message = "角色不能为空")
    private Long roleId;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    private static final long serialVersionUID = 1L;
}
