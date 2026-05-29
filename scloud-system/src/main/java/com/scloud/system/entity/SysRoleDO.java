package com.scloud.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role")
public class SysRoleDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roleName;
    private String roleKey;
    private Integer dataScope;
    private Integer status;
}
