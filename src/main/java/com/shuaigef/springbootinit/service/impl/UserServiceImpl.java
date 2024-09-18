package com.shuaigef.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.mapper.RoleMapper;
import com.shuaigef.springbootinit.mapper.UserMapper;
import com.shuaigef.springbootinit.model.dto.user.UserAddRequest;
import com.shuaigef.springbootinit.model.entity.Role;
import com.shuaigef.springbootinit.model.entity.User;
import com.shuaigef.springbootinit.model.vo.UserVO;
import com.shuaigef.springbootinit.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public long addUser(UserAddRequest userAddRequest) {
        String username = userAddRequest.getUsername();
        String password = userAddRequest.getPassword();
        String checkPassword = userAddRequest.getCheckPassword();
        String nickname = userAddRequest.getNickname();
        String userAvatar = userAddRequest.getUserAvatar();
        String userProfile = userAddRequest.getUserProfile();
        Long roleId = userAddRequest.getRoleId();
        // username 不能为 admin
        if (StringUtils.equals(SecurityConstant.ADMIN_USERNAME, username)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 两次密码不一致
        if (!StringUtils.equals(password, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码输入不一致");
        }
        // 用户名已存在
        User selectUser = baseMapper
                .selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (selectUser != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }
        // 角色是否为管理员以及是否存在
        if (SecurityConstant.ADMIN_ROLE_ID.compareTo(roleId) == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不能为管理员");
        }
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        user.setPassword(passwordEncoder.encode(password));
        baseMapper.insert(user);
        return user.getId();
    }

    @Override
    public boolean deleteUserById(long id) {
        // 删除用户不能为管理员
        if (SecurityConstant.ADMIN_USER_ID.compareTo(id) == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能删除管理员");
        }
        // 用户不存在
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        baseMapper.deleteById(id);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 用户角色类型转换 id -> roleName
        userVO.setUserRole(roleMapper.selectById(user.getRoleId()).getRoleName());
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }
}




