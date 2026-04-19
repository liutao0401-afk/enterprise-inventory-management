package com.eims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime approvalTime;
    private String approvalComment;
}
