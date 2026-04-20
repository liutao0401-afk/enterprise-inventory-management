package com.eims.material.controller;

import com.eims.common.core.domain.Result;
import com.eims.material.domain.Material;
import com.eims.material.service.IMaterialService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material")
@RequiredArgsConstructor
public class MaterialController {

    private final IMaterialService materialService;

    @GetMapping
    public Result<IPage<Material>> list(Material query,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(materialService.queryPage(query, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Material> getInfo(@PathVariable Long id) {
        return Result.ok(materialService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Material material) {
        materialService.save(material);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Material material) {
        materialService.updateById(material);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        materialService.removeByIds(ids);
        return Result.ok();
    }

    @GetMapping("/code/generate")
    public Result<String> generateCode() {
        return Result.ok(materialService.generateCode());
    }
}
