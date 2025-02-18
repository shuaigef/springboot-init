package com.shuaigef.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuaigef.springbootinit.model.dto.role.SysRoleAddRequest;
import com.shuaigef.springbootinit.model.dto.role.SysRoleQueryRequest;
import com.shuaigef.springbootinit.model.dto.role.SysRoleUpdateRequest;
import com.shuaigef.springbootinit.model.entity.SysRole;
import com.shuaigef.springbootinit.model.vo.SysRoleVO;
import java.util.List;

/**
 * 角色服务
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 新增角色
     *
     * @param sysRoleAddRequest 角色新增请求
     * @return 新增角色id
     */
    long addRole(SysRoleAddRequest sysRoleAddRequest);

    /**
     * 修改角色
     *
     * @param sysRoleUpdateRequest 角色修改请求
     * @return 是否成功
     */
    boolean updateRole(SysRoleUpdateRequest sysRoleUpdateRequest);

    /**
     * 删除角色
     *
     * @param id 角色id
     * @return 是否成功
     */
    boolean deleteRole(long id);

    /**
     * 批量删除角色
     * @param ids 角色id
     * @return 是否成功
     */
    boolean deleteBatchRole(List<Long> ids);

    /**
     * 分页查询角色
     *
     * @param sysRoleQueryRequest 角色查询请求
     * @return
     */
    Page<SysRoleVO> listRoleByPage(SysRoleQueryRequest sysRoleQueryRequest);

    SysRoleVO getRoleVO(SysRole sysRole);

    List<SysRoleVO> getRoleVO(List<SysRole> sysRoleList);

}
