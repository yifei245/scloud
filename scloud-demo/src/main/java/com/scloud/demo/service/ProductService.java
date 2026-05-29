package com.scloud.demo.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scloud.common.core.BizException;
import com.scloud.common.core.ErrorCode;
import com.scloud.demo.entity.OrderDO;
import com.scloud.demo.entity.ProductDO;
import com.scloud.demo.mapper.OrderMapper;
import com.scloud.demo.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService extends ServiceImpl<ProductMapper, ProductDO> {
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public Page<ProductDO> page(String keyword, Integer status, long current, long size) {
        return page(Page.of(current, size), Wrappers.<ProductDO>lambdaQuery()
                .like(keyword != null && !keyword.isBlank(), ProductDO::getProductName, keyword)
                .eq(status != null, ProductDO::getStatus, status).orderByDesc(ProductDO::getId));
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderDO purchase(Long userId, Long productId, Integer quantity) {
        ProductDO product = productMapper.selectById(productId);
        if (product == null) throw new BizException(ErrorCode.NOT_FOUND, "商品不存在");
        if (product.getStatus() == null || product.getStatus() != 1) throw new BizException(ErrorCode.CONFLICT, "商品未上架");
        if (product.getStock() == null || product.getStock() < quantity) throw new BizException(ErrorCode.CONFLICT, "库存不足");
        int updated = productMapper.update(null, Wrappers.<ProductDO>lambdaUpdate()
                .eq(ProductDO::getId, productId).ge(ProductDO::getStock, quantity).setSql("stock = stock - " + quantity));
        if (updated != 1) throw new BizException(ErrorCode.CONFLICT, "库存扣减失败");
        OrderDO order = new OrderDO();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        order.setStatus(1);
        orderMapper.insert(order);
        return order;
    }
}
