package com.shuaigef.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.common.request.DeleteRequest;
import com.shuaigef.springbootinit.common.response.BaseResponse;
import com.shuaigef.springbootinit.common.utils.ResultUtils;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.model.dto.role.RoleAddRequest;
import com.shuaigef.springbootinit.model.dto.role.RoleQueryRequest;
import com.shuaigef.springbootinit.model.dto.role.RoleUpdateRequest;
import com.shuaigef.springbootinit.model.entity.Role;
import com.shuaigef.springbootinit.model.vo.RoleVO;
import com.shuaigef.springbootinit.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "系统管理-角色管理")
@RestController
@RequestMapping("/manage/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    public BaseResponse addRole(@Valid @RequestBody RoleAddRequest roleAddRequest) {
        long roleId = roleService.addRole(roleAddRequest);
        return ResultUtils.success(roleId, "新增角色成功");
    }

    @ApiOperation("修改角色")
    @PutMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    public BaseResponse addRole(@Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
        boolean result = roleService.updateRole(roleUpdateRequest);
        return ResultUtils.success(result, "修改角色成功");
    }

    @ApiOperation("删除角色")
    @DeleteMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    public BaseResponse deleteRole(@Valid @RequestBody DeleteRequest deleteRequest) {
        boolean result = roleService.deleteRoleById(deleteRequest.getId());
        return ResultUtils.success(result, "删除角色成功");
    }

    @ApiOperation("根据 id 查询角色")
    @GetMapping("/id")
    public BaseResponse<RoleVO> getRoleById(@RequestParam Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 必须大于 0");
        }
        Role role = roleService.getById(id);
        if (role == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该角色不存在");
        }
        return ResultUtils.success(roleService.getRoleVO(role), "查询角色成功");
    }

    @ApiOperation("查询角色列表")
    @GetMapping("/list")
    public BaseResponse<List<RoleVO>> listRole() {
        List<Role> roleList = roleService.list();
        return ResultUtils.success(roleService.getRoleVO(roleList), "查询角色列表成功");
    }

    @ApiOperation("分页查询角色列表")
    @GetMapping("/list/page")
    public BaseResponse<Page<RoleVO>> listRoleByPage(RoleQueryRequest roleQueryRequest) {
        String roleName = roleQueryRequest.getRoleName();
        String roleType = roleQueryRequest.getRoleType();
        long current = roleQueryRequest.getCurrent();
        long pageSize = roleQueryRequest.getPageSize();
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(roleName), Role::getRoleName, roleName)
                .eq(StringUtils.isNotBlank(roleType), Role::getRoleType, roleType);
        Page<Role> rolePage = roleService.page(new Page<>(current, pageSize), queryWrapper);
        Page<RoleVO> roleVOPage = new Page<>(current, pageSize, rolePage.getTotal());
        List<RoleVO> roleVOList = roleService.getRoleVO(rolePage.getRecords());
        roleVOPage.setRecords(roleVOList);
        return ResultUtils.success(roleVOPage, "查询角色列表成功");
    }

}
