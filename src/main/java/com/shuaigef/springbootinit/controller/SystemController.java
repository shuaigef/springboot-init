package com.shuaigef.springbootinit.controller;

import com.shuaigef.springbootinit.common.response.BaseResponse;
import com.shuaigef.springbootinit.common.utils.JwtUtils;
import com.shuaigef.springbootinit.common.utils.ResultUtils;
import com.shuaigef.springbootinit.common.utils.SecurityUtils;
import com.shuaigef.springbootinit.constant.RedisConstant;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.model.dto.user.UserLoginRequest;
import com.shuaigef.springbootinit.model.entity.Authority;
import com.shuaigef.springbootinit.model.entity.SessionUser;
import com.shuaigef.springbootinit.model.vo.LoginUserVO;
import com.shuaigef.springbootinit.service.AuthorityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Api(tags = "系统接口")
@RestController
@RequestMapping("/system")
@Slf4j
public class SystemController {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private AuthorityService authorityService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 登录接口
     *
     * @param userLoginRequest
     * @return
     */
    @ApiOperation("登录接口-获取token")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginUserVO>> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginRequest.getUsernameOrEmail(),
                        userLoginRequest.getPassword());
        Authentication authentication = this.authenticationManager
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SecurityConstant.TOKEN_HEADER, "Bearer " + jwt);
        // 获取权限树
        long currentUserId = SecurityUtils.getCurrentUserId();
        List<Authority> authorityList = authorityService.findMenuTree(currentUserId);
        SessionUser sessionUser =
                (SessionUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 将 jwt 存入 redis
        stringRedisTemplate.opsForValue().set(
                RedisConstant.LOGIN_USER + currentUserId,
                jwt,
                RedisConstant.LOGIN_USER_TIME,
                RedisConstant.LOGIN_USER_TIME_UNIT);

        return new ResponseEntity<>(
                ResultUtils.success(new LoginUserVO(jwt, sessionUser, authorityList)),
                httpHeaders, HttpStatus.OK);
    }

    @ApiOperation("登出接口")
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout() {
        // 从 redis 移除 jwt
        long currentUserId = SecurityUtils.getCurrentUserId();
        Boolean result = stringRedisTemplate.delete(RedisConstant.LOGIN_USER + currentUserId);

        return ResultUtils.success(result, "登出成功");
    }

}
