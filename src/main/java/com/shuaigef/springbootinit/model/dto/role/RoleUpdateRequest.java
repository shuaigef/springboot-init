package com.shuaigef.springbootinit.model.dto.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改角色请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel(description = "修改角色请求")
@Data
public class RoleUpdateRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id", required = true)
    @Min(value = 1, message = "id 不能小于 1")
    private Long id;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述", required = true)
    @NotBlank(message = "角色描述不能为空")
    private String roleDesc;

    /**
     * 角色类型
     */
    @ApiModelProperty(value = "角色类型", required = true)
    @NotBlank(message = "角色类型不能为空")
    private String roleType;

    private static final long serialVersionUID = 1L;
}
