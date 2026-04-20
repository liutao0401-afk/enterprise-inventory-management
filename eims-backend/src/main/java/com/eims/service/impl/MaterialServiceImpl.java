package com.eims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.Material;
import com.eims.repository.MaterialRepository;
import com.eims.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public Page<Material> listPage(PageRequest pageRequest) {
        Page<Material> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(pageRequest.getKeyword())) {
            wrapper.and(w -> w.like(Material::getName, pageRequest.getKeyword())
                    .or().like(Material::getCode, pageRequest.getKeyword()));
        }
        wrapper.eq(Material::getStatus, "ACTIVE");
        wrapper.orderByDesc(Material::getCreateTime);

        return materialRepository.selectPage(page, wrapper);
    }

    @Override
    public List<Material> getAll() {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, "ACTIVE");
        wrapper.orderByAsc(Material::getName);
        return materialRepository.selectList(wrapper);
    }

    @Override
    public Material getById(Long id) {
        return materialRepository.selectById(id);
    }

    @Override
    public void create(Material material) {
        if (!StringUtils.hasText(material.getCode())) {
            material.setCode(generateCode());
        }
        materialRepository.insert(material);
    }

    @Override
    public void update(Long id, Material material) {
        material.setId(id);
        materialRepository.updateById(material);
    }

    @Override
    public void delete(Long id) {
        Material material = new Material();
        material.setId(id);
        material.setStatus("INACTIVE");
        materialRepository.updateById(material);
    }

    @Override
    public List<Material> getLowStock() {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, "ACTIVE");
        wrapper.le("current_stock", 1);
        return materialRepository.selectList(wrapper);
    }

    private String generateCode() {
        String prefix = "MAT-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = materialRepository.selectCount(null) + 1;
        return prefix + String.format("%04d", count);
    }
}
