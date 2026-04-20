package com.eims.service;

import java.util.List;
import java.util.Map;

public interface ReportService {
    // 1. 库存台账
    List<Map<String, Object>> inventoryLedger(Long warehouseId, Long categoryId);

    // 2. 采购汇总表
    List<Map<String, Object>> purchaseSummary(String startDate, String endDate);

    // 3. 到货及时率
    List<Map<String, Object>> deliveryRate(String startDate, String endDate);

    // 4. 质检合格率
    List<Map<String, Object>> qualityRate(String startDate, String endDate);

    // 5. 物资周转率
    List<Map<String, Object>> turnoverRate(String startDate, String endDate);

    // 6. 需求漏斗
    Map<String, Object> requirementFunnel();

    // 7. 供应商绩效
    List<Map<String, Object>> supplierPerformance();

    // 8. 出入库流水
    List<Map<String, Object>> inventoryFlow(String startDate, String endDate, String type);

    // 9. 在途订单
    List<Map<String, Object>> pendingOrders();
}
