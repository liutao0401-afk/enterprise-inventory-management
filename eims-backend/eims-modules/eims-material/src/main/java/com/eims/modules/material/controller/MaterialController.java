package com.eims.modules.material.controller;

import com.eims.common.core.domain.Result;
import com.eims.modules.material.domain.Material;
import com.eims.modules.material.domain.MaterialQuery;
import com.eims.modules.material.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 物资管理 Controller
 */
@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    /**
     * GET /api/material/page - 分页查询物资
     */
    @GetMapping("/page")
    public Result<Object> page(MaterialQuery query) {
        return Result.ok(materialService.page(query));
    }

    /**
     * GET /api/material/{id} - 获取物资详情
     */
    @GetMapping("/{id}")
    public Result<Material> getById(@PathVariable Long id) {
        Material material = materialService.getById(id);
        return material != null ? Result.ok(material) : Result.fail("物资不存在");
    }

    /**
     * POST /api/material - 新增物资
     */
    @PostMapping
    public Result<Long> save(@RequestBody Material material) {
        Long id = materialService.save(material);
        return id != null ? Result.ok(id) : Result.fail("新增失败");
    }

    /**
     * PUT /api/material/{id} - 更新物资
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Material material) {
        material.setId(id);
        return Result.ok(materialService.updateById(material));
    }

    /**
     * DELETE /api/material/{id} - 删除物资
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(materialService.deleteById(id));
    }
}
