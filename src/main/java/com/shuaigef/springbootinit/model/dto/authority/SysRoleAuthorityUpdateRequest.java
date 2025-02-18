package com.shuaigef.springbootinit.model.dto.authority;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色权限修改请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel(description = "角色权限修改请求")
@Data
public class SysRoleAuthorityUpdateRequest implements Serializable {

    @ApiModelProperty(value = "角色id", required = true)
    @NotNull(message = "角色id不能为空")
    Long roleId;

    @ApiModelProperty(value = "权限id列表")
    Set<Long> authorityIds;

    private static final long serialVersionUID = 1L;
}
