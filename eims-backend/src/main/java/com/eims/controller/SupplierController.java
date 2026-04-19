package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.ApiResponse;
import com.eims.dto.PageRequest;
import com.eims.entity.Supplier;
import com.eims.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping("/list")
    public ApiResponse<Page<Supplier>> list(PageRequest pageRequest) {
        Page<Supplier> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();

        if (pageRequest.getKeyword() != null && !pageRequest.getKeyword().isEmpty()) {
            wrapper.like(Supplier::getName, pageRequest.getKeyword())
                    .or()
                    .like(Supplier::getCode, pageRequest.getKeyword());
        }

        wrapper.eq(Supplier::getStatus, "ACTIVE");
        wrapper.orderByDesc(Supplier::getCreateTime);

        Page<Supplier> result = supplierRepository.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/all")
    public ApiResponse<List<Supplier>> all() {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getStatus, "ACTIVE");
        wrapper.orderByAsc(Supplier::getName);
        List<Supplier> list = supplierRepository.selectList(wrapper);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<Supplier> getById(@PathVariable Long id) {
        Supplier supplier = supplierRepository.selectById(id);
        if (supplier == null) {
            return ApiResponse.error("供应商不存在");
        }
        return ApiResponse.success(supplier);
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody Supplier supplier) {
        supplierRepository.insert(supplier);
        return ApiResponse.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplierRepository.updateById(supplier);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setStatus("INACTIVE");
        supplierRepository.updateById(supplier);
        return ApiResponse.success("删除成功", null);
    }
}
