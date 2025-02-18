package com.shuaigef.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuaigef.springbootinit.model.entity.SysAuthority;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface SysAuthorityMapper extends BaseMapper<SysAuthority> {

    /**
     * 查询用户权限列表
     *
     * @param userId 用户id
     * @return 用户权限列表
     */
    List<SysAuthority> findListByUserIdAndType(
            @Param("userId") Long userId);

}




