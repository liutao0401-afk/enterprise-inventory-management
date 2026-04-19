package com.eims.modules.material.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.modules.material.domain.Material;
import com.eims.modules.material.domain.MaterialQuery;
import com.eims.modules.material.mapper.MaterialMapper;
import com.eims.modules.material.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 物资管理 Service 实现
 */
@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Override
    public Object page(MaterialQuery query) {
        Page<Material> page = new Page<>(query.getPageNum(), query.getPageSize());
        return materialMapper.selectPageByQuery(page, query);
    }

    @Override
    public Material getById(Long id) {
        return materialMapper.selectById(id);
    }

    @Override
    @Transactional
    public Long save(Material material) {
        int rows = materialMapper.insert(material);
        return rows > 0 ? material.getId() : null;
    }

    @Override
    @Transactional
    public boolean updateById(Material material) {
        return materialMapper.updateById(material) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return materialMapper.deleteById(id) > 0;
    }
}
