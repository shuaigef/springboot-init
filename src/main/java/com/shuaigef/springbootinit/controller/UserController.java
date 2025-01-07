package com.shuaigef.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.common.request.DeleteRequest;
import com.shuaigef.springbootinit.common.response.BaseResponse;
import com.shuaigef.springbootinit.common.utils.JwtUtils;
import com.shuaigef.springbootinit.common.utils.ResultUtils;
import com.shuaigef.springbootinit.common.utils.SecurityUtils;
import com.shuaigef.springbootinit.constant.RedisConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.model.dto.user.UserAddRequest;
import com.shuaigef.springbootinit.model.dto.user.UserQueryRequest;
import com.shuaigef.springbootinit.model.dto.user.UserUpdateBasicInfoRequest;
import com.shuaigef.springbootinit.model.entity.SessionUser;
import com.shuaigef.springbootinit.model.entity.User;
import com.shuaigef.springbootinit.model.vo.LoginUserVO;
import com.shuaigef.springbootinit.model.vo.UserVO;
import com.shuaigef.springbootinit.service.UserService;
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
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @return
     */
    @ApiOperation("新增用户")
    @PostMapping
    @PreAuthorize("@roleCheckService.hasPermission('systemManage:userManage')")
    public BaseResponse<Long> addUser(@Valid @RequestBody UserAddRequest userAddRequest) {
        return ResultUtils.success(userService.addUser(userAddRequest), "新增用户成功");
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
        boolean result = userService.deleteUserById(deleteRequest.getId());
        return ResultUtils.success(result, "删除用户成功");
    }

    @ApiOperation("根据 id 查询用户")
    @GetMapping("/id")
    public BaseResponse<UserVO> getUserById(@RequestParam @ApiParam(value = "id", required = true) Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 必须大于 0");
        }
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该角色不存在");
        }
        return ResultUtils.success(userService.getUserVO(user), "查询角色成功");
    }

    @ApiOperation("分页查询用户列表")
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest) {
        String username = userQueryRequest.getUsername();
        String nickname = userQueryRequest.getNickname();
        Long roleId = userQueryRequest.getRoleId();
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(username), User::getUsername, username)
                .like(StringUtils.isNotBlank(nickname), User::getNickname, nickname)
                .eq(roleId != null, User::getRoleId, roleId);
        Page<User> userPage = userService.page(new Page<>(current, pageSize), queryWrapper);
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage, "查询角色列表成功");
    }

    @ApiOperation("更新用户基本信息")
    @PutMapping("/basic")
    public BaseResponse<LoginUserVO> updateUserBasicInfo(@Valid @RequestBody UserUpdateBasicInfoRequest userUpdateBasicInfoRequest) {
        String username = userUpdateBasicInfoRequest.getUsername();
        String nickname = userUpdateBasicInfoRequest.getNickname();
        String userAvatar = userUpdateBasicInfoRequest.getUserAvatar();
        String userProfile = userUpdateBasicInfoRequest.getUserProfile();
        Integer gender = userUpdateBasicInfoRequest.getGender();

        userService.updateUserBasicInfo(userUpdateBasicInfoRequest);

        // 生成新的jwt
        SessionUser currentUser = SecurityUtils.getCurrentUser();
        BeanUtils.copyProperties(userUpdateBasicInfoRequest, currentUser);
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
