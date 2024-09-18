package com.shuaigef.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
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

    List<Authority> findMenuTree(Long userId);

    Boolean isAuthorityExist(Set<Long> authorityIds);

    void roleBindAuthority(Long roleId, Set<Long> newAuthorityIds);

    AuthorityVO getAuthorityVO(Authority authority);

    List<AuthorityVO> getAuthorityVO(List<Authority> authorityList);

}
