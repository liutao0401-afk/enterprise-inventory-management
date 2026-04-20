package com.eims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.Supplier;
import com.eims.repository.SupplierRepository;
import com.eims.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public Page<Supplier> listPage(PageRequest pageRequest) {
        Page<Supplier> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(pageRequest.getKeyword())) {
            wrapper.and(w -> w.like(Supplier::getName, pageRequest.getKeyword())
                    .or().like(Supplier::getCode, pageRequest.getKeyword()));
        }
        wrapper.eq(Supplier::getStatus, "ACTIVE");
        wrapper.orderByDesc(Supplier::getCreateTime);

        return supplierRepository.selectPage(page, wrapper);
    }

    @Override
    public List<Supplier> getAll() {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getStatus, "ACTIVE");
        wrapper.orderByAsc(Supplier::getName);
        return supplierRepository.selectList(wrapper);
    }

    @Override
    public Supplier getById(Long id) {
        return supplierRepository.selectById(id);
    }

    @Override
    public void create(Supplier supplier) {
        supplierRepository.insert(supplier);
    }

    @Override
    public void update(Long id, Supplier supplier) {
        supplier.setId(id);
        supplierRepository.updateById(supplier);
    }

    @Override
    public void delete(Long id) {
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setStatus("INACTIVE");
        supplierRepository.updateById(supplier);
    }
}
