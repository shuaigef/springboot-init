package com.shuaigef.springbootinit.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.common.utils.RegexUtils;
import com.shuaigef.springbootinit.common.utils.SecurityUtils;
import com.shuaigef.springbootinit.constant.RedisConstant;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.mapper.SysRoleMapper;
import com.shuaigef.springbootinit.mapper.SysUserMapper;
import com.shuaigef.springbootinit.model.dto.user.SysUserAddRequest;
import com.shuaigef.springbootinit.model.dto.user.SysUserRegisterRequest;
import com.shuaigef.springbootinit.model.dto.user.SysUserUpdateBasicInfoRequest;
import com.shuaigef.springbootinit.model.dto.user.SysUserUpdateRequest;
import com.shuaigef.springbootinit.model.entity.SysRole;
import com.shuaigef.springbootinit.model.entity.SysUser;
import com.shuaigef.springbootinit.model.enums.SysUserGenderEnum;
import com.shuaigef.springbootinit.model.enums.VerificationCodeBizEnum;
import com.shuaigef.springbootinit.model.vo.SysUserVO;
import com.shuaigef.springbootinit.service.SysRoleService;
import com.shuaigef.springbootinit.service.SysUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 盐值
     */
    private final static String SALT = "shuaigef";

    /**
     * 默认用户名前缀
     */
    private final static String DEFAULT_USERNAME_PREFIX = "用户";

    @Override
    public boolean register(SysUserRegisterRequest sysUserRegisterRequest) {
        String username = sysUserRegisterRequest.getUsername();
        String email = sysUserRegisterRequest.getEmail();
        String password = sysUserRegisterRequest.getPassword();
        String checkPassword = sysUserRegisterRequest.getCheckPassword();
        String verificationCode = sysUserRegisterRequest.getVerificationCode();

        // 密码输入不一致
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码输入不一致");
        }

        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserRegisterRequest, sysUser);
        this.validateUser(sysUser, false);

        // 验证码校验
        String redisKey = VerificationCodeBizEnum.EMAIL_REGISTER.getValue() + RedisConstant.VERIFICATION_CODE_KEY + email;
        String currentVerificationCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (!verificationCode.equals(currentVerificationCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        // 删除已使用验证码
        stringRedisTemplate.delete(redisKey);

        String nickname = DEFAULT_USERNAME_PREFIX + RandomUtil.randomNumbers(4);
        sysUser.setNickname(nickname);
        sysUser.setPassword(passwordEncoder.encode(password));
        // todo 普通用户改为常量或字典
        sysUser.setRoleId(2l);
        sysUser.setGender(SysUserGenderEnum.UNKNOWN.getValue());
        return this.save(sysUser);
    }

    @Override
    public long addUser(SysUserAddRequest sysUserAddRequest) {
        String username = sysUserAddRequest.getUsername();
        String password = sysUserAddRequest.getPassword();
        String nickname = sysUserAddRequest.getNickname();
        Integer gender = sysUserAddRequest.getGender();
        Long roleId = sysUserAddRequest.getRoleId();
        String userAvatar = sysUserAddRequest.getUserAvatar();
        String email = sysUserAddRequest.getEmail();
        String phoneNumber = sysUserAddRequest.getPhoneNumber();


        // username 不能为 admin
        if (StringUtils.equals(SecurityConstant.ADMIN_USERNAME, username)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不能为 admin");
        }
        // 用户名已存在
        SysUser selectSysUser = baseMapper
                .selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (selectSysUser != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }
        // 角色是否为管理员以及是否存在
        if (SecurityConstant.ADMIN_ROLE_ID == roleId) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不能为管理员");
        }
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        if (sysRole == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不存在");
        }
        // 性别
        SysUserGenderEnum sysUserGenderEnum = SysUserGenderEnum.getEnumByValue(gender);
        if (sysUserGenderEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别错误");
        }
        // 邮箱
        if (StringUtils.isNotEmpty(email) && RegexUtils.isNotEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱错误");
        }
        // 手机号
        if (StringUtils.isNotEmpty(phoneNumber) && RegexUtils.isNotPhoneNumber(phoneNumber)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱错误");
        }

        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserAddRequest, sysUser);
        sysUser.setPassword(passwordEncoder.encode(password));
        boolean result = this.save(sysUser);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return sysUser.getId();
    }

    @Override
    public boolean deleteUserById(long id) {
        // 删除用户不能为管理员
        if (SecurityConstant.ADMIN_USER_ID.compareTo(id) == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能删除管理员");
        }
        // 用户不存在
        SysUser sysUser = baseMapper.selectById(id);
        if (sysUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        boolean result = this.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return result;
    }

    @Override
    public boolean deleteBatchUser(List<Long> ids) {
        // 删除用户不能为管理员
        if (CollectionUtil.contains(ids, SecurityConstant.ADMIN_USER_ID)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能删除管理员");
        }
        boolean result = this
                .remove(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, ids));
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量删除失败");
        }
        return true;
    }

    @Override
    public boolean updateUser(SysUserUpdateRequest sysUserUpdateRequest) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserUpdateRequest, sysUser);
        this.validateUser(sysUser, true);

        boolean result = this.updateById(sysUser);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新用户失败");
        }
        return result;
    }

    @Override
    public boolean updateUserBasicInfo(SysUserUpdateBasicInfoRequest sysUserUpdateBasicInfoRequest) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserUpdateBasicInfoRequest, sysUser);
        sysUser.setId(SecurityUtils.getCurrentUserId());
        validateUser(sysUser, true);
        boolean result = this.updateById(sysUser);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新用户基本信息失败");
        }
        return result;
    }

    @Override
    public SysUserVO getUserVO(SysUser sysUser) {
        if (sysUser == null) {
            return null;
        }
        SysUserVO sysUserVO = new SysUserVO();
        BeanUtils.copyProperties(sysUser, sysUserVO);
        // 用户角色类型转换 id -> roleName
        sysUserVO.setRoleName(sysRoleMapper.selectById(sysUser.getRoleId()).getRoleName());
        return sysUserVO;
    }

    @Override
    public List<SysUserVO> getUserVO(List<SysUser> sysUserList) {
        if (CollectionUtils.isEmpty(sysUserList)) {
            return new ArrayList<>();
        }
        return sysUserList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 用户字段校验
     *
     * @param sysUser 用户信息
     * @param isUpdate 是否为更新用户，如果更新则排除 id == user.getId() 的记录
     */
    private void validateUser(SysUser sysUser, boolean isUpdate) {
        if (sysUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 用户不存在
        Long id = sysUser.getId();
        if (isUpdate && this.getById(id) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 校验用户名
        String username = sysUser.getUsername();
        if (StringUtils.isNotBlank(username)) {
            if (isUpdate && SecurityConstant.ADMIN_USER_ID.equals(id)) {
                if (!SecurityConstant.ADMIN_USERNAME.equals(username)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能修改管理员用户名");
                }
            } else {
                // 非管理员用户名不能为 admin
                if (SecurityConstant.ADMIN_USERNAME.equals(username)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名异常");
                }
                this.validateUniqueField(SysUser::getUsername, username, id, isUpdate, "用户名已存在");
            }
        }

        // 校验手机号
        String phoneNumber = sysUser.getPhoneNumber();
        if (StringUtils.isNotBlank(phoneNumber)) {
            if (RegexUtils.isNotPhoneNumber(phoneNumber)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号错误");
            }
            this.validateUniqueField(SysUser::getPhoneNumber, phoneNumber, id, isUpdate, "手机号已注册");
        }

        // 校验邮箱
        String email = sysUser.getEmail();
        if (StringUtils.isNotBlank(email)) {
            if (RegexUtils.isNotEmail(email)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱错误");
            }
            this.validateUniqueField(SysUser::getEmail, email, id, isUpdate, "邮箱已注册");
        }

        // 校验角色
        Long roleId = sysUser.getRoleId();
        if (roleId != null && roleId > 0) {
            // 不可以修改管理员账号角色
            if (isUpdate && SecurityConstant.ADMIN_USER_ID.equals(id)) {
                if (!SecurityConstant.ADMIN_ROLE_ID.equals(roleId)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能修改管理员角色");
                }
            } else {
                // 角色是否为管理员以及是否存在
                if (SecurityConstant.ADMIN_ROLE_ID.compareTo(roleId) == 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不能为管理员");
                }
                SysRole sysRole = sysRoleService.getById(roleId);
                if (sysRole == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不存在");
                }
            }
        }

        // 校验性别
        Integer gender = sysUser.getGender();
        if (gender != null && SysUserGenderEnum.getEnumByValue(gender) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别错误");
        }
    }

    /**
     * 校验字段唯一性
     *
     * @param column   需要校验的字段（Lambda表达式）
     * @param value    字段值
     * @param id       当前记录ID
     * @param isUpdate 是否为更新操作
     * @param errorMsg 异常提示信息
     */
    private void validateUniqueField(SFunction<SysUser, ?> column, Object value, Long id, boolean isUpdate, String errorMsg) {
        SysUser existingSysUser = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(column, value)
                .ne(isUpdate && id != null, SysUser::getId, id));
        if (existingSysUser != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMsg);
        }
    }

}




