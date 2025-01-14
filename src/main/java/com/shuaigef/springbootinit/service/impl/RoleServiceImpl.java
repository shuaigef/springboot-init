package com.shuaigef.springbootinit.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.mapper.RoleMapper;
import com.shuaigef.springbootinit.model.dto.role.RoleAddRequest;
import com.shuaigef.springbootinit.model.dto.role.RoleQueryRequest;
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
        Role role = new Role();
        BeanUtils.copyProperties(roleAddRequest, role);
        validateRole(role, false);

        boolean result = this.save(role);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增失败");
        }
        return role.getId();
    }

    @Override
    public boolean updateRole(RoleUpdateRequest roleUpdateRequest) {
        Role role = new Role();
        BeanUtils.copyProperties(roleUpdateRequest, role);
        validateRole(role, true);

        boolean result = this.updateById(role);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "修改失败");
        }
        return result;
    }

    @Override
    public boolean deleteRole(long id) {
        if (SecurityConstant.ADMIN_ROLE_ID.compareTo(id) == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能删除管理员角色");
        }

        Role selectRole = baseMapper.selectById(id);
        if (selectRole == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该角色不存在");
        }
        boolean result = this.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除失败");
        }
        return true;
    }

    @Override
    public boolean deleteBatchRole(List<Long> ids) {
        if (CollectionUtil.contains(ids, SecurityConstant.ADMIN_ROLE_ID)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能删除管理员角色");
        }
        boolean result = this.removeBatchByIds(ids);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量删除失败");
        }
        return false;
    }

    @Override
    public Page<RoleVO> listRoleByPage(RoleQueryRequest roleQueryRequest) {
        String roleName = roleQueryRequest.getRoleName();
        long current = roleQueryRequest.getCurrent();
        long pageSize = roleQueryRequest.getPageSize();
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(roleName), Role::getRoleName, roleName);
        Page<Role> rolePage = this.page(new Page<>(current, pageSize), queryWrapper);
        Page<RoleVO> roleVOPage = new Page<>(current, pageSize, rolePage.getTotal());
        List<RoleVO> roleVOList = this.getRoleVO(rolePage.getRecords());
        roleVOPage.setRecords(roleVOList);
        return roleVOPage;
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

    /**
     * 角色字段校验
     *
     * @param role 角色信息
     * @param isUpdate 是否为更新用户，如果更新则排除 id == role.getId() 的记录
     */
    private void validateRole(Role role, boolean isUpdate) {
        if (role == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验id
        Long id = role.getId();
        if (isUpdate) {
            if (SecurityConstant.ADMIN_ROLE_ID.equals(id)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能修改管理员角色");
            }
            if (this.getById(id) == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不存在");
            }
        }

        // 校验角色名
        String roleName = role.getRoleName();
        if (StringUtils.isNotBlank(roleName)) {
            if (SecurityConstant.ADMIN_ROLE_NAME.equals(roleName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, isUpdate ? "不能修改管理员角色" : "角色名异常");
            }
            validateUniqueField(Role::getRoleName, roleName, id, isUpdate, "角色名重复");
        }
    }

    /**
     * 校验字段唯一性
     *
     * @param column   需要校验的字段（Lambda表达式）
     * @param value    字段值
     * @param id       当前记录ID
     * @param isUpdate 是否为更新操作
     * @param errorMsg 异常提示信息
     */
    private void validateUniqueField(SFunction<Role, ?> column, Object value, Long id, boolean isUpdate, String errorMsg) {
        Role existingRole = this.getOne(new LambdaQueryWrapper<Role>()
                .eq(column, value)
                .ne(isUpdate && id != null, Role::getId, id));
        if (existingRole != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMsg);
        }
    }


}




