package com.eims.purchase.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.eims.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_order_item")
public class PurchaseOrderItem extends BaseEntity {

    private Long orderId;
    private Long materialId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal shippedQty;
    private BigDecimal receivedQty;
}
