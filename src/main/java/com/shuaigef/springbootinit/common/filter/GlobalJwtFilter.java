package com.shuaigef.springbootinit.common.filter;

import com.shuaigef.springbootinit.common.utils.JwtUtils;
import com.shuaigef.springbootinit.common.utils.SecurityUtils;
import com.shuaigef.springbootinit.constant.RedisConstant;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.JwtCheckException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 全局 jwt 过滤器，用于校验当前请求是否需要进行 jwt 校验，以及 jwt 是否正确
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Slf4j
public class GlobalJwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final StringRedisTemplate stringRedisTemplate;

    public GlobalJwtFilter(JwtUtils jwtUtils, StringRedisTemplate stringRedisTemplate) {
        this.jwtUtils = jwtUtils;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {
        String requestURI = httpServletRequest.getRequestURI();
        if (!checkIgnoreURI(requestURI)) {
            try {
                String jwt = resolveToken(httpServletRequest);
                if (StringUtils.hasText(jwt)) {
                    // 验证 jwt 是否合法
                    long currentUserId = jwtUtils.validateToken(jwt);
                    // 校验 JWT 是否存在于 Redis 中
                    isJwtValidInRedis(jwt, currentUserId);
                    Authentication authentication = jwtUtils.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (RuntimeException e) {
                httpServletRequest.setAttribute(SecurityConstant.ERROR_MESSAGE, e);
                httpServletRequest.getRequestDispatcher("/handler/tokenError")
                        .forward(httpServletRequest, httpServletResponse);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * 获取 jwt 值
     *
     * @param request
     * @return
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConstant.TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken
                .startsWith(SecurityConstant.TOKEN_PREFIX)) {
            return bearerToken.substring(SecurityConstant.TOKEN_PREFIX.length());
        }
        throw new JwtCheckException("传入的令牌为空或格式错误");
    }

    /**
     * 当前请求是否需要被权限校验
     *
     * @param requestURI
     * @return
     */
    private boolean checkIgnoreURI(String requestURI) {
        return SecurityConstant.IGNORE_URI_PREFIX.stream().anyMatch(requestURI::startsWith);
    }

    /**
     * 校验 JWT 在 redis 中是否有效
     *
     * @return true 表示有效，false 表示无效
     */
    private void isJwtValidInRedis(String jwt, long currentUserId) {
        String key = RedisConstant.LOGIN_USER + currentUserId;
        String redisJwt = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(redisJwt) || !jwt.equals(redisJwt)) {
            throw new JwtCheckException("无效的JWT令牌！");
        }
    }
}
