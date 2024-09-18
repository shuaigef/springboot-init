package com.shuaigef.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuaigef.springbootinit.model.dto.user.UserAddRequest;
import com.shuaigef.springbootinit.model.entity.User;
import com.shuaigef.springbootinit.model.vo.UserVO;
import java.util.List;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
public interface UserService extends IService<User> {

    long addUser(UserAddRequest userAddRequest);

    boolean deleteUserById(long id);

    UserVO getUserVO(User user);

    List<UserVO> getUserVO(List<User> userList);
}
