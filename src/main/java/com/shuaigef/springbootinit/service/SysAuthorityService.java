package com.shuaigef.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuaigef.springbootinit.model.dto.authority.SysAuthorityAddRequest;
import com.shuaigef.springbootinit.model.dto.authority.SysAuthorityQueryRequest;
import com.shuaigef.springbootinit.model.dto.authority.SysAuthorityUpdateRequest;
import com.shuaigef.springbootinit.model.entity.SysAuthority;
import com.shuaigef.springbootinit.model.vo.SysAuthorityVO;
import java.util.List;
import java.util.Set;

/**
 * 权限服务
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface SysAuthorityService extends IService<SysAuthority> {

    /**
     * 查询角色权限树
     *
     * @param roleId 角色id
     * @return
     */
    List<SysAuthority> findTreeByRoleId(long roleId);

    /**
     * 查询用户菜单权限树
     * @param userId
     * @return
     */
    List<SysAuthority> findMenuTree(Long userId);

    /**
     * 权限是否存在
     *
     * @param authorityIds 权限id
     * @return
     */
    Boolean isAuthorityExist(Set<Long> authorityIds);

    /**
     * 角色绑定权限
     *
     * @param roleId 角色id
     * @param authorityIds 权限id
     */
    void roleBindAuthority(Long roleId, Set<Long> authorityIds);

    /**
     * 分页查询权限
     *
     * @param sysAuthorityQueryRequest 权限查询请求
     * @return
     */
    Page<SysAuthorityVO> listAuthorityByPage(SysAuthorityQueryRequest sysAuthorityQueryRequest);

    /**
     * 新增权限
     * @param sysAuthorityAddRequest 权限新增请求
     * @return 新增权限id
     */
    long addAuthority(SysAuthorityAddRequest sysAuthorityAddRequest);

    /**
     * 删除权限
     *
     * @param id 权限id
     * @return 是否成功
     */
    boolean deleteAuthority(long id);

    /**
     * 批量删除权限
     *
     * @param ids 权限id
     * @return 是否成功
     */
    boolean deleteBatchAuthority(List<Long> ids);

    /**
     * 修改权限
     * @param sysAuthorityUpdateRequest 权限修改请求
     * @return 是否成功
     */
    boolean updateAuthority(SysAuthorityUpdateRequest sysAuthorityUpdateRequest);

    /**
     * 重置管理员权限
     */
    void resetAdmin();

    SysAuthorityVO getAuthorityVO(SysAuthority sysAuthority);

    List<SysAuthorityVO> getAuthorityVO(List<SysAuthority> sysAuthorityList);

}
