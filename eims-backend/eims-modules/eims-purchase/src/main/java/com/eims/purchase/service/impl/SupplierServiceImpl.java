package com.eims.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eims.purchase.domain.Supplier;
import com.eims.purchase.mapper.SupplierMapper;
import com.eims.purchase.service.ISupplierService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

    @Override
    public IPage<Supplier> queryPage(Supplier query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Supplier::getName, query.getName());
        }
        if (StringUtils.hasText(query.getLevel())) {
            wrapper.eq(Supplier::getLevel, query.getLevel());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Supplier::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Supplier::getCreateTime);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Supplier> getAll() {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getStatus, "ACTIVE");
        return baseMapper.selectList(wrapper);
    }
}
