package com.eims.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.InventoryLog;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    Page<InventoryLog> logPage(PageRequest pageRequest);
    void inStock(Map<String, Object> params);
    void outStock(Map<String, Object> params);
    List<Map<String, Object>> getStockList();
}
