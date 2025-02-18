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
 * 用户修改基本信息请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel(description = "用户修改基本信息请求")
@Data
public class SysUserUpdateBasicInfoRequest implements Serializable {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 10, message = "用户名必须为4-10位")
    @Pattern(regexp = RegexUtils.USERNAME_REGEX, message = "用户名错误")
    private String username;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", required = true)
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 10, message = "昵称长度必须为 2 - 10 位")
    private String nickname;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    /**
     * 用户简介
     */
    @ApiModelProperty(value = "用户简介")
    @Size(max = 50, message = "用户简介不能超过50个字符")
    private String userProfile;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @NotNull(message = "性别不能为空")
    private Integer gender;

    private static final long serialVersionUID = 1L;
}
