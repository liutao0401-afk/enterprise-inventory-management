package com.eims.purchase.domain.vo;

import lombok.Data;

@Data
public class DuplicateCheckVO {
    private Long materialId;
    private String materialName;
    private String spec;
    private String purpose;
    private String checkMode;
}
