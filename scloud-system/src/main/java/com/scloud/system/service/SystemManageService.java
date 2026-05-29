package com.scloud.system.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scloud.common.core.BizException;
import com.scloud.common.core.ErrorCode;
import com.scloud.system.convert.SystemConvertMapper;
import com.scloud.system.entity.SysDeptDO;
import com.scloud.system.entity.SysDictDO;
import com.scloud.system.entity.SysMenuDO;
import com.scloud.system.entity.SysPostDO;
import com.scloud.system.entity.SysRoleDO;
import com.scloud.system.entity.SysRoleMenuDO;
import com.scloud.system.entity.SysUserDO;
import com.scloud.system.entity.SysUserRoleDO;
import com.scloud.system.mapper.SysDeptMapper;
import com.scloud.system.mapper.SysDictMapper;
import com.scloud.system.mapper.SysMenuMapper;
import com.scloud.system.mapper.SysPostMapper;
import com.scloud.system.mapper.SysRoleMapper;
import com.scloud.system.mapper.SysRoleMenuMapper;
import com.scloud.system.mapper.SysUserMapper;
import com.scloud.system.mapper.SysUserRoleMapper;
import com.scloud.system.vo.DeptRequest;
import com.scloud.system.vo.DictRequest;
import com.scloud.system.vo.MenuRequest;
import com.scloud.system.vo.PostRequest;
import com.scloud.system.vo.RoleRequest;
import com.scloud.system.vo.UserRequest;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemManageService {
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final SysDeptMapper deptMapper;
    private final SysDictMapper dictMapper;
    private final SysPostMapper postMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SystemConvertMapper systemConvertMapper;

    public SysUserDO createUser(UserRequest request) {
        SysUserDO user = systemConvertMapper.toUser(request);
        user.setPassword(SecureUtil.sha256(requirePassword(request.getPassword())));
        user.setStatus(defaultStatus(request.getStatus()));
        userMapper.insert(user);
        return user;
    }

    public SysUserDO updateUser(Long id, UserRequest request) {
        SysUserDO user = requireUser(id);
        SysUserDO update = systemConvertMapper.toUser(request);
        update.setPassword(user.getPassword());
        if (StrUtil.isNotBlank(request.getPassword())) {
            update.setPassword(SecureUtil.sha256(request.getPassword()));
        }
        update.setId(id);
        userMapper.updateById(update);
        return requireUser(id);
    }

    public void deleteUser(Long id) {
        requireUser(id);
        userRoleMapper.delete(Wrappers.<SysUserRoleDO>lambdaQuery().eq(SysUserRoleDO::getUserId, id));
        userMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        requireUser(userId);
        userRoleMapper.delete(Wrappers.<SysUserRoleDO>lambdaQuery().eq(SysUserRoleDO::getUserId, userId));
        if (CollUtil.isEmpty(roleIds)) {
            return;
        }
        for (Long roleId : roleIds) {
            requireRole(roleId);
            SysUserRoleDO userRole = new SysUserRoleDO();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    public List<Long> listUserRoleIds(Long userId) {
        requireUser(userId);
        return userRoleMapper.selectList(Wrappers.<SysUserRoleDO>lambdaQuery().eq(SysUserRoleDO::getUserId, userId))
                .stream().map(SysUserRoleDO::getRoleId).toList();
    }

    public SysRoleDO createRole(RoleRequest request) {
        SysRoleDO role = systemConvertMapper.toRole(request);
        role.setStatus(defaultStatus(request.getStatus()));
        roleMapper.insert(role);
        return role;
    }

    public SysRoleDO updateRole(Long id, RoleRequest request) {
        requireRole(id);
        SysRoleDO role = systemConvertMapper.toRole(request);
        role.setId(id);
        roleMapper.updateById(role);
        return requireRole(id);
    }

    public void deleteRole(Long id) {
        requireRole(id);
        userRoleMapper.delete(Wrappers.<SysUserRoleDO>lambdaQuery().eq(SysUserRoleDO::getRoleId, id));
        roleMenuMapper.delete(Wrappers.<SysRoleMenuDO>lambdaQuery().eq(SysRoleMenuDO::getRoleId, id));
        roleMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        requireRole(roleId);
        roleMenuMapper.delete(Wrappers.<SysRoleMenuDO>lambdaQuery().eq(SysRoleMenuDO::getRoleId, roleId));
        if (CollUtil.isEmpty(menuIds)) {
            return;
        }
        for (Long menuId : menuIds) {
            requireMenu(menuId);
            SysRoleMenuDO roleMenu = new SysRoleMenuDO();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    public List<Long> listRoleMenuIds(Long roleId) {
        requireRole(roleId);
        return roleMenuMapper.selectList(Wrappers.<SysRoleMenuDO>lambdaQuery().eq(SysRoleMenuDO::getRoleId, roleId))
                .stream().map(SysRoleMenuDO::getMenuId).toList();
    }

    public SysMenuDO createMenu(MenuRequest request) {
        SysMenuDO menu = systemConvertMapper.toMenu(request);
        menu.setParentId(defaultParentId(request.getParentId()));
        menu.setStatus(defaultStatus(request.getStatus()));
        menuMapper.insert(menu);
        return menu;
    }

    public SysMenuDO updateMenu(Long id, MenuRequest request) {
        requireMenu(id);
        SysMenuDO menu = systemConvertMapper.toMenu(request);
        menu.setId(id);
        menuMapper.updateById(menu);
        return requireMenu(id);
    }

    public void deleteMenu(Long id) {
        requireMenu(id);
        roleMenuMapper.delete(Wrappers.<SysRoleMenuDO>lambdaQuery().eq(SysRoleMenuDO::getMenuId, id));
        menuMapper.deleteById(id);
    }

    public SysDeptDO createDept(DeptRequest request) {
        SysDeptDO dept = systemConvertMapper.toDept(request);
        dept.setParentId(defaultParentId(request.getParentId()));
        dept.setStatus(defaultStatus(request.getStatus()));
        deptMapper.insert(dept);
        return dept;
    }

    public SysDeptDO updateDept(Long id, DeptRequest request) {
        requireDept(id);
        SysDeptDO dept = systemConvertMapper.toDept(request);
        dept.setId(id);
        deptMapper.updateById(dept);
        return requireDept(id);
    }

    public void deleteDept(Long id) {
        requireDept(id);
        deptMapper.deleteById(id);
    }

    public SysDictDO createDict(DictRequest request) {
        SysDictDO dict = systemConvertMapper.toDict(request);
        dict.setStatus(defaultStatus(request.getStatus()));
        dictMapper.insert(dict);
        return dict;
    }

    public SysDictDO updateDict(Long id, DictRequest request) {
        requireDict(id);
        SysDictDO dict = systemConvertMapper.toDict(request);
        dict.setId(id);
        dictMapper.updateById(dict);
        return requireDict(id);
    }

    public void deleteDict(Long id) {
        requireDict(id);
        dictMapper.deleteById(id);
    }

    public SysPostDO createPost(PostRequest request) {
        SysPostDO post = systemConvertMapper.toPost(request);
        post.setStatus(defaultStatus(request.getStatus()));
        postMapper.insert(post);
        return post;
    }

    public SysPostDO updatePost(Long id, PostRequest request) {
        requirePost(id);
        SysPostDO post = systemConvertMapper.toPost(request);
        post.setId(id);
        postMapper.updateById(post);
        return requirePost(id);
    }

    public void deletePost(Long id) {
        requirePost(id);
        postMapper.deleteById(id);
    }

    public void updateUserStatus(Long id, Integer status) {
        requireUser(id);
        SysUserDO user = new SysUserDO();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    public void updateRoleStatus(Long id, Integer status) {
        requireRole(id);
        SysRoleDO role = new SysRoleDO();
        role.setId(id);
        role.setStatus(status);
        roleMapper.updateById(role);
    }

    public void updateMenuStatus(Long id, Integer status) {
        requireMenu(id);
        SysMenuDO menu = new SysMenuDO();
        menu.setId(id);
        menu.setStatus(status);
        menuMapper.updateById(menu);
    }

    public void updateDeptStatus(Long id, Integer status) {
        requireDept(id);
        SysDeptDO dept = new SysDeptDO();
        dept.setId(id);
        dept.setStatus(status);
        deptMapper.updateById(dept);
    }

    public void updateDictStatus(Long id, Integer status) {
        requireDict(id);
        SysDictDO dict = new SysDictDO();
        dict.setId(id);
        dict.setStatus(status);
        dictMapper.updateById(dict);
    }

    public void updatePostStatus(Long id, Integer status) {
        requirePost(id);
        SysPostDO post = new SysPostDO();
        post.setId(id);
        post.setStatus(status);
        postMapper.updateById(post);
    }

    private SysUserDO requireUser(Long id) {
        SysUserDO user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private SysRoleDO requireRole(Long id) {
        SysRoleDO role = roleMapper.selectById(id);
        if (role == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "角色不存在");
        }
        return role;
    }

    private SysMenuDO requireMenu(Long id) {
        SysMenuDO menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "菜单不存在");
        }
        return menu;
    }

    private SysDeptDO requireDept(Long id) {
        SysDeptDO dept = deptMapper.selectById(id);
        if (dept == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "部门不存在");
        }
        return dept;
    }

    private SysDictDO requireDict(Long id) {
        SysDictDO dict = dictMapper.selectById(id);
        if (dict == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "字典不存在");
        }
        return dict;
    }

    private SysPostDO requirePost(Long id) {
        SysPostDO post = postMapper.selectById(id);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "岗位不存在");
        }
        return post;
    }

    private Integer defaultStatus(Integer status) {
        return status == null ? 1 : status;
    }

    private Long defaultParentId(Long parentId) {
        return parentId == null ? 0L : parentId;
    }

    private String requirePassword(String password) {
        if (StrUtil.isBlank(password)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "密码不能为空");
        }
        return password;
    }

    public List<Long> safeIds(List<Long> ids) {
        return ids == null ? Collections.emptyList() : ids;
    }

    public boolean hasAllDataPermission(Long userId) {
        return dataScope(userId) == 1;
    }

    public boolean hasSelfDataPermission(Long userId) {
        return dataScope(userId) == 3;
    }

    public Long userDeptId(Long userId) {
        SysUserDO user = requireUser(userId);
        return user.getDeptId();
    }

    private int dataScope(Long userId) {
        List<Long> roleIds = userRoleMapper.selectList(Wrappers.<SysUserRoleDO>lambdaQuery()
                        .eq(SysUserRoleDO::getUserId, userId))
                .stream().map(SysUserRoleDO::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return 3;
        }
        List<SysRoleDO> roles = roleMapper.selectBatchIds(roleIds);
        boolean admin = roles.stream().anyMatch(role -> "admin".equals(role.getRoleKey()));
        if (admin || roles.stream().anyMatch(role -> role.getDataScope() != null && role.getDataScope() == 1)) {
            return 1;
        }
        if (roles.stream().anyMatch(role -> role.getDataScope() != null && role.getDataScope() == 2)) {
            return 2;
        }
        return 3;
    }
}
