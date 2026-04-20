package com.eims.warehouse.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryInVO {
    private Long materialId;
    private Long warehouseId;
    private BigDecimal quantity;
    private String orderCode;
    private String reason;
}
