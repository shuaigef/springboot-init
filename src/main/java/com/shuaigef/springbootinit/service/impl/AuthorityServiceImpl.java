package com.shuaigef.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuaigef.springbootinit.common.code.ErrorCode;
import com.shuaigef.springbootinit.common.utils.SecurityUtils;
import com.shuaigef.springbootinit.constant.SecurityConstant;
import com.shuaigef.springbootinit.exception.BusinessException;
import com.shuaigef.springbootinit.mapper.AuthorityMapper;
import com.shuaigef.springbootinit.mapper.RoleAuthorityMapper;
import com.shuaigef.springbootinit.model.dto.authority.AuthorityAddRequest;
import com.shuaigef.springbootinit.model.dto.authority.AuthorityQueryRequest;
import com.shuaigef.springbootinit.model.dto.authority.AuthorityUpdateRequest;
import com.shuaigef.springbootinit.model.entity.Authority;
import com.shuaigef.springbootinit.model.entity.AuthorityMeta;
import com.shuaigef.springbootinit.model.entity.RoleAuthority;
import com.shuaigef.springbootinit.model.vo.AuthorityVO;
import com.shuaigef.springbootinit.service.AuthorityService;
import com.shuaigef.springbootinit.service.RoleAuthorityService;
import com.shuaigef.springbootinit.service.RoleService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 权限服务实现
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Service
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, Authority>
        implements AuthorityService {

    @Resource
    private RoleAuthorityService roleAuthorityService;

    @Resource
    private RoleAuthorityMapper roleAuthorityMapper;

    @Resource
    private RoleService roleService;

    private List<Authority> listTreeProcess(List<Authority> authorityList) {
        List<Authority> tree = authorityList.stream()
                //过滤出parentId为0的节点为根节点 (如果有多个根节点也可以)
                .filter(item -> item.getParentId() == 0)
                .map(item -> {
                    item.setChildren(findChildNode(item, authorityList));
                    return item;
                }).collect(Collectors.toList());
        return tree;
    }

    private List<Authority> findChildNode(Authority parentNode, List<Authority> nodes) {
        List<Authority> childList = nodes.stream()
                //过滤出parentNode的子节点
                .filter(item -> Objects.equals(parentNode.getId(), item.getParentId()))
                //重新映射成一个新的node节点
                .map(item -> {
                    //这里设置当前节点的子节点列表, 子节点列表的获取是通过递归的方式获取的
                    item.setChildren(findChildNode(item, nodes));
                    return item;
                }).collect(Collectors.toList());
        return childList;
    }

    @Override
    public List<Authority> findTreeByRoleId(long roleId) {
        // 全部(排除前端需要隐藏的次级路由页面)
        List<Authority> authorityList = this.list();
        HashSet<Long> checkIds = roleAuthorityMapper.findAuthorityIdsByRoleId(roleId);
        // 判断是否要打勾
        authorityList.forEach(authority -> {
            authority.setChildren(new ArrayList<>());
            if (checkIds.contains(authority.getId())) {
                authority.setCheck(true);
            }
        });

        // list -> tree
        return this.listTreeProcess(authorityList);
    }

    @Override
    public List<Authority> findMenuTree(Long userId) {
        // 用户被赋权的所有菜单
        List<Authority> authorityList =
                baseMapper.findListByUserIdAndType(userId);
        for (int i = authorityList.size() - 1; i >= 0; i--) {
            Authority authority = authorityList.get(i);
            AuthorityMeta meta = new AuthorityMeta();
            meta.setHideMenu(authority.getHidden() != 0);
            meta.setIcon(authority.getMenuIcon());
            meta.setComponentName(authority.getComponentName());
            meta.setTitle(authority.getName());
            authority.setMeta(meta);
        }

        //排序
        List<Authority> collect = authorityList.stream()
                .sorted(Comparator.comparing(Authority::getOrderNo))
                .collect(Collectors.toList());
        // list -> tree
        return listTreeProcess(collect);
    }

    @Override
    public Boolean isAuthorityExist(Set<Long> authorityIds) {
        if (CollectionUtils.isEmpty(authorityIds)) {
            return true;
        }

        Long existCount = baseMapper.selectCount(
                new QueryWrapper<Authority>().lambda().in(Authority::getId, authorityIds));
        return existCount == authorityIds.size();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void roleBindAuthority(Long roleId, Set<Long> newAuthorityIds) {
        // 清空原有的权限
        roleAuthorityService.remove(
                new LambdaQueryWrapper<RoleAuthority>().eq(RoleAuthority::getRoleId, roleId));

        // 为空则不再添加新权限
        if (!CollectionUtils.isEmpty(newAuthorityIds)) {
            Set<RoleAuthority> roleAuthoritySet = new HashSet<>();
            for (Long newAuthorityId : newAuthorityIds) {
                RoleAuthority roleAuthority = new RoleAuthority();
                roleAuthority.setRoleId(roleId);
                roleAuthority.setAuthorityId(newAuthorityId);
                roleAuthoritySet.add(roleAuthority);
            }
            roleAuthorityService.saveBatch(roleAuthoritySet);
        }
    }

    @Override
    public Page<AuthorityVO> listAuthorityByPage(AuthorityQueryRequest authorityQueryRequest) {
        String code = authorityQueryRequest.getCode();
        String name = authorityQueryRequest.getName();
        String authorityType = authorityQueryRequest.getAuthorityType();
        long current = authorityQueryRequest.getCurrent();
        long pageSize = authorityQueryRequest.getPageSize();

        LambdaQueryWrapper<Authority> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(code), Authority::getCode, code)
                .like(StringUtils.isNotBlank(name), Authority::getName, name)
                .eq(StringUtils.isNotBlank(authorityType), Authority::getAuthorityType, authorityType);
        Page<Authority> authorityPage = this.page(new Page<>(current, pageSize), queryWrapper);
        Page<AuthorityVO> authorityVOPage = new Page<>(current, pageSize, authorityPage.getTotal());
        List<AuthorityVO> authorityVOList = this.getAuthorityVO(authorityPage.getRecords());
        authorityVOPage.setRecords(authorityVOList);
        return authorityVOPage;
    }

    @Override
    public long addAuthority(AuthorityAddRequest authorityAddRequest) {
        Authority authority = new Authority();
        BeanUtils.copyProperties(authorityAddRequest, authority);
        validateAuthority(authority, false);

        boolean result = this.save(authority);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增失败");
        }
        return authority.getId();
    }

    @Override
    public boolean deleteAuthority(long id) {
        // 权限不存在
        Authority authority = this.getById(id);
        if (authority == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "权限不存在");
        }
        // 权限已被绑定
        List<RoleAuthority> roleAuthorityList = roleAuthorityService.list(new LambdaQueryWrapper<RoleAuthority>()
                .eq(RoleAuthority::getAuthorityId, id));
        if (!CollectionUtils.isEmpty(roleAuthorityList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该权限已被绑定，请先解绑");
        }

        boolean result = this.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除失败");
        }
        return true;
    }

    @Override
    public boolean deleteBatchAuthority(List<Long> ids) {
        boolean hasBindAuthority = ids.stream().anyMatch(id ->
                !CollectionUtils.isEmpty(roleAuthorityService.list(
                        new LambdaQueryWrapper<RoleAuthority>()
                                .eq(RoleAuthority::getAuthorityId, id)))
        );
        if (hasBindAuthority) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "所选权限中存在已被绑定的权限");
        }

        boolean result = this
                .remove(new LambdaQueryWrapper<Authority>().in(Authority::getId, ids));
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量删除失败");
        }
        return true;
    }

    @Override
    public boolean updateAuthority(AuthorityUpdateRequest authorityUpdateRequest) {
        Authority authority = new Authority();
        BeanUtils.copyProperties(authorityUpdateRequest, authority);
        validateAuthority(authority, true);

        boolean result = this.updateById(authority);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新权限失败");
        }
        return result;
    }

    @Override
    public void resetAdmin() {
        long currentUserId = SecurityUtils.getCurrentUserId();
        if (!SecurityConstant.ADMIN_USER_ID.equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }

        Set<Long> authorityIds = this.list().stream().map(Authority::getId)
                .collect(Collectors.toSet());
        this.roleBindAuthority(SecurityConstant.ADMIN_ROLE_ID, authorityIds);
    }

    @Override
    public AuthorityVO getAuthorityVO(Authority authority) {
        if (authority == null) {
            return null;
        }
        AuthorityVO authorityVO = new AuthorityVO();
        BeanUtils.copyProperties(authority, authorityVO);
        return authorityVO;
    }

    @Override
    public List<AuthorityVO> getAuthorityVO(List<Authority> authorityList) {
        if (CollectionUtils.isEmpty(authorityList)) {
            return new ArrayList<>();
        }
        return authorityList.stream().map(this::getAuthorityVO).collect(Collectors.toList());
    }

    /**
     * 权限字段校验
     *
     * @param authority 权限信息
     * @param isUpdate 是否为更新，如果更新则排除 id == authority.getId() 的记录
     */
    private void validateAuthority(Authority authority, boolean isUpdate) {
        if (authority == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验 id
        Long id = authority.getId();
        if (isUpdate && this.getById(id) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "权限不存在");
        }

        // 校验 code
        String code = authority.getCode();
        if (StringUtils.isNotBlank(code)) {
            validateUniqueField(Authority::getCode, code, id, isUpdate, "code 重复");
        }

        //校验 parentId
        Long parentId = authority.getParentId();
        if (parentId != null && parentId > 0 && this.getById(parentId) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "父权限 id 不存在");
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
    private void validateUniqueField(SFunction<Authority, ?> column, Object value, Long id, boolean isUpdate, String errorMsg) {
        Authority existingRole = this.getOne(new LambdaQueryWrapper<Authority>()
                .eq(column, value)
                .ne(isUpdate && id != null, Authority::getId, id));
        if (existingRole != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMsg);
        }
    }

}




