package com.shuaigef.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuaigef.springbootinit.mapper.AuthorityMapper;
import com.shuaigef.springbootinit.model.entity.Authority;
import com.shuaigef.springbootinit.model.entity.AuthorityMeta;
import com.shuaigef.springbootinit.model.entity.RoleAuthority;
import com.shuaigef.springbootinit.model.vo.AuthorityVO;
import com.shuaigef.springbootinit.service.AuthorityService;
import com.shuaigef.springbootinit.service.RoleAuthorityService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
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
            meta.setTitle(authority.getMenuName());
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

}




