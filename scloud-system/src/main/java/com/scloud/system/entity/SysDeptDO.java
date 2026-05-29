package com.scloud.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_dept")
public class SysDeptDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String deptName;
    private Integer status;
}
