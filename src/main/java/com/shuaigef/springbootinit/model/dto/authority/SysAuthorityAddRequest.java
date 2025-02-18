package com.shuaigef.springbootinit.model.dto.authority;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 权限新增请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 **/
@ApiModel(description = "权限新增请求")
@Data
public class SysAuthorityAddRequest implements Serializable {

    /**
     * 权限标识符
     */
    @ApiModelProperty(value = "权限标识符", required = true)
    @NotBlank(message = "权限标识符不能为空")
    private String code;

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "权限名称", required = true)
    @NotBlank(message = "权限名称不能为空")
    private String name;

    /**
     * 菜单顺序
     */
    @ApiModelProperty(value = "菜单顺序", required = true)
    @NotNull(message = "菜单顺序不能为空")
    @Min(value = 1, message = "排序必须大于 0")
    private Integer orderNo;

    /**
     * 父节点id
     */
    @ApiModelProperty(value = "父节点id")
    @NotNull(message = "父节点 id 不能为空")
    @Min(value = 0, message = "父节点 id 必须大于等于 0")
    private Long parentId;

    /**
     * 权限类型 menu/button
     */
    @ApiModelProperty(value = "权限类型 menu/button", required = true)
    @NotBlank(message = "权限类型不能为空")
    private String authorityType;

    /**
     * 一级菜单跳转地址
     */
    @ApiModelProperty(value = "一级菜单跳转地址")
    private String redirect;

    /**
     * 路由路径
     */
    @ApiModelProperty(value = "路由路径", required = true)
    @NotBlank(message = "路由路径不能为空")
    private String path;

    /**
     * 是否隐藏路由菜单(0 - 否，1 - 是)
     */
    @ApiModelProperty(value = "是否隐藏路由菜单(0 - 否，1 - 是)", required = true)
    @NotNull(message = "是否隐藏路由菜单不能为空")
    private Integer hidden;

    /**
     * 菜单图标
     */
    @ApiModelProperty(value = "菜单图标")
    private String menuIcon;

    /**
     * 组件
     */
    @ApiModelProperty(value = "组件")
    @NotBlank(message = "组件不能为空")
    private String component;

    /**
     * 组件名称
     */
    @ApiModelProperty(value = "组件名称")
    @NotBlank(message = "组件名称不能为空")
    private String componentName;

    private static final long serialVersionUID = 1L;
}
