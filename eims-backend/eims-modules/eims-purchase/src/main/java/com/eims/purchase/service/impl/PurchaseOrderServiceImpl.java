package com.eims.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eims.purchase.domain.PurchaseOrder;
import com.eims.purchase.domain.PurchaseOrderItem;
import com.eims.purchase.mapper.PurchaseOrderItemMapper;
import com.eims.purchase.mapper.PurchaseOrderMapper;
import com.eims.purchase.service.IPurchaseOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder>
    implements IPurchaseOrderService {

    private final PurchaseOrderItemMapper itemMapper;

    public PurchaseOrderServiceImpl(PurchaseOrderItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public IPage<PurchaseOrder> queryPage(PurchaseOrder query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getCode())) {
            wrapper.like(PurchaseOrder::getCode, query.getCode());
        }
        if (query.getSupplierId() != null) {
            wrapper.eq(PurchaseOrder::getSupplierId, query.getSupplierId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(PurchaseOrder::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<PurchaseOrderItem> getItemsByOrderId(Long orderId) {
        LambdaQueryWrapper<PurchaseOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrderItem::getOrderId, orderId);
        return itemMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveOrder(PurchaseOrder order) {
        if (!StringUtils.hasText(order.getCode())) {
            order.setCode(generateCode());
        }
        if (!StringUtils.hasText(order.getStatus())) {
            order.setStatus("DRAFT");
        }
        baseMapper.insert(order);
    }

    @Override
    public void confirm(Long id) {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(id);
        order.setStatus("CONFIRMED");
        baseMapper.updateById(order);
    }

    @Override
    public void cancel(Long id) {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(id);
        order.setStatus("CANCELLED");
        baseMapper.updateById(order);
    }

    private String generateCode() {
        String prefix = "PO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = baseMapper.selectCount(null) + 1;
        return prefix + "-" + String.format("%03d", count);
    }
}
