package com.eims.purchase.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.eims.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private String onTimeRate;
    private String qualifyRate;
    private String bankName;
    private String bankAccount;
    private String status;
}
