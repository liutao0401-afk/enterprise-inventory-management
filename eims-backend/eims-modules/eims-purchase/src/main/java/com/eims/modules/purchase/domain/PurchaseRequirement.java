package com.eims.modules.purchase.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.eims.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购需求表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("p_purchase_requirement")
public class PurchaseRequirement extends BaseEntity {

    /** 需求单号 */
    private String requirementCode;

    /** 需求部门ID */
    private Long departmentId;

    /** 申请人ID */
    private Long applicantId;

    /** 物资ID */
    private Long materialId;

    /** 需求数量 */
    private BigDecimal quantity;

    /** 期望单价 */
    private BigDecimal unitPrice;

    /** 期望交货日期 */
    private LocalDate expectedDate;

    /** 优先级 1-10 */
    private Integer priority = 5;

    /** 采购用途 */
    private String purpose;

    /** 状态 */
    private String status = "PENDING";

    /** 重复检测结果 */
    private String duplicateCheckResult;

    /** 需求内容哈希 */
    private String requirementHash;
}
