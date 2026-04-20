package com.eims.purchase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eims.purchase.domain.PurchaseOrder;
import com.eims.purchase.domain.PurchaseOrderItem;
import java.util.List;

public interface IPurchaseOrderService {
    IPage<PurchaseOrder> queryPage(PurchaseOrder query, Integer pageNum, Integer pageSize);
    PurchaseOrder getById(Long id);
    List<PurchaseOrderItem> getItemsByOrderId(Long orderId);
    void saveOrder(PurchaseOrder order);
    void updateById(PurchaseOrder order);
    void removeByIds(List<Long> ids);
    void confirm(Long id);
    void cancel(Long id);
}
