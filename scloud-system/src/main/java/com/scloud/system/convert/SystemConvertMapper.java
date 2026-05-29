package com.scloud.system.convert;

import com.scloud.system.entity.SysDeptDO;
import com.scloud.system.entity.SysDictDO;
import com.scloud.system.entity.SysMenuDO;
import com.scloud.system.entity.SysPostDO;
import com.scloud.system.entity.SysRoleDO;
import com.scloud.system.entity.SysUserDO;
import com.scloud.system.vo.DeptRequest;
import com.scloud.system.vo.DictRequest;
import com.scloud.system.vo.MenuRequest;
import com.scloud.system.vo.PostRequest;
import com.scloud.system.vo.RoleRequest;
import com.scloud.system.vo.UserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SystemConvertMapper {
    SysUserDO toUser(UserRequest request);

    SysRoleDO toRole(RoleRequest request);

    SysMenuDO toMenu(MenuRequest request);

    SysDeptDO toDept(DeptRequest request);

    SysDictDO toDict(DictRequest request);

    SysPostDO toPost(PostRequest request);
}
