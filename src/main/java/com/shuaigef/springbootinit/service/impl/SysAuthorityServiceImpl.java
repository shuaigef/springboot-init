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
import com.shuaigef.springbootinit.mapper.SysAuthorityMapper;
import com.shuaigef.springbootinit.mapper.SysRoleAuthorityMapper;
import com.shuaigef.springbootinit.model.dto.authority.SysAuthorityAddRequest;
import com.shuaigef.springbootinit.model.dto.authority.SysAuthorityQueryRequest;
import com.shuaigef.springbootinit.model.dto.authority.SysAuthorityUpdateRequest;
import com.shuaigef.springbootinit.model.entity.SysAuthority;
import com.shuaigef.springbootinit.model.entity.SysAuthorityMeta;
import com.shuaigef.springbootinit.model.entity.SysRoleAuthority;
import com.shuaigef.springbootinit.model.vo.SysAuthorityVO;
import com.shuaigef.springbootinit.service.SysAuthorityService;
import com.shuaigef.springbootinit.service.SysRoleAuthorityService;
import com.shuaigef.springbootinit.service.SysRoleService;
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
public class SysAuthorityServiceImpl extends ServiceImpl<SysAuthorityMapper, SysAuthority>
        implements SysAuthorityService {

    @Resource
    private SysRoleAuthorityService sysRoleAuthorityService;

    @Resource
    private SysRoleAuthorityMapper sysRoleAuthorityMapper;

    @Resource
    private SysRoleService sysRoleService;

    private List<SysAuthority> listTreeProcess(List<SysAuthority> sysAuthorityList) {
        List<SysAuthority> tree = sysAuthorityList.stream()
                //过滤出parentId为0的节点为根节点 (如果有多个根节点也可以)
                .filter(item -> item.getParentId() == 0)
                .map(item -> {
                    item.setChildren(findChildNode(item, sysAuthorityList));
                    return item;
                }).collect(Collectors.toList());
        return tree;
    }

    private List<SysAuthority> findChildNode(SysAuthority parentNode, List<SysAuthority> nodes) {
        List<SysAuthority> childList = nodes.stream()
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
    public List<SysAuthority> findTreeByRoleId(long roleId) {
        // 全部(排除前端需要隐藏的次级路由页面)
        List<SysAuthority> sysAuthorityList = this.list();
        HashSet<Long> checkIds = sysRoleAuthorityMapper.findAuthorityIdsByRoleId(roleId);
        // 判断是否要打勾
        sysAuthorityList.forEach(authority -> {
            authority.setChildren(new ArrayList<>());
            if (checkIds.contains(authority.getId())) {
                authority.setCheck(true);
            }
        });

        // list -> tree
        return this.listTreeProcess(sysAuthorityList);
    }

    @Override
    public List<SysAuthority> findMenuTree(Long userId) {
        // 用户被赋权的所有菜单
        List<SysAuthority> sysAuthorityList =
                baseMapper.findListByUserIdAndType(userId);
        for (int i = sysAuthorityList.size() - 1; i >= 0; i--) {
            SysAuthority sysAuthority = sysAuthorityList.get(i);
            SysAuthorityMeta meta = new SysAuthorityMeta();
            meta.setHideMenu(sysAuthority.getHidden() != 0);
            meta.setIcon(sysAuthority.getMenuIcon());
            meta.setComponentName(sysAuthority.getComponentName());
            meta.setTitle(sysAuthority.getName());
            sysAuthority.setMeta(meta);
        }

        //排序
        List<SysAuthority> collect = sysAuthorityList.stream()
                .sorted(Comparator.comparing(SysAuthority::getOrderNo))
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
                new QueryWrapper<SysAuthority>().lambda().in(SysAuthority::getId, authorityIds));
        return existCount == authorityIds.size();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void roleBindAuthority(Long roleId, Set<Long> newAuthorityIds) {
        // 清空原有的权限
        sysRoleAuthorityService.remove(
                new LambdaQueryWrapper<SysRoleAuthority>().eq(SysRoleAuthority::getRoleId, roleId));

        // 为空则不再添加新权限
        if (!CollectionUtils.isEmpty(newAuthorityIds)) {
            Set<SysRoleAuthority> sysRoleAuthoritySet = new HashSet<>();
            for (Long newAuthorityId : newAuthorityIds) {
                SysRoleAuthority sysRoleAuthority = new SysRoleAuthority();
                sysRoleAuthority.setRoleId(roleId);
                sysRoleAuthority.setAuthorityId(newAuthorityId);
                sysRoleAuthoritySet.add(sysRoleAuthority);
            }
            sysRoleAuthorityService.saveBatch(sysRoleAuthoritySet);
        }
    }

    @Override
    public Page<SysAuthorityVO> listAuthorityByPage(
            SysAuthorityQueryRequest sysAuthorityQueryRequest) {
        String code = sysAuthorityQueryRequest.getCode();
        String name = sysAuthorityQueryRequest.getName();
        String authorityType = sysAuthorityQueryRequest.getAuthorityType();
        long current = sysAuthorityQueryRequest.getCurrent();
        long pageSize = sysAuthorityQueryRequest.getPageSize();

        LambdaQueryWrapper<SysAuthority> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(code), SysAuthority::getCode, code)
                .like(StringUtils.isNotBlank(name), SysAuthority::getName, name)
                .eq(StringUtils.isNotBlank(authorityType), SysAuthority::getAuthorityType, authorityType);
        Page<SysAuthority> authorityPage = this.page(new Page<>(current, pageSize), queryWrapper);
        Page<SysAuthorityVO> authorityVOPage = new Page<>(current, pageSize, authorityPage.getTotal());
        List<SysAuthorityVO> sysAuthorityVOList = this.getAuthorityVO(authorityPage.getRecords());
        authorityVOPage.setRecords(sysAuthorityVOList);
        return authorityVOPage;
    }

    @Override
    public long addAuthority(SysAuthorityAddRequest sysAuthorityAddRequest) {
        SysAuthority sysAuthority = new SysAuthority();
        BeanUtils.copyProperties(sysAuthorityAddRequest, sysAuthority);
        validateAuthority(sysAuthority, false);

        boolean result = this.save(sysAuthority);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增失败");
        }
        return sysAuthority.getId();
    }

    @Override
    public boolean deleteAuthority(long id) {
        // 权限不存在
        SysAuthority sysAuthority = this.getById(id);
        if (sysAuthority == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "权限不存在");
        }
        // 权限已被绑定
        List<SysRoleAuthority> sysRoleAuthorityList = sysRoleAuthorityService.list(new LambdaQueryWrapper<SysRoleAuthority>()
                .eq(SysRoleAuthority::getAuthorityId, id));
        if (!CollectionUtils.isEmpty(sysRoleAuthorityList)) {
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
                !CollectionUtils.isEmpty(sysRoleAuthorityService.list(
                        new LambdaQueryWrapper<SysRoleAuthority>()
                                .eq(SysRoleAuthority::getAuthorityId, id)))
        );
        if (hasBindAuthority) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "所选权限中存在已被绑定的权限");
        }

        boolean result = this
                .remove(new LambdaQueryWrapper<SysAuthority>().in(SysAuthority::getId, ids));
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量删除失败");
        }
        return true;
    }

    @Override
    public boolean updateAuthority(SysAuthorityUpdateRequest sysAuthorityUpdateRequest) {
        SysAuthority sysAuthority = new SysAuthority();
        BeanUtils.copyProperties(sysAuthorityUpdateRequest, sysAuthority);
        validateAuthority(sysAuthority, true);

        boolean result = this.updateById(sysAuthority);
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

        Set<Long> authorityIds = this.list().stream().map(SysAuthority::getId)
                .collect(Collectors.toSet());
        this.roleBindAuthority(SecurityConstant.ADMIN_ROLE_ID, authorityIds);
    }

    @Override
    public SysAuthorityVO getAuthorityVO(SysAuthority sysAuthority) {
        if (sysAuthority == null) {
            return null;
        }
        SysAuthorityVO sysAuthorityVO = new SysAuthorityVO();
        BeanUtils.copyProperties(sysAuthority, sysAuthorityVO);
        return sysAuthorityVO;
    }

    @Override
    public List<SysAuthorityVO> getAuthorityVO(List<SysAuthority> sysAuthorityList) {
        if (CollectionUtils.isEmpty(sysAuthorityList)) {
            return new ArrayList<>();
        }
        return sysAuthorityList.stream().map(this::getAuthorityVO).collect(Collectors.toList());
    }

    /**
     * 权限字段校验
     *
     * @param sysAuthority 权限信息
     * @param isUpdate 是否为更新，如果更新则排除 id == authority.getId() 的记录
     */
    private void validateAuthority(SysAuthority sysAuthority, boolean isUpdate) {
        if (sysAuthority == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验 id
        Long id = sysAuthority.getId();
        if (isUpdate && this.getById(id) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "权限不存在");
        }

        // 校验 code
        String code = sysAuthority.getCode();
        if (StringUtils.isNotBlank(code)) {
            validateUniqueField(SysAuthority::getCode, code, id, isUpdate, "code 重复");
        }

        //校验 parentId
        Long parentId = sysAuthority.getParentId();
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
    private void validateUniqueField(SFunction<SysAuthority, ?> column, Object value, Long id, boolean isUpdate, String errorMsg) {
        SysAuthority existingRole = this.getOne(new LambdaQueryWrapper<SysAuthority>()
                .eq(column, value)
                .ne(isUpdate && id != null, SysAuthority::getId, id));
        if (existingRole != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMsg);
        }
    }

}




