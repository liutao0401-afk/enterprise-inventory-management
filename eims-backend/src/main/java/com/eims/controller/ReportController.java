package com.eims.controller;

import com.eims.dto.ApiResponse;
import com.eims.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/inventory-ledger")
    public ApiResponse<List<Map<String, Object>>> inventoryLedger(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(reportService.inventoryLedger(warehouseId, categoryId));
    }

    @GetMapping("/purchase-summary")
    public ApiResponse<List<Map<String, Object>>> purchaseSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ApiResponse.success(reportService.purchaseSummary(startDate, endDate));
    }

    @GetMapping("/delivery-rate")
    public ApiResponse<List<Map<String, Object>>> deliveryRate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ApiResponse.success(reportService.deliveryRate(startDate, endDate));
    }

    @GetMapping("/quality-rate")
    public ApiResponse<List<Map<String, Object>>> qualityRate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ApiResponse.success(reportService.qualityRate(startDate, endDate));
    }

    @GetMapping("/turnover-rate")
    public ApiResponse<List<Map<String, Object>>> turnoverRate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ApiResponse.success(reportService.turnoverRate(startDate, endDate));
    }

    @GetMapping("/requirement-funnel")
    public ApiResponse<Map<String, Object>> requirementFunnel() {
        return ApiResponse.success(reportService.requirementFunnel());
    }

    @GetMapping("/supplier-performance")
    public ApiResponse<List<Map<String, Object>>> supplierPerformance() {
        return ApiResponse.success(reportService.supplierPerformance());
    }

    @GetMapping("/inventory-flow")
    public ApiResponse<List<Map<String, Object>>> inventoryFlow(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type) {
        return ApiResponse.success(reportService.inventoryFlow(startDate, endDate, type));
    }

    @GetMapping("/pending-orders")
    public ApiResponse<List<Map<String, Object>>> pendingOrders() {
        return ApiResponse.success(reportService.pendingOrders());
    }
}
