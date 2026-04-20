package com.eims.material.service;

import com.eims.material.domain.Category;
import java.util.List;

public interface ICategoryService {
    List<Category> getTreeList();
    Category getById(Long id);
    void save(Category category);
    void updateById(Category category);
    void removeByIds(List<Long> ids);
}
