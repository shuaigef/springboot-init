package com.shuaigef.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuaigef.springbootinit.model.dto.role.RoleAddRequest;
import com.shuaigef.springbootinit.model.dto.role.RoleQueryRequest;
import com.shuaigef.springbootinit.model.dto.role.RoleUpdateRequest;
import com.shuaigef.springbootinit.model.entity.Role;
import com.shuaigef.springbootinit.model.vo.RoleVO;
import java.util.List;

/**
 * 角色服务
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface RoleService extends IService<Role> {

    /**
     * 新增角色
     *
     * @param roleAddRequest 角色新增请求
     * @return 新增角色id
     */
    long addRole(RoleAddRequest roleAddRequest);

    /**
     * 修改角色
     *
     * @param roleUpdateRequest 角色修改请求
     * @return 是否成功
     */
    boolean updateRole(RoleUpdateRequest roleUpdateRequest);

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
     * @param roleQueryRequest 角色查询请求
     * @return
     */
    Page<RoleVO> listRoleByPage(RoleQueryRequest roleQueryRequest);

    RoleVO getRoleVO(Role role);

    List<RoleVO> getRoleVO(List<Role> roleList);

}
