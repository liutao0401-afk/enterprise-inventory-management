package com.eims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.Warehouse;
import com.eims.repository.WarehouseRepository;
import com.eims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Override
    public Page<Warehouse> listPage(PageRequest pageRequest) {
        Page<Warehouse> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(pageRequest.getKeyword())) {
            wrapper.like(Warehouse::getName, pageRequest.getKeyword());
        }
        wrapper.eq(Warehouse::getStatus, "ACTIVE");
        wrapper.orderByDesc(Warehouse::getCreateTime);

        return warehouseRepository.selectPage(page, wrapper);
    }

    @Override
    public List<Warehouse> getAll() {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Warehouse::getStatus, "ACTIVE");
        wrapper.orderByAsc(Warehouse::getName);
        return warehouseRepository.selectList(wrapper);
    }

    @Override
    public Warehouse getById(Long id) {
        return warehouseRepository.selectById(id);
    }

    @Override
    public void create(Warehouse warehouse) {
        warehouseRepository.insert(warehouse);
    }

    @Override
    public void update(Long id, Warehouse warehouse) {
        warehouse.setId(id);
        warehouseRepository.updateById(warehouse);
    }

    @Override
    public void delete(Long id) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setStatus("INACTIVE");
        warehouseRepository.updateById(warehouse);
    }
}
