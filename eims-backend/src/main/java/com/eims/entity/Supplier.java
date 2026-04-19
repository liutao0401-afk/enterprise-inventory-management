package com.eims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("supplier")
public class Supplier extends BaseEntity {
    private String code;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String address;
    private String mainProduct;
    private String level;
    private BigDecimal onTimeRate;
    private BigDecimal qualifyRate;
    private String bankName;
    private String bankAccount;
    private String status;
}
