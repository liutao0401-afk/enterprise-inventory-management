package com.eims.qc.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.eims.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qc_record")
public class QcRecord extends BaseEntity {

    private String code;
    private Long materialId;
    private Long orderId;
    private String batchNo;
    private BigDecimal quantity;
    private BigDecimal sampleQty;
    private String result;
    private BigDecimal qualifiedQty;
    private BigDecimal unqualifiedQty;
    private String unqualifiedReason;
    private Long inspectorId;
    private LocalDateTime inspectTime;
    private String remark;
}
