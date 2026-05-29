package com.scloud.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scloud.common.core.Result;
import com.scloud.common.security.CurrentUserContext;
import com.scloud.common.security.RequirePermission;
import com.scloud.system.entity.SysDeptDO;
import com.scloud.system.entity.SysDictDO;
import com.scloud.system.entity.SysMenuDO;
import com.scloud.system.entity.SysPostDO;
import com.scloud.system.entity.SysRoleDO;
import com.scloud.system.entity.SysUserDO;
import com.scloud.system.mapper.SysDeptMapper;
import com.scloud.system.mapper.SysDictMapper;
import com.scloud.system.mapper.SysMenuMapper;
import com.scloud.system.mapper.SysPostMapper;
import com.scloud.system.mapper.SysRoleMapper;
import com.scloud.system.mapper.SysUserMapper;
import com.scloud.system.service.SystemManageService;
import com.scloud.system.vo.AssignIdsRequest;
import com.scloud.system.vo.DeptRequest;
import com.scloud.system.vo.DeptPageRequest;
import com.scloud.system.vo.DictRequest;
import com.scloud.system.vo.DictPageRequest;
import com.scloud.system.vo.MenuRequest;
import com.scloud.system.vo.MenuPageRequest;
import com.scloud.system.vo.PostPageRequest;
import com.scloud.system.vo.PostRequest;
import com.scloud.system.vo.RolePageRequest;
import com.scloud.system.vo.RoleRequest;
import com.scloud.system.vo.StatusRequest;
import com.scloud.system.vo.UserPageRequest;
import com.scloud.system.vo.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system")
public class SystemController {
    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;
    private final SysMenuMapper menuMapper;
    private final SysDeptMapper deptMapper;
    private final SysDictMapper dictMapper;
    private final SysPostMapper postMapper;
    private final SystemManageService systemManageService;

    @Operation(summary = "用户列表")
    @RequirePermission("system:user:query")
    @GetMapping("/users")
    public Result<?> users(UserPageRequest request) {
        Long currentUserId = CurrentUserContext.getUserId();
        LambdaQueryWrapper<SysUserDO> query = new LambdaQueryWrapper<SysUserDO>()
                .eq(request.getStatus() != null, SysUserDO::getStatus, request.getStatus())
                .eq(request.getDeptId() != null, SysUserDO::getDeptId, request.getDeptId())
                .like(StrUtil.isNotBlank(request.getUsername()), SysUserDO::getUsername, request.getUsername())
                .like(StrUtil.isNotBlank(request.getNickname()), SysUserDO::getNickname, request.getNickname())
                .like(StrUtil.isNotBlank(request.getMobile()), SysUserDO::getMobile, request.getMobile())
                .like(StrUtil.isNotBlank(request.getEmail()), SysUserDO::getEmail, request.getEmail())
                .orderByDesc(SysUserDO::getId);
        if (currentUserId != null && !systemManageService.hasAllDataPermission(currentUserId)) {
            query.eq(SysUserDO::getDeptId, systemManageService.userDeptId(currentUserId));
            if (systemManageService.hasSelfDataPermission(currentUserId)) {
                query.eq(SysUserDO::getId, currentUserId);
            }
        }
        return Result.ok(userMapper.selectPage(new Page<>(request.getPageNo(), request.getPageSize()), query));
    }

    @Operation(summary = "用户详情")
    @RequirePermission("system:user:query")
    @GetMapping("/users/{id}")
    public Result<?> user(@PathVariable Long id) {
        return Result.ok(userMapper.selectById(id));
    }

    @Operation(summary = "新增用户")
    @RequirePermission("system:user:create")
    @PostMapping("/users")
    public Result<?> createUser(@Valid @RequestBody UserRequest request) {
        return Result.ok(systemManageService.createUser(request));
    }

