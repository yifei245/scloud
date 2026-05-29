package com.scloud.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_dict")
public class SysDictDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String dictType;
    private String dictLabel;
    private String dictValue;
    private Integer status;
}
