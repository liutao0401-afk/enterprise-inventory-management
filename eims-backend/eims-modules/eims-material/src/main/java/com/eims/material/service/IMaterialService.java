package com.eims.material.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eims.material.domain.Material;
import java.util.List;

public interface IMaterialService {
    IPage<Material> queryPage(Material query, Integer pageNum, Integer pageSize);
    Material getById(Long id);
    void save(Material material);
    void updateById(Material material);
    void removeByIds(List<Long> ids);
    String generateCode();
}