    @Operation(summary = "修改用户")
    @RequirePermission("system:user:update")
    @PutMapping("/users/{id}")
    public Result<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return Result.ok(systemManageService.updateUser(id, request));
    }

    @Operation(summary = "删除用户")
    @RequirePermission("system:user:delete")
    @DeleteMapping("/users/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        systemManageService.deleteUser(id);
        return Result.ok(true);
    }

    @Operation(summary = "启用禁用用户")
    @RequirePermission("system:user:update")
    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        systemManageService.updateUserStatus(id, request.getStatus());
        return Result.ok(true);
    }

    @Operation(summary = "查询用户角色")
    @RequirePermission("system:user:query")
    @GetMapping("/users/{id}/roles")
    public Result<?> userRoles(@PathVariable Long id) {
        return Result.ok(systemManageService.listUserRoleIds(id));
    }

    @Operation(summary = "分配用户角色")
    @RequirePermission("system:user:assign-role")
    @PutMapping("/users/{id}/roles")
    public Result<?> assignUserRoles(@PathVariable Long id, @RequestBody AssignIdsRequest request) {
        systemManageService.assignUserRoles(id, systemManageService.safeIds(request.getIds()));
        return Result.ok(true);
    }

    @Operation(summary = "角色列表")
    @RequirePermission("system:role:query")
    @GetMapping("/roles")
    public Result<?> roles(RolePageRequest request) {
        LambdaQueryWrapper<SysRoleDO> query = new LambdaQueryWrapper<SysRoleDO>()
                .eq(request.getStatus() != null, SysRoleDO::getStatus, request.getStatus())
                .eq(request.getDataScope() != null, SysRoleDO::getDataScope, request.getDataScope())
                .like(StrUtil.isNotBlank(request.getRoleName()), SysRoleDO::getRoleName, request.getRoleName())
                .like(StrUtil.isNotBlank(request.getRoleKey()), SysRoleDO::getRoleKey, request.getRoleKey())
                .orderByDesc(SysRoleDO::getId);
        return Result.ok(roleMapper.selectPage(new Page<>(request.getPageNo(), request.getPageSize()), query));
    }

    @Operation(summary = "角色详情")
    @RequirePermission("system:role:query")
    @GetMapping("/roles/{id}")
    public Result<?> role(@PathVariable Long id) {
        return Result.ok(roleMapper.selectById(id));
    }

    @Operation(summary = "新增角色")
    @RequirePermission("system:role:create")
    @PostMapping("/roles")
    public Result<?> createRole(@Valid @RequestBody RoleRequest request) {
        return Result.ok(systemManageService.createRole(request));
    }

    @Operation(summary = "修改角色")
    @RequirePermission("system:role:update")
    @PutMapping("/roles/{id}")
    public Result<?> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return Result.ok(systemManageService.updateRole(id, request));
    }

    @Operation(summary = "删除角色")
    @RequirePermission("system:role:delete")
    @DeleteMapping("/roles/{id}")
    public Result<?> deleteRole(@PathVariable Long id) {
        systemManageService.deleteRole(id);
        return Result.ok(true);
    }

    @Operation(summary = "启用禁用角色")
    @RequirePermission("system:role:update")
    @PutMapping("/roles/{id}/status")
    public Result<?> updateRoleStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        systemManageService.updateRoleStatus(id, request.getStatus());
        return Result.ok(true);
    }

    @Operation(summary = "查询角色菜单权限")
    @RequirePermission("system:role:query")
    @GetMapping("/roles/{id}/menus")
    public Result<?> roleMenus(@PathVariable Long id) {
        return Result.ok(systemManageService.listRoleMenuIds(id));
    }

    @Operation(summary = "分配角色菜单权限")
    @RequirePermission("system:role:assign-menu")
    @PutMapping("/roles/{id}/menus")
    public Result<?> assignRoleMenus(@PathVariable Long id, @RequestBody AssignIdsRequest request) {
        systemManageService.assignRoleMenus(id, systemManageService.safeIds(request.getIds()));
        return Result.ok(true);
    }

    @Operation(summary = "菜单列表")
    @RequirePermission("system:menu:query")
    @GetMapping("/menus")
    public Result<?> menus(MenuPageRequest request) {
        LambdaQueryWrapper<SysMenuDO> query = new LambdaQueryWrapper<SysMenuDO>()
                .eq(request.getStatus() != null, SysMenuDO::getStatus, request.getStatus())
                .eq(request.getParentId() != null, SysMenuDO::getParentId, request.getParentId())
                .eq(request.getType() != null, SysMenuDO::getType, request.getType())
                .like(StrUtil.isNotBlank(request.getMenuName()), SysMenuDO::getMenuName, request.getMenuName())
                .like(StrUtil.isNotBlank(request.getPermission()), SysMenuDO::getPermission, request.getPermission())
                .orderByAsc(SysMenuDO::getParentId)
                .orderByAsc(SysMenuDO::getId);
        return Result.ok(menuMapper.selectPage(new Page<>(request.getPageNo(), request.getPageSize()), query));
    }

    @Operation(summary = "新增菜单")
    @RequirePermission("system:menu:create")
    @PostMapping("/menus")
    public Result<?> createMenu(@Valid @RequestBody MenuRequest request) {
        return Result.ok(systemManageService.createMenu(request));
    }

    @Operation(summary = "修改菜单")
    @RequirePermission("system:menu:update")
    @PutMapping("/menus/{id}")
    public Result<?> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        return Result.ok(systemManageService.updateMenu(id, request));
    }

    @Operation(summary = "删除菜单")
    @RequirePermission("system:menu:delete")
    @DeleteMapping("/menus/{id}")
    public Result<?> deleteMenu(@PathVariable Long id) {
        systemManageService.deleteMenu(id);
        return Result.ok(true);
    }

    @Operation(summary = "启用禁用菜单")
    @RequirePermission("system:menu:update")
    @PutMapping("/menus/{id}/status")
    public Result<?> updateMenuStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        systemManageService.updateMenuStatus(id, request.getStatus());
        return Result.ok(true);
    }

    @Operation(summary = "部门列表")
    @RequirePermission("system:dept:query")
    @GetMapping("/depts")
    public Result<?> depts(DeptPageRequest request) {
        LambdaQueryWrapper<SysDeptDO> query = new LambdaQueryWrapper<SysDeptDO>()
                .eq(request.getStatus() != null, SysDeptDO::getStatus, request.getStatus())
                .eq(request.getParentId() != null, SysDeptDO::getParentId, request.getParentId())
                .like(StrUtil.isNotBlank(request.getDeptName()), SysDeptDO::getDeptName, request.getDeptName())
                .orderByAsc(SysDeptDO::getParentId)
                .orderByAsc(SysDeptDO::getId);
        return Result.ok(deptMapper.selectPage(new Page<>(request.getPageNo(), request.getPageSize()), query));
    }

    @Operation(summary = "新增部门")
    @RequirePermission("system:dept:create")
    @PostMapping("/depts")
    public Result<?> createDept(@Valid @RequestBody DeptRequest request) {
        return Result.ok(systemManageService.createDept(request));
    }

    @Operation(summary = "修改部门")
    @RequirePermission("system:dept:update")
    @PutMapping("/depts/{id}")
    public Result<?> updateDept(@PathVariable Long id, @Valid @RequestBody DeptRequest request) {
        return Result.ok(systemManageService.updateDept(id, request));
    }

    @Operation(summary = "删除部门")
    @RequirePermission("system:dept:delete")
    @DeleteMapping("/depts/{id}")
    public Result<?> deleteDept(@PathVariable Long id) {
        systemManageService.deleteDept(id);
        return Result.ok(true);
    }

    @Operation(summary = "启用禁用部门")
    @RequirePermission("system:dept:update")
    @PutMapping("/depts/{id}/status")
    public Result<?> updateDeptStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        systemManageService.updateDeptStatus(id, request.getStatus());
        return Result.ok(true);
    }

    @Operation(summary = "字典列表")
    @RequirePermission("system:dict:query")
    @GetMapping("/dicts")
    public Result<?> dicts(DictPageRequest request) {
        LambdaQueryWrapper<SysDictDO> query = new LambdaQueryWrapper<SysDictDO>()
                .eq(request.getStatus() != null, SysDictDO::getStatus, request.getStatus())
                .eq(StrUtil.isNotBlank(request.getDictType()), SysDictDO::getDictType, request.getDictType())
                .like(StrUtil.isNotBlank(request.getDictLabel()), SysDictDO::getDictLabel, request.getDictLabel())
                .like(StrUtil.isNotBlank(request.getDictValue()), SysDictDO::getDictValue, request.getDictValue())
                .orderByAsc(SysDictDO::getDictType)
                .orderByAsc(SysDictDO::getId);
        return Result.ok(dictMapper.selectPage(new Page<>(request.getPageNo(), request.getPageSize()), query));
    }

    @Operation(summary = "新增字典")
    @RequirePermission("system:dict:create")
    @PostMapping("/dicts")
    public Result<?> createDict(@Valid @RequestBody DictRequest request) {
        return Result.ok(systemManageService.createDict(request));
    }

    @Operation(summary = "修改字典")
    @RequirePermission("system:dict:update")
    @PutMapping("/dicts/{id}")
    public Result<?> updateDict(@PathVariable Long id, @Valid @RequestBody DictRequest request) {
        return Result.ok(systemManageService.updateDict(id, request));
    }

    @Operation(summary = "删除字典")
    @RequirePermission("system:dict:delete")
    @DeleteMapping("/dicts/{id}")
    public Result<?> deleteDict(@PathVariable Long id) {
        systemManageService.deleteDict(id);
        return Result.ok(true);
    }

    @Operation(summary = "启用禁用字典")
    @RequirePermission("system:dict:update")
    @PutMapping("/dicts/{id}/status")
    public Result<?> updateDictStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        systemManageService.updateDictStatus(id, request.getStatus());
        return Result.ok(true);
    }

    @Operation(summary = "岗位列表")
    @RequirePermission("system:post:query")
    @GetMapping("/posts")
    public Result<?> posts(PostPageRequest request) {
        LambdaQueryWrapper<SysPostDO> query = new LambdaQueryWrapper<SysPostDO>()
                .eq(request.getStatus() != null, SysPostDO::getStatus, request.getStatus())
                .like(StrUtil.isNotBlank(request.getPostCode()), SysPostDO::getPostCode, request.getPostCode())
                .like(StrUtil.isNotBlank(request.getPostName()), SysPostDO::getPostName, request.getPostName())
                .orderByDesc(SysPostDO::getId);
        return Result.ok(postMapper.selectPage(new Page<>(request.getPageNo(), request.getPageSize()), query));
    }

    @Operation(summary = "新增岗位")
    @RequirePermission("system:post:create")
    @PostMapping("/posts")
    public Result<?> createPost(@Valid @RequestBody PostRequest request) {
        return Result.ok(systemManageService.createPost(request));
    }

    @Operation(summary = "修改岗位")
    @RequirePermission("system:post:update")
    @PutMapping("/posts/{id}")
    public Result<?> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        return Result.ok(systemManageService.updatePost(id, request));
    }

    @Operation(summary = "删除岗位")
    @RequirePermission("system:post:delete")
    @DeleteMapping("/posts/{id}")
    public Result<?> deletePost(@PathVariable Long id) {
        systemManageService.deletePost(id);
        return Result.ok(true);
    }

    @Operation(summary = "启用禁用岗位")
    @RequirePermission("system:post:update")
    @PutMapping("/posts/{id}/status")
    public Result<?> updatePostStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        systemManageService.updatePostStatus(id, request.getStatus());
        return Result.ok(true);
    }
}
