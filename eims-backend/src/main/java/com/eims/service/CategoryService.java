package com.eims.service;

import com.eims.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getTreeList();
    Category getById(Long id);
    void create(Category category);
    void update(Long id, Category category);
    void delete(Long id);
}
