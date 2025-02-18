package com.shuaigef.springbootinit.config;

import com.shuaigef.springbootinit.mapper.SysRoleAuthorityMapper;
import com.shuaigef.springbootinit.model.entity.SessionUser;
import com.shuaigef.springbootinit.model.entity.SysUser;
import com.shuaigef.springbootinit.service.SysUserService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 动态权限校验 通过用户的 RoleId 获取 Role 对应的 AuthorityList
 * 然后判断key是不是存在list中 以此来认定此用户是否拥有访问此接口的权限
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Service
@Slf4j
public class RoleCheckService {

    @Resource
    private SysRoleAuthorityMapper sysRoleAuthorityMapper;

    @Resource
    private SysUserService sysUserService;

    /**
     * check用户是否具有访问的权限 注：不需要用户登陆的接口不要使用该方法作为自定义el表达式使用
     *
     * @param keys 需要进行验证的权限字符串
     * @return 登陆用户是否拥有对应权限 true or false
     */
    public boolean hasPermission(String... keys) {
        log.info("权限校验：" + Arrays.toString(keys));

        Long currentUserId;
        try {
            // 获取当前登陆用户信息
            SessionUser sessionUser = (SessionUser) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            currentUserId = sessionUser.getUserId();
        } catch (ClassCastException E) {
            return false;
        }

        // 根据用户的 roleId 查询用户权限列表
        SysUser currentSysUser = sysUserService.getById(currentUserId);
        List<String> authorityCodeList = sysRoleAuthorityMapper.findAuthorityCodeByRoleId(
                currentSysUser.getRoleId());

        // 权限校验
        List<GrantedAuthority> grantedAuthorityList = authorityCodeList.stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        for (String key : keys) {
            boolean flag = grantedAuthorityList.stream().anyMatch(
                    authority -> authority.getAuthority().equals(key)
            );
            if (flag) {
                return true;
            }
        }

        return false;
    }

}
