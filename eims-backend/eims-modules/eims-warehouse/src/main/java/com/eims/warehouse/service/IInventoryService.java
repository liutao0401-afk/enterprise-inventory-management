package com.eims.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eims.warehouse.domain.InventoryLog;
import com.eims.warehouse.domain.vo.InventoryInVO;
import com.eims.warehouse.domain.vo.InventoryOutVO;
import java.util.List;
import java.util.Map;

public interface IInventoryService {
    IPage<InventoryLog> queryLogPage(InventoryLog query, Integer pageNum, Integer pageSize);
    void in(InventoryInVO vo);
    void out(InventoryOutVO vo);
    List<Map<String, Object>> getStockList();
}
