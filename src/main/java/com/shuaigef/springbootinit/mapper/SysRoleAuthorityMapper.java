package com.shuaigef.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuaigef.springbootinit.model.entity.SysRoleAuthority;
import java.util.HashSet;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface SysRoleAuthorityMapper extends BaseMapper<SysRoleAuthority> {

    HashSet<Long> findAuthorityIdsByRoleId(@Param("roleId") Long roleId);

    List<String> findAuthorityCodeByRoleId(@Param("roleId") Long roleId);

}




