package com.shuaigef.springbootinit.model.vo;

import com.shuaigef.springbootinit.model.entity.SessionUser;
import com.shuaigef.springbootinit.model.entity.SysAuthority;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录用户视图
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel("登录用户视图")
@Data
@AllArgsConstructor
public class LoginUserVO implements Serializable {

    /**
     * jwt令牌
     */
    @ApiModelProperty(value = "jwt令牌")
    private String token;

    /**
     * 用户信息
     */
    @ApiModelProperty(value = "用户信息")
    private SessionUser userInfo;

    /**
     * 权限列表
     */
    @ApiModelProperty(value = "权限列表")
    private List<SysAuthority> sysAuthorityList;

    private static final long serialVersionUID = 1L;

}
