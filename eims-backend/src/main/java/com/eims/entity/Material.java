package com.eims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("material")
public class Material extends BaseEntity {
    private String code;
    private String name;
    private Long categoryId;
    private String unit;
    private String spec;
    private BigDecimal safetyStock;
    private BigDecimal minStock;
    private BigDecimal currentStock;
    private BigDecimal price;
    private Long supplierId;
    private Long warehouseId;
    private String location;
    private String description;
    private String status;
}
