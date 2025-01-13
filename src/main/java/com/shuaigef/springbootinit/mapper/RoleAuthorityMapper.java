package com.shuaigef.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuaigef.springbootinit.model.entity.RoleAuthority;
import java.util.HashSet;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface RoleAuthorityMapper extends BaseMapper<RoleAuthority> {

    HashSet<Long> findAuthorityIdsByRoleId(@Param("roleId") Long roleId);

    List<String> findAuthorityCodeByRoleId(@Param("roleId") Long roleId);

}




