package com.shuaigef.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuaigef.springbootinit.model.dto.user.UserAddRequest;
import com.shuaigef.springbootinit.model.dto.user.UserRegisterRequest;
import com.shuaigef.springbootinit.model.dto.user.UserUpdateBasicInfoRequest;
import com.shuaigef.springbootinit.model.entity.User;
import com.shuaigef.springbootinit.model.vo.UserVO;
import java.util.List;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface UserService extends IService<User> {

    /**
     * 注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 是否成功
     */
    boolean register(UserRegisterRequest userRegisterRequest);

    long addUser(UserAddRequest userAddRequest);

    boolean deleteUserById(long id);

    boolean updateUserBasicInfo(UserUpdateBasicInfoRequest userUpdateBasicInfoRequest);

    UserVO getUserVO(User user);

    List<UserVO> getUserVO(List<User> userList);
}
