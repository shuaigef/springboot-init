package com.shuaigef.springbootinit.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shuaigef.springbootinit.model.entity.AuthorityMeta;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 权限视图
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel(description = "权限视图")
@Data
public class AuthorityVO implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 权限标识符
     */
    @ApiModelProperty(value = "权限标识符")
    private String code;

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "权限名称")
    private String name;

    /**
     * 菜单顺序
     */
    @ApiModelProperty(value = "菜单顺序")
    private Integer orderNo;

    /**
     * 父节点id
     */
    @ApiModelProperty(value = "父节点id")
    private Long parentId;

    /**
     * 权限类型 menu/button
     */
    @ApiModelProperty(value = "权限类型 menu/button")
    private String authorityType;

    /**
     * 一级菜单跳转地址
     */
    @ApiModelProperty(value = "一级菜单跳转地址")
    private String redirect;

    /**
     * 路由路径
     */
    @ApiModelProperty(value = "路由路径")
    private String path;

    /**
     * 是否隐藏路由菜单(0 - 否，1 - 是)
     */
    @ApiModelProperty(value = "是否隐藏路由菜单(0 - 否，1 - 是)")
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
    private String component;

    /**
     * 组件名称
     */
    @ApiModelProperty(value = "组件名称")
    private String componentName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 附加信息
     */
    @ApiModelProperty(value = "附加信息")
    private AuthorityMeta meta;

    /**
     * 子节点集合
     */
    @ApiModelProperty(value = "子节点集合")
    private List<AuthorityVO> children;

    /**
     * 此节点是否勾选
     */
    @ApiModelProperty(value = "此节点是否勾选")
    private boolean check = false;

    /**
     * 此节点是否有子节点
     */
    @ApiModelProperty(value = "此节点是否有子节点")
    private boolean node = false;

    private static final long serialVersionUID = 1L;
}
