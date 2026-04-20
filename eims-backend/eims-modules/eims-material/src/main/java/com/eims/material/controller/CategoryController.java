package com.eims.material.controller;

import com.eims.common.core.domain.Result;
import com.eims.material.domain.Category;
import com.eims.material.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping
    public Result<List<Category>> list() {
        return Result.ok(categoryService.getTreeList());
    }

    @GetMapping("/{id}")
    public Result<Category> getInfo(@PathVariable Long id) {
        return Result.ok(categoryService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        categoryService.removeByIds(ids);
        return Result.ok();
    }
}
