package com.shuaigef.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuaigef.springbootinit.common.utils.RegexUtils;
import com.shuaigef.springbootinit.mapper.UserMapper;
import com.shuaigef.springbootinit.model.entity.SessionUser;
import com.shuaigef.springbootinit.model.entity.User;
import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * security 自定义 UserDetailsService 实现
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String usernameOrEmail) {
        log.debug("Authenticating {}", usernameOrEmail);
        User user = null;
        if (RegexUtils.isEmail(usernameOrEmail)) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, usernameOrEmail));
        } else {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, usernameOrEmail));
        }
        return Optional.ofNullable(user)
                .map(userTemp -> new SessionUser(userTemp.getUsername(), userTemp.getPassword(),
                        new ArrayList<>(),
                        userTemp.getId(), userTemp.getRoleId(), userTemp.getNickname(),
                        userTemp.getUserAvatar(), userTemp.getUserProfile(),
                        userTemp.getGender()))
                .orElseThrow(() -> new UsernameNotFoundException("登录信息错误"));
    }
}
