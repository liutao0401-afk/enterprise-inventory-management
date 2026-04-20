package com.eims.purchase.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.eims.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("requirement")
public class Requirement extends BaseEntity {

    private String code;
    private Long materialId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String purpose;
    private LocalDate expectedDate;
    private String deptName;
    private Long requesterId;
    private String checkMode;
    private String checkResult;
    private String status;
    private Long approvalId;
    private String approvalTime;
    private String approvalComment;
}
