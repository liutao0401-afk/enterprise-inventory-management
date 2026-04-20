package com.eims.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eims.material.domain.Material;
import com.eims.material.mapper.MaterialMapper;
import com.eims.material.service.IMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements IMaterialService {

    @Override
    public IPage<Material> queryPage(Material query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getCode())) {
            wrapper.like(Material::getCode, query.getCode());
        }
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Material::getName, query.getName());
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(Material::getCategoryId, query.getCategoryId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Material::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Material::getCreateTime);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public String generateCode() {
        String prefix = "MAT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = baseMapper.selectCount(null) + 1;
        return prefix + String.format("%04d", count);
    }
}
