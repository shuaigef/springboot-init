package com.shuaigef.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.common.request.DeleteBatchRequest;
import com.shuaigef.springbootinit.common.request.DeleteRequest;
import com.shuaigef.springbootinit.common.response.BaseResponse;
import com.shuaigef.springbootinit.common.utils.ResultUtils;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.model.dto.authority.SysAuthorityAddRequest;
import com.shuaigef.springbootinit.model.dto.authority.SysAuthorityQueryRequest;
import com.shuaigef.springbootinit.model.dto.authority.SysAuthorityUpdateRequest;
import com.shuaigef.springbootinit.model.dto.authority.SysRoleAuthorityUpdateRequest;
import com.shuaigef.springbootinit.model.entity.SysAuthority;
import com.shuaigef.springbootinit.model.entity.SysRole;
import com.shuaigef.springbootinit.model.vo.SysAuthorityVO;
import com.shuaigef.springbootinit.service.SysAuthorityService;
import com.shuaigef.springbootinit.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "权限管理")
@RestController
@RequestMapping("/manage/authority")
@Slf4j
public class SysAuthorityController {

    @Resource
    private SysAuthorityService sysAuthorityService;

    @Resource
    private SysRoleService sysRoleService;

    @ApiOperation("获取指定角色的权限树(含未选中权限, 请根据check属性判断)")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    @GetMapping("/tree")
    public BaseResponse<List<SysAuthority>> getAuthorityTreeByRoleId(
            @RequestParam @ApiParam(value = "角色id", required = true) Long roleId) {
        List<SysAuthority> sysAuthorityList = sysAuthorityService.findTreeByRoleId(roleId);
        return ResultUtils.success(sysAuthorityList);
    }

    @ApiOperation("修改角色权限")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    @PutMapping("/roleBindAuthority")
    public BaseResponse roleBindAuthority(@Valid @RequestBody SysRoleAuthorityUpdateRequest sysRoleAuthorityUpdateRequest) {
        Long roleId = sysRoleAuthorityUpdateRequest.getRoleId();
        Set<Long> authorityIds = sysRoleAuthorityUpdateRequest.getAuthorityIds();

        // 不能修改管理员权限
        if (SecurityConstant.ADMIN_ROLE_ID.compareTo(roleId) == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员权限不可更改");
        }
        // roleId 是否存在
        SysRole sysRole = sysRoleService.getById(roleId);
        if (sysRole == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不存在");
        }
        // 修改的权限是否存在
        Boolean authorityExist = sysAuthorityService.isAuthorityExist(authorityIds);
        if (!authorityExist) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "权限不存在");
        }
        // 角色权限绑定
        sysAuthorityService.roleBindAuthority(roleId, authorityIds);
        return ResultUtils.success("修改角色权限成功");
    }

    @ApiOperation("分页查询权限列表")
    @GetMapping("/list/page")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:authorityManage')")
    public BaseResponse<Page<SysAuthorityVO>> listAuthorityByPage(
            SysAuthorityQueryRequest sysAuthorityQueryRequest) {
        return ResultUtils.success(sysAuthorityService.listAuthorityByPage(sysAuthorityQueryRequest), "查询权限列表成功");
    }

    @ApiOperation("新增权限")
    @PostMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:authorityManage')")
    public BaseResponse<Long> addAuthority(@Valid @RequestBody SysAuthorityAddRequest sysAuthorityAddRequest) {
        return ResultUtils.success(sysAuthorityService.addAuthority(sysAuthorityAddRequest), "新增权限成功");
    }

    @ApiOperation("删除权限")
    @DeleteMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:authorityManage')")
    public BaseResponse<Boolean> deleteAuthority(@Valid @RequestBody DeleteRequest deleteRequest) {
        boolean result = sysAuthorityService.deleteAuthority(deleteRequest.getId());
        return ResultUtils.success(result, "删除权限成功");
    }

    @ApiOperation("批量删除权限")
    @DeleteMapping("/ids")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:authorityManage')")
    public BaseResponse<Boolean> deleteBatchAuthority(@Valid @RequestBody DeleteBatchRequest deleteBatchRequest) {
        boolean result = sysAuthorityService.deleteBatchAuthority(deleteBatchRequest.getIds());
        return ResultUtils.success(result, "批量删除权限成功");
    }

    @ApiOperation("修改权限")
    @PutMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:authorityManage')")
    public BaseResponse<Boolean> updateAuthority(@Valid @RequestBody SysAuthorityUpdateRequest sysAuthorityUpdateRequest) {
        return ResultUtils.success(sysAuthorityService.updateAuthority(sysAuthorityUpdateRequest), "更新权限成功");
    }

    @ApiOperation("重置超级管理员权限")
    @PutMapping("/reset/admin")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:authorityManage')")
    public BaseResponse resetAdminAuthorize() {
        sysAuthorityService.resetAdmin();
        return ResultUtils.success(null, "重置管理员权限成功");
    }



}
