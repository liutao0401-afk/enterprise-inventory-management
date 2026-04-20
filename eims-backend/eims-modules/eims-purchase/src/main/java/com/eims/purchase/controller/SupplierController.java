package com.eims.purchase.controller;

import com.eims.common.core.domain.Result;
import com.eims.purchase.domain.Supplier;
import com.eims.purchase.service.ISupplierService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final ISupplierService supplierService;

    @GetMapping
    public Result<IPage<Supplier>> list(Supplier query,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(supplierService.queryPage(query, pageNum, pageSize));
    }

    @GetMapping("/all")
    public Result<List<Supplier>> getAll() {
        return Result.ok(supplierService.getAll());
    }

    @GetMapping("/{id}")
    public Result<Supplier> getInfo(@PathVariable Long id) {
        return Result.ok(supplierService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Supplier supplier) {
        supplierService.save(supplier);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Supplier supplier) {
        supplierService.updateById(supplier);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        supplierService.removeByIds(ids);
        return Result.ok();
    }
}
