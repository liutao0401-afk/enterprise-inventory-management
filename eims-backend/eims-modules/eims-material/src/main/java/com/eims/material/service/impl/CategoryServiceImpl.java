package com.eims.material.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eims.material.domain.Category;
import com.eims.material.mapper.CategoryMapper;
import com.eims.material.service.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Override
    public List<Category> getTreeList() {
        List<Category> all = list();
        return buildTree(all, 0L);
    }

    private List<Category> buildTree(List<Category> all, Long parentId) {
        return all.stream()
            .filter(c -> c.getParentId().equals(parentId))
            .peek(c -> c.setChildren(buildTree(all, c.getId())))
            .collect(Collectors.toList());
    }
}
