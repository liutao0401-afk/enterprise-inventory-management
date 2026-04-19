package com.eims.dto;

import lombok.Data;

@Data
public class PageRequest {
    private int page = 1;
    private int pageSize = 10;
    private String keyword;

    public int getOffset() {
        return (page - 1) * pageSize;
    }
}
