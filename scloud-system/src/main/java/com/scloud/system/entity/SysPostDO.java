package com.scloud.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_post")
public class SysPostDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String postCode;
    private String postName;
    private Integer status;
}
