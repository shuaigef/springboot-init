package com.shuaigef.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuaigef.springbootinit.model.dto.role.RoleAddRequest;
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

    long addRole(RoleAddRequest roleAddRequest);

    boolean updateRole(RoleUpdateRequest roleUpdateRequest);

    boolean deleteRoleById(long id);

    RoleVO getRoleVO(Role role);

    List<RoleVO> getRoleVO(List<Role> roleList);

}
