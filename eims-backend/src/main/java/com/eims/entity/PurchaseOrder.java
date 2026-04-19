package com.eims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
