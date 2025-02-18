package com.shuaigef.springbootinit.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.mapper.SysRoleMapper;
import com.shuaigef.springbootinit.model.dto.role.SysRoleAddRequest;
import com.shuaigef.springbootinit.model.dto.role.SysRoleQueryRequest;
import com.shuaigef.springbootinit.model.dto.role.SysRoleUpdateRequest;
import com.shuaigef.springbootinit.model.entity.SysRole;
import com.shuaigef.springbootinit.model.vo.SysRoleVO;
import com.shuaigef.springbootinit.service.SysRoleService;
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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    @Override
    public long addRole(SysRoleAddRequest sysRoleAddRequest) {
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleAddRequest, sysRole);
        validateRole(sysRole, false);

        boolean result = this.save(sysRole);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增失败");
        }
        return sysRole.getId();
    }

    @Override
    public boolean updateRole(SysRoleUpdateRequest sysRoleUpdateRequest) {
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleUpdateRequest, sysRole);
        validateRole(sysRole, true);

        boolean result = this.updateById(sysRole);
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

        SysRole selectSysRole = baseMapper.selectById(id);
        if (selectSysRole == null) {
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
    public Page<SysRoleVO> listRoleByPage(SysRoleQueryRequest sysRoleQueryRequest) {
        String roleName = sysRoleQueryRequest.getRoleName();
        long current = sysRoleQueryRequest.getCurrent();
        long pageSize = sysRoleQueryRequest.getPageSize();
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(roleName), SysRole::getRoleName, roleName);
        Page<SysRole> rolePage = this.page(new Page<>(current, pageSize), queryWrapper);
        Page<SysRoleVO> roleVOPage = new Page<>(current, pageSize, rolePage.getTotal());
        List<SysRoleVO> sysRoleVOList = this.getRoleVO(rolePage.getRecords());
        roleVOPage.setRecords(sysRoleVOList);
        return roleVOPage;
    }

    @Override
    public SysRoleVO getRoleVO(SysRole sysRole) {
        if (sysRole == null) {
            return null;
        }
        SysRoleVO sysRoleVO = new SysRoleVO();
        BeanUtils.copyProperties(sysRole, sysRoleVO);
        return sysRoleVO;
    }

    @Override
    public List<SysRoleVO> getRoleVO(List<SysRole> sysRoleList) {
        if (CollectionUtils.isEmpty(sysRoleList)) {
            return new ArrayList<>();
        }
        return sysRoleList.stream().map(this::getRoleVO).collect(Collectors.toList());
    }

    /**
     * 角色字段校验
     *
     * @param sysRole 角色信息
     * @param isUpdate 是否为更新用户，如果更新则排除 id == role.getId() 的记录
     */
    private void validateRole(SysRole sysRole, boolean isUpdate) {
        if (sysRole == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验id
        Long id = sysRole.getId();
        if (isUpdate) {
            if (SecurityConstant.ADMIN_ROLE_ID.equals(id)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能修改管理员角色");
            }
            if (this.getById(id) == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不存在");
            }
        }

        // 校验角色名
        String roleName = sysRole.getRoleName();
        if (StringUtils.isNotBlank(roleName)) {
            if (SecurityConstant.ADMIN_ROLE_NAME.equals(roleName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, isUpdate ? "不能修改管理员角色" : "角色名异常");
            }
            validateUniqueField(SysRole::getRoleName, roleName, id, isUpdate, "角色名重复");
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
    private void validateUniqueField(SFunction<SysRole, ?> column, Object value, Long id, boolean isUpdate, String errorMsg) {
        SysRole existingSysRole = this.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(column, value)
                .ne(isUpdate && id != null, SysRole::getId, id));
        if (existingSysRole != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMsg);
        }
    }


}




