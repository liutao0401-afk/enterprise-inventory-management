package com.eims.purchase.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.eims.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_order")
public class PurchaseOrder extends BaseEntity {

    private String code;
    private Long supplierId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDate expectedDate;
    private LocalDate actualDate;
    private String contactName;
    private String contactPhone;
    private String deliveryAddress;
    private String paymentTerms;
    private String remark;
    private Long createUserId;
}
