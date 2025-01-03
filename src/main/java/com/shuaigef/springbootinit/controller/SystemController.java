package com.shuaigef.springbootinit.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailUtil;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.common.response.BaseResponse;
import com.shuaigef.springbootinit.common.utils.JwtUtils;
import com.shuaigef.springbootinit.common.utils.RegexUtils;
import com.shuaigef.springbootinit.common.utils.ResultUtils;
import com.shuaigef.springbootinit.common.utils.SecurityUtils;
import com.shuaigef.springbootinit.constant.RedisConstant;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.model.dto.user.UserLoginRequest;
import com.shuaigef.springbootinit.model.dto.user.UserRegisterRequest;
import com.shuaigef.springbootinit.model.dto.user.VerificationCodeSendRequest;
import com.shuaigef.springbootinit.model.entity.Authority;
import com.shuaigef.springbootinit.model.entity.SessionUser;
import com.shuaigef.springbootinit.model.enums.VerificationCodeBizEnum;
import com.shuaigef.springbootinit.model.vo.LoginUserVO;
import com.shuaigef.springbootinit.service.AuthorityService;
import com.shuaigef.springbootinit.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private UserService userService;

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

    @ApiOperation("注册接口")
    @PostMapping("/register")
    public BaseResponse<Boolean> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        return ResultUtils.success(userService.register(userRegisterRequest), "注册成功");
    }

    @ApiOperation("验证码发送接口")
    @PostMapping("/send")
    public BaseResponse sendVerificationCode(@Valid @RequestBody VerificationCodeSendRequest verificationCodeSendRequest) {
        String target = verificationCodeSendRequest.getTarget();
        String biz = verificationCodeSendRequest.getBiz();
        VerificationCodeBizEnum verificationCodeBizEnum = VerificationCodeBizEnum.getEnumByValue(biz);
        if (verificationCodeBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (verificationCodeBizEnum.equals(VerificationCodeBizEnum.EMAIL_REGISTER) || verificationCodeBizEnum.equals(VerificationCodeBizEnum.EMAIL_UPDATE)) {
            if (!RegexUtils.isEmail(target)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱错误");
            }
            // 需要一分钟才能再次发送
            if (stringRedisTemplate.hasKey(biz + RedisConstant.INTERVAL_KEY + target)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请一分钟后重试");
            }

            // 验证码 - 随机6位数数字验证码
            String verificationCode = RandomUtil.randomNumbers(6);

            try {
                // 发送邮件
                MailUtil.send(target, "SpringBoot 初始化项目", "【SpringBoot 初始化项目】验证码 " + verificationCode + " 用于" + verificationCodeBizEnum.getText() + "验证，5分钟内有效，请勿泄漏和转发。", false);
                // 存入 redis
                stringRedisTemplate.opsForValue().set(
                        biz + RedisConstant.VERIFICATION_CODE_KEY + target,
                        verificationCode,
                        5l,
                        TimeUnit.MINUTES);
                stringRedisTemplate.opsForValue().set(
                        biz + RedisConstant.INTERVAL_KEY + target,
                        "true",
                        1l,
                        TimeUnit.MINUTES);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码发送失败");
            }
        }

        return ResultUtils.success("验证码发送成功");
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
