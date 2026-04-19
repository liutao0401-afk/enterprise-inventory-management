package com.eims.modules.material.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.eims.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 物资主表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("m_material")
public class Material extends BaseEntity {

    /** 物资编码 */
    private String materialCode;

    /** 物资名称 */
    private String materialName;

    /** 分类ID */
    private Long categoryId;

    /** 计量单位 */
    private String unit;

    /** 规格型号 */
    private String spec;

    /** 安全库存 */
    private BigDecimal safetyStock = BigDecimal.ZERO;

    /** 最大库存 */
    private BigDecimal maxStock = BigDecimal.ZERO;

    /** 当前库存 */
    private BigDecimal currentStock = BigDecimal.ZERO;

    /** 再订货点 */
    private BigDecimal reorderPoint = BigDecimal.ZERO;

    /** 采购提前期（天） */
    private Integer leadTimeDays = 0;

    /** 参考单价 */
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /** 库位编码 */
    private String locationCode;
}
