package com.shuaigef.springbootinit.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.shuaigef.springbootinit.constant.UserConstant.ADMIN_ROLE;
import static com.shuaigef.springbootinit.constant.UserConstant.DEFAULT_ROLE;

/**
 * SaToken 路由拦截器
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 登录校验和角色校验
            SaRouter.match("/**")
                .notMatch("/user/login")
                .notMatch("/user/register")
                .check( r -> {
                    StpUtil.checkLogin();
                    StpUtil.checkRoleOr(DEFAULT_ROLE, ADMIN_ROLE);
                });
        })).addPathPatterns("/**");
    }
}

