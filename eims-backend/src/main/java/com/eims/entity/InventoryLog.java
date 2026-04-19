package com.eims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory_log")
public class InventoryLog extends BaseEntity {
    private Long materialId;
    private Long warehouseId;
    private String type;
    private BigDecimal quantity;
    private BigDecimal beforeStock;
    private BigDecimal afterStock;
    private String orderCode;
    private String reason;
    private Long operatorId;
    private LocalDateTime createTime;
}
