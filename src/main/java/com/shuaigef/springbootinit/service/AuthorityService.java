package com.shuaigef.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuaigef.springbootinit.model.dto.authority.AuthorityAddRequest;
import com.shuaigef.springbootinit.model.dto.authority.AuthorityQueryRequest;
import com.shuaigef.springbootinit.model.dto.authority.AuthorityUpdateRequest;
import com.shuaigef.springbootinit.model.entity.Authority;
import com.shuaigef.springbootinit.model.vo.AuthorityVO;
import java.util.List;
import java.util.Set;

/**
 * 权限服务
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface AuthorityService extends IService<Authority> {

    /**
     * 查询角色权限树
     *
     * @param roleId 角色id
     * @return
     */
    List<Authority> findTreeByRoleId(long roleId);

    /**
     * 查询用户菜单权限树
     * @param userId
     * @return
     */
    List<Authority> findMenuTree(Long userId);

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
     * @param authorityQueryRequest 权限查询请求
     * @return
     */
    Page<AuthorityVO> listAuthorityByPage(AuthorityQueryRequest authorityQueryRequest);

    /**
     * 新增权限
     * @param authorityAddRequest 权限新增请求
     * @return 新增权限id
     */
    long addAuthority(AuthorityAddRequest authorityAddRequest);

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
     * @param authorityUpdateRequest 权限修改请求
     * @return 是否成功
     */
    boolean updateAuthority(AuthorityUpdateRequest authorityUpdateRequest);

    /**
     * 重置管理员权限
     */
    void resetAdmin();

    AuthorityVO getAuthorityVO(Authority authority);

    List<AuthorityVO> getAuthorityVO(List<Authority> authorityList);

}
