package com.shuaigef.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.common.request.DeleteBatchRequest;
import com.shuaigef.springbootinit.common.request.DeleteRequest;
import com.shuaigef.springbootinit.common.response.BaseResponse;
import com.shuaigef.springbootinit.common.utils.ResultUtils;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.model.dto.role.SysRoleAddRequest;
import com.shuaigef.springbootinit.model.dto.role.SysRoleQueryRequest;
import com.shuaigef.springbootinit.model.dto.role.SysRoleUpdateRequest;
import com.shuaigef.springbootinit.model.entity.SysRole;
import com.shuaigef.springbootinit.model.vo.SysRoleVO;
import com.shuaigef.springbootinit.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
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
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    public BaseResponse<Long> addRole(@Valid @RequestBody SysRoleAddRequest sysRoleAddRequest) {
        long roleId = sysRoleService.addRole(sysRoleAddRequest);
        return ResultUtils.success(roleId, "新增角色成功");
    }

    @ApiOperation("修改角色")
    @PutMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    public BaseResponse<Boolean> addRole(@Valid @RequestBody SysRoleUpdateRequest sysRoleUpdateRequest) {
        boolean result = sysRoleService.updateRole(sysRoleUpdateRequest);
        return ResultUtils.success(result, "修改角色成功");
    }

    @ApiOperation("删除角色")
    @DeleteMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    public BaseResponse<Boolean> deleteRole(@Valid @RequestBody DeleteRequest deleteRequest) {
        boolean result = sysRoleService.deleteRole(deleteRequest.getId());
        return ResultUtils.success(result, "删除角色成功");
    }

    @ApiOperation("批量删除角色")
    @DeleteMapping("/ids")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    public BaseResponse<Boolean> deleteBatchRole(@Valid @RequestBody DeleteBatchRequest deleteBatchRequest) {
        boolean result = sysRoleService.deleteBatchRole(deleteBatchRequest.getIds());
        return ResultUtils.success(result, "批量删除角色成功");
    }

    @ApiOperation("根据 id 查询角色")
    @GetMapping("/id")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    public BaseResponse<SysRoleVO> getRoleById(@RequestParam Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 必须大于 0");
        }
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该角色不存在");
        }
        return ResultUtils.success(sysRoleService.getRoleVO(sysRole), "查询角色成功");
    }

    @ApiOperation("查询角色列表-选择角色使用")
    @GetMapping("/list")
    public BaseResponse<List<SysRoleVO>> listRole() {
        List<SysRole> sysRoleList = sysRoleService.list();
        return ResultUtils.success(sysRoleService.getRoleVO(sysRoleList), "查询角色列表成功");
    }

    @ApiOperation("分页查询角色列表")
    @GetMapping("/list/page")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    public BaseResponse<Page<SysRoleVO>> listRoleByPage(SysRoleQueryRequest sysRoleQueryRequest) {
        return ResultUtils.success(sysRoleService.listRoleByPage(sysRoleQueryRequest), "查询角色列表成功");
    }

}
