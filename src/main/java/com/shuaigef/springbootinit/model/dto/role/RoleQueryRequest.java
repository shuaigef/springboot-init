package com.shuaigef.springbootinit.model.dto.role;

import com.shuaigef.springbootinit.common.request.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 查询角色请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel("查询角色请求")
@Data
public class RoleQueryRequest extends PageRequest implements Serializable {

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
     * 角色类型
     */
    @ApiModelProperty(value = "角色类型")
    private String roleType;

    private static final long serialVersionUID = 1L;

}
