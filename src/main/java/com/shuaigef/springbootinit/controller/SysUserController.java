package com.shuaigef.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.common.request.DeleteBatchRequest;
import com.shuaigef.springbootinit.common.request.DeleteRequest;
import com.shuaigef.springbootinit.common.response.BaseResponse;
import com.shuaigef.springbootinit.common.utils.JwtUtils;
import com.shuaigef.springbootinit.common.utils.ResultUtils;
import com.shuaigef.springbootinit.common.utils.SecurityUtils;
import com.shuaigef.springbootinit.constant.RedisConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.model.dto.user.SysUserAddRequest;
import com.shuaigef.springbootinit.model.dto.user.SysUserQueryRequest;
import com.shuaigef.springbootinit.model.dto.user.SysUserUpdateBasicInfoRequest;
import com.shuaigef.springbootinit.model.dto.user.SysUserUpdateRequest;
import com.shuaigef.springbootinit.model.entity.SessionUser;
import com.shuaigef.springbootinit.model.entity.SysUser;
import com.shuaigef.springbootinit.model.vo.LoginUserVO;
import com.shuaigef.springbootinit.model.vo.SysUserVO;
import com.shuaigef.springbootinit.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Api(tags = "系统管理-用户管理")
@RestController
@RequestMapping("/manage/user")
@Slf4j
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建用户
     *
     * @param sysUserAddRequest
     * @return
     */
    @ApiOperation("新增用户")
    @PostMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:userManage')")
    public BaseResponse<Long> addUser(@Valid @RequestBody SysUserAddRequest sysUserAddRequest) {
        return ResultUtils.success(sysUserService.addUser(sysUserAddRequest), "新增用户成功");
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @return
     */
    @ApiOperation("删除用户")
    @DeleteMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:userManage')")
    public BaseResponse deleteUser(@Valid @RequestBody DeleteRequest deleteRequest) {
        boolean result = sysUserService.deleteUserById(deleteRequest.getId());
        return ResultUtils.success(result, "删除用户成功");
    }

    @ApiOperation("批量删除用户")
    @DeleteMapping("/ids")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:userManage')")
    public BaseResponse<Boolean> deleteBatchUser(@Valid @RequestBody DeleteBatchRequest deleteBatchRequest) {
        boolean result = sysUserService.deleteBatchUser(deleteBatchRequest.getIds());
        return ResultUtils.success(result, "批量删除用户成功");
    }

    @ApiOperation("根据 id 查询用户")
    @GetMapping("/id")
    public BaseResponse<SysUserVO> getUserById(@RequestParam @ApiParam(value = "id", required = true) Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 必须大于 0");
        }
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该角色不存在");
        }
        return ResultUtils.success(sysUserService.getUserVO(sysUser), "查询角色成功");
    }

    @ApiOperation("分页查询用户列表")
    @GetMapping("/list/page")
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:userManage')")
    public BaseResponse<Page<SysUserVO>> listUserByPage(SysUserQueryRequest sysUserQueryRequest) {
        String username = sysUserQueryRequest.getUsername();
        String nickname = sysUserQueryRequest.getNickname();
        long current = sysUserQueryRequest.getCurrent();
        long pageSize = sysUserQueryRequest.getPageSize();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(username), SysUser::getUsername, username)
                .like(StringUtils.isNotBlank(nickname), SysUser::getNickname, nickname);
        Page<SysUser> userPage = sysUserService.page(new Page<>(current, pageSize), queryWrapper);
        Page<SysUserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<SysUserVO> sysUserVOList = sysUserService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(sysUserVOList);
        return ResultUtils.success(userVOPage, "查询用户列表成功");
    }

    @ApiOperation("修改用户")
    @PutMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:userManage')")
    public BaseResponse<Boolean> updateUser(@Valid @RequestBody SysUserUpdateRequest sysUserUpdateRequest) {
        return ResultUtils.success(sysUserService.updateUser(sysUserUpdateRequest), "修改用户成功");
    }

    @ApiOperation("更新用户基本信息")
    @PutMapping("/basic")
    public BaseResponse<LoginUserVO> updateUserBasicInfo(@Valid @RequestBody SysUserUpdateBasicInfoRequest sysUserUpdateBasicInfoRequest) {
        String username = sysUserUpdateBasicInfoRequest.getUsername();
        String nickname = sysUserUpdateBasicInfoRequest.getNickname();
        String userAvatar = sysUserUpdateBasicInfoRequest.getUserAvatar();
        String userProfile = sysUserUpdateBasicInfoRequest.getUserProfile();
        Integer gender = sysUserUpdateBasicInfoRequest.getGender();

        sysUserService.updateUserBasicInfo(sysUserUpdateBasicInfoRequest);

        // 生成新的jwt
        SessionUser currentUser = SecurityUtils.getCurrentUser();
        BeanUtils.copyProperties(sysUserUpdateBasicInfoRequest, currentUser);
        SessionUser sessionUser = new SessionUser(
                username,
                "",
                new ArrayList<>(),
                currentUser.getUserId(),
                currentUser.getRoleId(),
                nickname, userAvatar, userProfile, gender);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(sessionUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.createToken(authentication);

        // 将 jwt 存入 redis
        stringRedisTemplate.opsForValue().set(
                RedisConstant.LOGIN_USER + currentUser.getUserId(),
                token,
                RedisConstant.LOGIN_USER_TIME,
                RedisConstant.LOGIN_USER_TIME_UNIT);

        return ResultUtils.success(new LoginUserVO(token, sessionUser, null), "更新用户基本信息成功");
    }

}
