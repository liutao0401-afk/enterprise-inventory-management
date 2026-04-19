package com.eims.modules.material.domain;

import lombok.Data;

/**
 * 物资查询条件
 */
@Data
public class MaterialQuery {

    private String keyword;           // 关键词（名称/编码）
    private Long categoryId;          // 分类ID
    private String warehouseCode;     // 仓库编码
    private String warehouseName;     // 仓库名称
    private String type;            // 查询类型：ALL-全部，LOW_STOCK-库存不足
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
