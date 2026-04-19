package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.ApiResponse;
import com.eims.dto.PageRequest;
import com.eims.entity.Material;
import com.eims.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialRepository materialRepository;

    @GetMapping("/list")
    public ApiResponse<Page<Material>> list(PageRequest pageRequest) {
        Page<Material> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();

        if (pageRequest.getKeyword() != null && !pageRequest.getKeyword().isEmpty()) {
            wrapper.like(Material::getName, pageRequest.getKeyword())
                    .or()
                    .like(Material::getCode, pageRequest.getKeyword());
        }

        wrapper.eq(Material::getStatus, "ACTIVE");
        wrapper.orderByDesc(Material::getCreateTime);

        Page<Material> result = materialRepository.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/all")
    public ApiResponse<List<Material>> all() {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, "ACTIVE");
        wrapper.orderByAsc(Material::getName);
        List<Material> list = materialRepository.selectList(wrapper);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<Material> getById(@PathVariable Long id) {
        Material material = materialRepository.selectById(id);
        if (material == null) {
            return ApiResponse.error("物资不存在");
        }
        return ApiResponse.success(material);
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody Material material) {
        materialRepository.insert(material);
        return ApiResponse.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Material material) {
        material.setId(id);
        materialRepository.updateById(material);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        // 软删除
        Material material = new Material();
        material.setId(id);
        material.setStatus("INACTIVE");
        materialRepository.updateById(material);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/low-stock")
    public ApiResponse<List<Material>> getLowStock() {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, "ACTIVE");
        wrapper.apply("current_stock <= safety_stock");
        List<Material> list = materialRepository.selectList(wrapper);
        return ApiResponse.success(list);
    }
}
