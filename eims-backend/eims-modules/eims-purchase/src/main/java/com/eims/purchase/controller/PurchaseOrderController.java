package com.eims.purchase.controller;

import com.eims.common.core.domain.Result;
import com.eims.purchase.domain.PurchaseOrder;
import com.eims.purchase.domain.PurchaseOrderItem;
import com.eims.purchase.service.IPurchaseOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final IPurchaseOrderService orderService;

    @GetMapping
    public Result<IPage<PurchaseOrder>> list(PurchaseOrder query,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(orderService.queryPage(query, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<PurchaseOrder> getInfo(@PathVariable Long id) {
        return Result.ok(orderService.getById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<PurchaseOrderItem>> getItems(@PathVariable Long id) {
        return Result.ok(orderService.getItemsByOrderId(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody PurchaseOrder order) {
        orderService.saveOrder(order);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody PurchaseOrder order) {
        orderService.updateById(order);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        orderService.removeByIds(ids);
        return Result.ok();
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirm(id);
        return Result.ok();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return Result.ok();
    }
}
