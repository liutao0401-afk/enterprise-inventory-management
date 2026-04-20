package com.eims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.PurchaseOrder;
import com.eims.entity.PurchaseOrderItem;
import com.eims.repository.PurchaseOrderItemRepository;
import com.eims.repository.PurchaseOrderRepository;
import com.eims.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository orderRepository;
    @Autowired
    private PurchaseOrderItemRepository itemRepository;

    @Override
    public Page<PurchaseOrder> listPage(PageRequest pageRequest) {
        Page<PurchaseOrder> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(pageRequest.getKeyword())) {
            wrapper.like(PurchaseOrder::getCode, pageRequest.getKeyword());
        }
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);

        return orderRepository.selectPage(page, wrapper);
    }

    @Override
    public List<PurchaseOrder> getAll() {
        return orderRepository.selectList(null);
    }

    @Override
    public PurchaseOrder getById(Long id) {
        return orderRepository.selectById(id);
    }

    @Override
    public List<PurchaseOrderItem> getItems(Long orderId) {
        LambdaQueryWrapper<PurchaseOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrderItem::getOrderId, orderId);
        return itemRepository.selectList(wrapper);
    }

    @Override
    @Transactional
    public void create(PurchaseOrder order) {
        if (!StringUtils.hasText(order.getCode())) {
            order.setCode(generateCode());
        }
        if (!StringUtils.hasText(order.getStatus())) {
            order.setStatus("DRAFT");
        }
        orderRepository.insert(order);
    }

    @Override
    public void update(Long id, PurchaseOrder order) {
        order.setId(id);
        orderRepository.updateById(order);
    }

    @Override
    public void delete(Long id) {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(id);
        order.setStatus("CANCELLED");
        orderRepository.updateById(order);
    }

    @Override
    @Transactional
    public void confirm(Long id) {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(id);
        order.setStatus("CONFIRMED");
        orderRepository.updateById(order);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(id);
        order.setStatus("CANCELLED");
        orderRepository.updateById(order);
    }

    private String generateCode() {
        String prefix = "PO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = orderRepository.selectCount(null) + 1;
        return prefix + "-" + String.format("%03d", count);
    }
}
