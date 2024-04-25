package com.shuaigef.springbootinit.common;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.shuaigef.springbootinit.model.entity.User;
import com.shuaigef.springbootinit.model.enums.UserRoleEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 初始化项目未使用权限校验，只使用角色校验
        List<String> list = new ArrayList<String>();
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 从 SaSession 获取当前登录用户信息
        User user = (User) StpUtil.getSession().get(SaSession.USER);
        String userRole = user.getUserRole();
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(userRole);
        List<String> list = new ArrayList<String>();
        if (userRoleEnum == null) {
            return list;
        }
        list.add(userRoleEnum.getValue());
        return list;
    }

}
