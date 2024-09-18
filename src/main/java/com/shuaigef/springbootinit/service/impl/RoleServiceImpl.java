package com.shuaigef.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.mapper.RoleMapper;
import com.shuaigef.springbootinit.model.dto.role.RoleAddRequest;
import com.shuaigef.springbootinit.model.dto.role.RoleUpdateRequest;
import com.shuaigef.springbootinit.model.entity.Role;
import com.shuaigef.springbootinit.model.vo.RoleVO;
import com.shuaigef.springbootinit.service.RoleService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 角色服务实现
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements RoleService {

    @Override
    public long addRole(RoleAddRequest roleAddRequest) {
        String roleName = roleAddRequest.getRoleName();
        if (StringUtils.equals(SecurityConstant.ADMIN_ROLE_NAME, roleName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能新增管理员角色");
        }
        Role selectRole = baseMapper
                .selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleName, roleName));
        if (selectRole != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该角色已存在");
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleAddRequest, role);
        baseMapper.insert(role);
        return role.getId();
    }

    @Override
    public boolean updateRole(RoleUpdateRequest roleUpdateRequest) {
        Long id = roleUpdateRequest.getId();
        String roleName = roleUpdateRequest.getRoleName();
        if (SecurityConstant.ADMIN_ROLE_ID.compareTo(id) == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能修改管理员角色");
        }
        if (StringUtils.equals(SecurityConstant.ADMIN_ROLE_NAME, roleName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能修改为管理员角色");
        }
        Role selectRole = baseMapper.selectById(id);
        if (selectRole == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该角色不存在");
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleUpdateRequest, role);
        baseMapper.updateById(role);
        return true;
    }

    @Override
    public boolean deleteRoleById(long id) {
        if (SecurityConstant.ADMIN_ROLE_ID.compareTo(id) == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能删除管理员角色");
        }
        Role selectRole = baseMapper.selectById(id);
        if (selectRole == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该角色不存在");
        }
        baseMapper.deleteById(id);
        return true;
    }

    @Override
    public RoleVO getRoleVO(Role role) {
        if (role == null) {
            return null;
        }
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        return roleVO;
    }

    @Override
    public List<RoleVO> getRoleVO(List<Role> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return new ArrayList<>();
        }
        return roleList.stream().map(this::getRoleVO).collect(Collectors.toList());
    }


}




