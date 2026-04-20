package com.eims.service.impl;

import com.eims.entity.Category;
import com.eims.repository.CategoryRepository;
import com.eims.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getTreeList() {
        List<Category> all = categoryRepository.selectList(null);
        return buildTree(all, 0L);
    }

    private List<Category> buildTree(List<Category> all, Long parentId) {
        return all.stream()
            .filter(c -> c.getParentId().equals(parentId))
            .peek(c -> c.setChildren(buildTree(all, c.getId())))
            .collect(Collectors.toList());
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.selectById(id);
    }

    @Override
    public void create(Category category) {
        categoryRepository.insert(category);
    }

    @Override
    public void update(Long id, Category category) {
        category.setId(id);
        categoryRepository.updateById(category);
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
