package com.shuaigef.springbootinit.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 用户修改请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel(description = "用户修改请求")
@Data
public class UserUpdateRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id 必须大于 0")
    private Long id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 10, message = "用户名长度必须为 4 - 10 位")
    private String username;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
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
     * 角色id
     */
    @ApiModelProperty(value = "角色id", required = true)
    @Min(value = 1, message = "角色id 必须大于 0")
    private Long roleId;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @NotNull(message = "性别不能为空")
    private Integer gender;

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
