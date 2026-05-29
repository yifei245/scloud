package com.scloud.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scloud.common.core.Result;
import com.scloud.common.security.RequirePermission;
import com.scloud.common.security.SecurityConstants;
import com.scloud.demo.entity.OrderDO;
import com.scloud.demo.entity.ProductDO;
import com.scloud.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/demo/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @RequirePermission("demo:product:query")
    public Result<Page<ProductDO>> page(String keyword, Integer status, @RequestParam(defaultValue = "1") long current, @RequestParam(defaultValue = "10") long size) {
        return Result.ok(productService.page(keyword, status, current, size));
    }

    @PostMapping
    @RequirePermission("demo:product:create")
    public Result<Boolean> save(@RequestBody ProductDO body) {
        return Result.ok(productService.save(body));
    }

    @PutMapping("/{id}")
    @RequirePermission("demo:product:update")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody ProductDO body) {
        body.setId(id);
        return Result.ok(productService.updateById(body));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("demo:product:delete")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(productService.removeById(id));
    }

    @PostMapping("/{id}/on-sale")
    @RequirePermission("demo:product:update")
    public Result<Boolean> onSale(@PathVariable Long id) {
        ProductDO body = new ProductDO();
        body.setId(id);
        body.setStatus(1);
        return Result.ok(productService.updateById(body));
    }

    @PostMapping("/{id}/off-sale")
    @RequirePermission("demo:product:update")
    public Result<Boolean> offSale(@PathVariable Long id) {
        ProductDO body = new ProductDO();
        body.setId(id);
        body.setStatus(0);
        return Result.ok(productService.updateById(body));
    }

    @PostMapping("/purchase")
    @RequirePermission("demo:product:purchase")
    public Result<OrderDO> purchase(@RequestHeader(value = SecurityConstants.USER_ID, defaultValue = "0") Long userId, @RequestBody Map<String, Integer> body) {
        return Result.ok(productService.purchase(userId, Long.valueOf(body.get("productId")), body.get("quantity")));
    }
}
