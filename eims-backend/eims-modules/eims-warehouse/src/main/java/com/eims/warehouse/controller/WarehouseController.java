package com.eims.warehouse.controller;

import com.eims.common.core.domain.Result;
import com.eims.warehouse.domain.Warehouse;
import com.eims.warehouse.service.IWarehouseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final IWarehouseService warehouseService;

    @GetMapping
    public Result<IPage<Warehouse>> list(Warehouse query,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(warehouseService.queryPage(query, pageNum, pageSize));
    }

    @GetMapping("/all")
    public Result<List<Warehouse>> getAll() {
        return Result.ok(warehouseService.getAll());
    }

    @GetMapping("/{id}")
    public Result<Warehouse> getInfo(@PathVariable Long id) {
        return Result.ok(warehouseService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Warehouse warehouse) {
        warehouseService.save(warehouse);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Warehouse warehouse) {
        warehouseService.updateById(warehouse);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        warehouseService.removeByIds(ids);
        return Result.ok();
    }
}
