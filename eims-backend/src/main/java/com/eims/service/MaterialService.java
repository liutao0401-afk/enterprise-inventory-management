package com.eims.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.Material;

import java.util.List;

public interface MaterialService {
    Page<Material> listPage(PageRequest pageRequest);
    List<Material> getAll();
    Material getById(Long id);
    void create(Material material);
    void update(Long id, Material material);
    void delete(Long id);
    List<Material> getLowStock();
}
