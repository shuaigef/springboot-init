package com.shuaigef.springbootinit.controller;

import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.common.response.BaseResponse;
import com.shuaigef.springbootinit.common.utils.ResultUtils;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.model.dto.authority.AuthorityUpdateRequest;
import com.shuaigef.springbootinit.model.entity.Authority;
import com.shuaigef.springbootinit.model.entity.Role;
import com.shuaigef.springbootinit.model.vo.AuthorityVO;
import com.shuaigef.springbootinit.service.AuthorityService;
import com.shuaigef.springbootinit.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "权限管理")
@RestController
@RequestMapping("/manage/authority")
@Slf4j
public class AuthorityController {

    @Resource
    private AuthorityService authorityService;

    @Resource
    private RoleService roleService;

    @ApiOperation("获取指定角色的权限树(含未选中权限, 请根据check属性判断)")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    @GetMapping
    public BaseResponse<List<AuthorityVO>> getAuthorityTreeByRoleId(
            @RequestParam @ApiParam(value = "角色id", required = true) Long roleId) {
        List<Authority> authorityList = authorityService.findMenuTree(roleId);
        return ResultUtils.success(authorityService.getAuthorityVO(authorityList));
    }

    @ApiOperation("修改角色权限")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:roleManage')")
    @PutMapping
    public BaseResponse roleBindAuthority(@Valid @RequestBody AuthorityUpdateRequest authorityUpdateRequest) {
        Long roleId = authorityUpdateRequest.getRoleId();
        Set<Long> authorityIds = authorityUpdateRequest.getAuthorityIds();

        // 不能修改管理员权限
        if (SecurityConstant.ADMIN_ROLE_ID.compareTo(roleId) == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员权限不可更改");
        }
        // roleId 是否存在
        Role role = roleService.getById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不存在");
        }
        // 修改的权限是否存在
        Boolean authorityExist = authorityService.isAuthorityExist(authorityIds);
        if (!authorityExist) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "权限不存在");
        }
        // 角色权限绑定
        authorityService.roleBindAuthority(roleId, authorityIds);
        return ResultUtils.success("修改角色权限成功");
    }

}
