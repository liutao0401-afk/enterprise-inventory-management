package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eims.dto.ApiResponse;
import com.eims.entity.Warehouse;
import com.eims.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @GetMapping("/list")
    public ApiResponse<List<Warehouse>> list() {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Warehouse::getStatus, "ACTIVE");
        wrapper.orderByAsc(Warehouse::getName);
        List<Warehouse> list = warehouseRepository.selectList(wrapper);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<Warehouse> getById(@PathVariable Long id) {
        Warehouse warehouse = warehouseRepository.selectById(id);
        if (warehouse == null) {
            return ApiResponse.error("仓库不存在");
        }
        return ApiResponse.success(warehouse);
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody Warehouse warehouse) {
        warehouseRepository.insert(warehouse);
        return ApiResponse.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        warehouseRepository.updateById(warehouse);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setStatus("INACTIVE");
        warehouseRepository.updateById(warehouse);
        return ApiResponse.success("删除成功", null);
    }
}
