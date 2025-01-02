package com.shuaigef.springbootinit.common.utils;

import com.shuaigef.springbootinit.exception.JwtCheckException;
import com.shuaigef.springbootinit.model.entity.SessionUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * jwt 工具类
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Component
@Slf4j
public class JwtUtils {

    private static final String USERNAME = "username";
    private static final String NICKNAME = "nickname";
    private static final String ROLE_ID = "roleId";
    private static final String USER_AVATAR = "userAvatar";
    private static final String USER_PROFILE = "userProfile";
    private static final String GENDER = "gender";

    // 7天，毫秒为单位
    // private static final long EXPIRE = 604800000l;
    // 1天
    private static final long EXPIRE = 86400000l;

    private static final String JWT_SECRET = "shuaigef";

    /**
     * 生成密钥
     *
     * @return
     */
    public static SecretKey generalKey() {
        // 本地的密码解码
//    byte[] encodedKey = Base64.decodeBase64(JWT_SECRET);
        byte[] encodedKey = Base64.getDecoder().decode(JWT_SECRET);
        // 根据给定的字节数组使用AES加密算法构造一个密钥
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 生成token 如果更改sessionUser 需要一起改了生成和解析token的方法
     */
    public String createToken(Authentication authentication) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + EXPIRE);

        SessionUser sessionUser = (SessionUser) authentication.getPrincipal();

        return Jwts.builder()
                // 用户ID
                .setSubject(sessionUser.getUserId() + "")
                // 自定义信息 对应sessionUser中的扩展信息
                .claim(USERNAME, sessionUser.getUsername())
                .claim(ROLE_ID, sessionUser.getRoleId())
                .claim(NICKNAME, sessionUser.getNickname())
                .claim(USER_AVATAR, sessionUser.getUserAvatar())
                .claim(USER_PROFILE, sessionUser.getUserProfile())
                .claim(GENDER, sessionUser.getGender())
                // 签名信息
                .signWith(SignatureAlgorithm.HS512, generalKey()).setExpiration(validity)
                // 到期时间
                .compact();
    }

    /**
     * 解析token
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(generalKey()).parseClaimsJws(token).getBody();

        String username = claims.get(USERNAME) + "";
        Long roleId = Long.valueOf(claims.get(ROLE_ID) + "");
        String nickname = claims.get(NICKNAME) + "";
        String userAvatar = claims.get(USER_AVATAR) + "";
        String userProfile = claims.get(USER_PROFILE) + "";
        Integer gender = Integer.valueOf(claims.get(GENDER) + "");
        long userId = Long.parseLong(claims.getSubject());
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_" + username.toUpperCase()));
        SessionUser principal = new SessionUser(username, "", authorityList, userId, roleId,
                nickname, userAvatar, userProfile, gender);
        return new UsernamePasswordAuthenticationToken(principal, token, authorityList);
    }

    /**
     * 校验token
     *
     * @param authToken
     * @return 返回当前登录用户id
     */
    public long validateToken(String authToken) {
        try {
            Claims claims = Jwts.parser().setSigningKey(generalKey()).parseClaimsJws(authToken).getBody();
            return Long.parseLong(claims.getSubject());
        } catch (MalformedJwtException | SignatureException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
            throw new JwtCheckException("无效的JWT令牌！");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
            throw new JwtCheckException("过期的JWT令牌！");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
            throw new JwtCheckException("不支持的JWT令牌！");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
            throw new JwtCheckException("该JWT令牌无效！");
        }
    }
}
