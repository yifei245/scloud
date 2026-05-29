package com.scloud.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("demo_product")
public class ProductDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String productName;
    private String productCode;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
}
