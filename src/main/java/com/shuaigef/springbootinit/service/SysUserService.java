package com.shuaigef.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuaigef.springbootinit.model.dto.user.SysUserAddRequest;
import com.shuaigef.springbootinit.model.dto.user.SysUserRegisterRequest;
import com.shuaigef.springbootinit.model.dto.user.SysUserUpdateBasicInfoRequest;
import com.shuaigef.springbootinit.model.dto.user.SysUserUpdateRequest;
import com.shuaigef.springbootinit.model.entity.SysUser;
import com.shuaigef.springbootinit.model.vo.SysUserVO;
import java.util.List;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 注册
     *
     * @param sysUserRegisterRequest 用户注册请求
     * @return 是否成功
     */
    boolean register(SysUserRegisterRequest sysUserRegisterRequest);

    long addUser(SysUserAddRequest sysUserAddRequest);

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 是否成功
     */
    boolean deleteUserById(long id);

    /**
     * 批量删除用户
     *
     * @param ids 用户id
     * @return 是否成功
     */
    boolean deleteBatchUser(List<Long> ids);

    /**
     * 更新用户
     *
     * @param sysUserUpdateRequest 用户更新参数
     * @return
     */
    boolean updateUser(SysUserUpdateRequest sysUserUpdateRequest);

    boolean updateUserBasicInfo(SysUserUpdateBasicInfoRequest sysUserUpdateBasicInfoRequest);

    SysUserVO getUserVO(SysUser sysUser);

    List<SysUserVO> getUserVO(List<SysUser> sysUserList);
}
