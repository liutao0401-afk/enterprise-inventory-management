package com.eims.warehouse.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.eims.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse")
public class Warehouse extends BaseEntity {

    private String code;
    private String name;
    private String address;
    private Long managerId;
    private BigDecimal capacity;
    private String description;
    private String status;
}
