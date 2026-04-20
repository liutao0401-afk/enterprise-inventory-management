package com.eims.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.PurchaseOrder;
import com.eims.entity.PurchaseOrderItem;

import java.util.List;

public interface PurchaseOrderService {
    Page<PurchaseOrder> listPage(PageRequest pageRequest);
    List<PurchaseOrder> getAll();
    PurchaseOrder getById(Long id);
    List<PurchaseOrderItem> getItems(Long orderId);
    void create(PurchaseOrder order);
    void update(Long id, PurchaseOrder order);
    void delete(Long id);
    void confirm(Long id);
    void cancel(Long id);
}
