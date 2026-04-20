package com.eims.warehouse.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryOutVO {
    private Long materialId;
    private Long warehouseId;
    private BigDecimal quantity;
    private String orderCode;
    private String reason;
}
