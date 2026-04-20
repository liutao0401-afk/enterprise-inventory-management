package com.eims.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eims.warehouse.domain.Warehouse;
import com.eims.warehouse.mapper.WarehouseMapper;
import com.eims.warehouse.service.IWarehouseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements IWarehouseService {

    @Override
    public IPage<Warehouse> queryPage(Warehouse query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Warehouse::getName, query.getName());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Warehouse::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Warehouse::getCreateTime);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Warehouse> getAll() {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Warehouse::getStatus, "ACTIVE");
        return baseMapper.selectList(wrapper);
    }
}
