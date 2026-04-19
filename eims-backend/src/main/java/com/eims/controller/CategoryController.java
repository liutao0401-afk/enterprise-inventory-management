package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.ApiResponse;
import com.eims.dto.PageRequest;
import com.eims.entity.Category;
import com.eims.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/list")
    public ApiResponse<List<Category>> list() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        List<Category> list = categoryRepository.selectList(wrapper);
        return ApiResponse.success(list);
    }

    @GetMapping("/tree")
    public ApiResponse<List<Category>> tree() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        List<Category> all = categoryRepository.selectList(wrapper);
        return ApiResponse.success(all);
    }

    @GetMapping("/{id}")
    public ApiResponse<Category> getById(@PathVariable Long id) {
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            return ApiResponse.error("分类不存在");
        }
        return ApiResponse.success(category);
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody Category category) {
        categoryRepository.insert(category);
        return ApiResponse.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryRepository.updateById(category);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        // 检查是否有子分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, id);
        Long count = categoryRepository.selectCount(wrapper);
        if (count > 0) {
            return ApiResponse.error("存在子分类，无法删除");
        }
        categoryRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
