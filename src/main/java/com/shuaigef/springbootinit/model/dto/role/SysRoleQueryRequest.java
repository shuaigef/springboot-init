package com.shuaigef.springbootinit.model.dto.role;

import com.shuaigef.springbootinit.common.request.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色查询请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel("角色查询请求")
@Data
public class SysRoleQueryRequest extends PageRequest implements Serializable {

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    private static final long serialVersionUID = 1L;

}
