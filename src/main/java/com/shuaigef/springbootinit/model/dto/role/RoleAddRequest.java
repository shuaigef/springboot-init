package com.shuaigef.springbootinit.model.dto.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 角色新增请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel("角色新增请求")
@Data
public class RoleAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;

}
