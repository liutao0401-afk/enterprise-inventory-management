package com.eims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eims.entity.*;
import com.eims.repository.*;
import com.eims.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private PurchaseOrderRepository orderRepository;
    @Autowired
    private PurchaseOrderItemRepository orderItemRepository;
    @Autowired
    private RequirementRepository requirementRepository;
    @Autowired
    private InventoryLogRepository inventoryLogRepository;
    @Autowired
    private QcRecordRepository qcRecordRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public List<Map<String, Object>> inventoryLedger(Long warehouseId, Long categoryId) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, "ACTIVE");
        if (warehouseId != null) {
            wrapper.eq(Material::getWarehouseId, warehouseId);
        }
        if (categoryId != null) {
            wrapper.eq(Material::getCategoryId, categoryId);
        }

        List<Material> materials = materialRepository.selectList(wrapper);
        return materials.stream().map(m -> {
            String status = getStockStatus(m);
            return Map.of(
                "code", m.getCode() != null ? m.getCode() : "",
                "name", m.getName() != null ? m.getName() : "",
                "spec", m.getSpec() != null ? m.getSpec() : "",
                "unit", m.getUnit() != null ? m.getUnit() : "",
                "currentStock", m.getCurrentStock() != null ? m.getCurrentStock() : BigDecimal.ZERO,
                "safetyStock", m.getSafetyStock() != null ? m.getSafetyStock() : BigDecimal.ZERO,
                "minStock", m.getMinStock() != null ? m.getMinStock() : BigDecimal.ZERO,
                "price", m.getPrice() != null ? m.getPrice() : BigDecimal.ZERO,
                "status", status
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> purchaseSummary(String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<PurchaseOrder> orders = orderRepository.selectList(
            new LambdaQueryWrapper<PurchaseOrder>()
                .ge(PurchaseOrder::getCreateTime, start.atStartOfDay())
                .le(PurchaseOrder::getCreateTime, end.plusDays(1).atStartOfDay())
        );

        // 按月汇总
        Map<String, BigDecimal> monthlyAmount = new TreeMap<>();
        for (PurchaseOrder order : orders) {
            String month = order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            monthlyAmount.merge(month, order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO, BigDecimal::add);
        }

        return monthlyAmount.entrySet().stream().map(e ->
            Map.of("month", e.getKey(), "amount", e.getValue())
        ).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> deliveryRate(String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<PurchaseOrder> orders = orderRepository.selectList(
            new LambdaQueryWrapper<PurchaseOrder>()
                .ge(PurchaseOrder::getCreateTime, start.atStartOfDay())
                .le(PurchaseOrder::getCreateTime, end.plusDays(1).atStartOfDay())
                .in(PurchaseOrder::getStatus, "CONFIRMED", "SHIPPED", "RECEIVED", "COMPLETED")
        );

        Map<Long, Map<String, Object>> supplierStats = new HashMap<>();

        for (PurchaseOrder order : orders) {
            Supplier supplier = supplierRepository.selectById(order.getSupplierId());
            if (supplier == null) continue;

            supplierStats.computeIfAbsent(supplier.getId(), k -> {
                Map<String, Object> stats = new HashMap<>();
                stats.put("supplierId", supplier.getId());
                stats.put("supplierName", supplier.getName());
                stats.put("totalOrders", 0);
                stats.put("onTimeOrders", 0);
                return stats;
            });

            Map<String, Object> stats = supplierStats.get(order.getSupplierId());
            int totalOrders = (int) stats.get("totalOrders") + 1;
            stats.put("totalOrders", totalOrders);

            // 判断是否准时
            if (order.getActualDate() != null && order.getExpectedDate() != null) {
                if (!order.getActualDate().isAfter(order.getExpectedDate())) {
                    stats.put("onTimeOrders", (int) stats.get("onTimeOrders") + 1);
                }
            }

            // 更新供应商准时率
            int onTime = (int) stats.get("onTimeOrders");
            BigDecimal rate = totalOrders > 0 ?
                BigDecimal.valueOf(onTime * 100.0 / totalOrders).setScale(2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;
            stats.put("onTimeRate", rate);
        }

        return new ArrayList<>(supplierStats.values());
    }

    @Override
    public List<Map<String, Object>> qualityRate(String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<QcRecord> records = qcRecordRepository.selectList(
            new LambdaQueryWrapper<QcRecord>()
                .ge(QcRecord::getCreateTime, start.atStartOfDay())
                .le(QcRecord::getCreateTime, end.plusDays(1).atStartOfDay())
        );

        Map<String, Map<String, Object>> materialStats = new LinkedHashMap<>();

        for (QcRecord record : records) {
            Material material = materialRepository.selectById(record.getMaterialId());
            String materialName = material != null && material.getName() != null ? material.getName() : "未知物资";

            materialStats.computeIfAbsent(record.getMaterialId().toString(), k -> {
                Map<String, Object> stats = new HashMap<>();
                stats.put("materialId", record.getMaterialId());
                stats.put("materialName", materialName);
                stats.put("totalQty", BigDecimal.ZERO);
                stats.put("qualifiedQty", BigDecimal.ZERO);
                return stats;
            });

            Map<String, Object> stats = materialStats.get(record.getMaterialId().toString());
            BigDecimal totalQty = (BigDecimal) stats.get("totalQty");
            BigDecimal qualifiedQty = (BigDecimal) stats.get("qualifiedQty");

            if (record.getQuantity() != null) {
                stats.put("totalQty", totalQty.add(record.getQuantity()));
            }
            if ("PASS".equals(record.getResult()) && record.getQualifiedQty() != null) {
                stats.put("qualifiedQty", qualifiedQty.add(record.getQualifiedQty()));
            }
        }

        return materialStats.values().stream().map(stats -> {
            BigDecimal total = (BigDecimal) stats.get("totalQty");
            BigDecimal qualified = (BigDecimal) stats.get("qualifiedQty");
            BigDecimal rate = total.compareTo(BigDecimal.ZERO) > 0 ?
                qualified.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;
            stats.put("qualifyRate", rate);
            stats.remove("totalQty");
            stats.remove("qualifiedQty");
            return stats;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> turnoverRate(String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<InventoryLog> logs = inventoryLogRepository.selectList(
            new LambdaQueryWrapper<InventoryLog>()
                .ge(InventoryLog::getCreateTime, start.atStartOfDay())
                .le(InventoryLog::getCreateTime, end.plusDays(1).atStartOfDay())
                .eq(InventoryLog::getType, "OUT")
        );

        Map<Long, BigDecimal> outAmountMap = new HashMap<>();
        for (InventoryLog log : logs) {
            outAmountMap.merge(log.getMaterialId(),
                log.getQuantity() != null ? log.getQuantity() : BigDecimal.ZERO,
                BigDecimal::add);
        }

        List<Material> materials = materialRepository.selectList(
            new LambdaQueryWrapper<Material>().eq(Material::getStatus, "ACTIVE")
        );

        return materials.stream().map(m -> {
            BigDecimal outAmount = outAmountMap.getOrDefault(m.getId(), BigDecimal.ZERO);
            BigDecimal avgStock = m.getCurrentStock() != null ? m.getCurrentStock() : BigDecimal.ZERO;
            long days = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;

            BigDecimal turnoverDays = BigDecimal.ZERO;
            if (avgStock.compareTo(BigDecimal.ZERO) > 0) {
                turnoverDays = avgStock.multiply(BigDecimal.valueOf(days))
                    .divide(outAmount.compareTo(BigDecimal.ZERO) > 0 ? outAmount : BigDecimal.ONE, 2, RoundingMode.HALF_UP);
            }

            return Map.of(
                "materialCode", m.getCode() != null ? m.getCode() : "",
                "materialName", m.getName() != null ? m.getName() : "",
                "currentStock", m.getCurrentStock() != null ? m.getCurrentStock() : BigDecimal.ZERO,
                "outAmount", outAmount,
                "turnoverDays", turnoverDays
            );
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> requirementFunnel() {
        List<Requirement> all = requirementRepository.selectList(null);

        long total = all.size();
        long pending = all.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        long approved = all.stream().filter(r -> "APPROVED".equals(r.getStatus())).count();
        long purchasing = all.stream().filter(r -> "PURCHASING".equals(r.getStatus())).count();
        long completed = all.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count();
        long rejected = all.stream().filter(r -> "REJECTED".equals(r.getStatus())).count();

        return Map.of(
            "total", total,
            "pending", pending,
            "approved", approved,
            "purchasing", purchasing,
            "completed", completed,
            "rejected", rejected,
            "pendingRate", total > 0 ? BigDecimal.valueOf(pending * 100.0 / total).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
            "approvedRate", total > 0 ? BigDecimal.valueOf(approved * 100.0 / total).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
            "completionRate", total > 0 ? BigDecimal.valueOf(completed * 100.0 / total).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO
        );
    }

    @Override
    public List<Map<String, Object>> supplierPerformance() {
        List<Supplier> suppliers = supplierRepository.selectList(
            new LambdaQueryWrapper<Supplier>().eq(Supplier::getStatus, "ACTIVE")
        );

        return suppliers.stream().map(s -> {
            // 计算综合评分
            BigDecimal onTimeRate = s.getOnTimeRate() != null ? new BigDecimal(s.getOnTimeRate()) : BigDecimal.ZERO;
            BigDecimal qualifyRate = s.getQualifyRate() != null ? new BigDecimal(s.getQualifyRate()) : BigDecimal.ZERO;

            // 综合评级 = (准时率×0.4) + (合格率×0.6)
            BigDecimal score = onTimeRate.multiply(BigDecimal.valueOf(0.4))
                .add(qualifyRate.multiply(BigDecimal.valueOf(0.6)))
                .setScale(2, RoundingMode.HALF_UP);

            return Map.of(
                "supplierId", s.getId(),
                "supplierName", s.getName(),
                "level", s.getLevel() != null ? s.getLevel() : "",
                "onTimeRate", onTimeRate,
                "qualifyRate", qualifyRate,
                "score", score
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> inventoryFlow(String startDate, String endDate, String type) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        LambdaQueryWrapper<InventoryLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(InventoryLog::getCreateTime, start.atStartOfDay());
        wrapper.le(InventoryLog::getCreateTime, end.plusDays(1).atStartOfDay());
        if (type != null && !type.isEmpty()) {
            wrapper.eq(InventoryLog::getType, type);
        }
        wrapper.orderByDesc(InventoryLog::getCreateTime);

        List<InventoryLog> logs = inventoryLogRepository.selectList(wrapper);

        return logs.stream().map(log -> {
            Material material = materialRepository.selectById(log.getMaterialId());
            return Map.of(
                "logId", log.getId(),
                "materialCode", material != null && material.getCode() != null ? material.getCode() : "",
                "materialName", material != null && material.getName() != null ? material.getName() : "",
                "type", log.getType() != null ? log.getType() : "",
                "quantity", log.getQuantity() != null ? log.getQuantity() : BigDecimal.ZERO,
                "beforeStock", log.getBeforeStock() != null ? log.getBeforeStock() : BigDecimal.ZERO,
                "afterStock", log.getAfterStock() != null ? log.getAfterStock() : BigDecimal.ZERO,
                "orderCode", log.getOrderCode() != null ? log.getOrderCode() : "",
                "reason", log.getReason() != null ? log.getReason() : "",
                "createTime", log.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> pendingOrders() {
        List<PurchaseOrder> orders = orderRepository.selectList(
            new LambdaQueryWrapper<PurchaseOrder>()
                .in(PurchaseOrder::getStatus, "DRAFT", "CONFIRMED", "PARTIAL_SHIPPED", "SHIPPED")
                .orderByAsc(PurchaseOrder::getExpectedDate)
        );

        return orders.stream().map(order -> {
            Supplier supplier = supplierRepository.selectById(order.getSupplierId());
            List<PurchaseOrderItem> items = orderItemRepository.selectList(
                new LambdaQueryWrapper<PurchaseOrderItem>()
                    .eq(PurchaseOrderItem::getOrderId, order.getId())
            );

            return Map.of(
                "orderId", order.getId(),
                "orderCode", order.getCode() != null ? order.getCode() : "",
                "supplierName", supplier != null ? supplier.getName() : "",
                "status", order.getStatus() != null ? order.getStatus() : "",
                "totalAmount", order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO,
                "expectedDate", order.getExpectedDate() != null ? order.getExpectedDate().toString() : "",
                "itemCount", items.size()
            );
        }).collect(Collectors.toList());
    }

    private String getStockStatus(Material m) {
        BigDecimal current = m.getCurrentStock() != null ? m.getCurrentStock() : BigDecimal.ZERO;
        BigDecimal min = m.getMinStock() != null ? m.getMinStock() : BigDecimal.ZERO;
        BigDecimal safety = m.getSafetyStock() != null ? m.getSafetyStock() : BigDecimal.ZERO;

        if (current.compareTo(BigDecimal.ZERO) == 0) return "CRITICAL";
        if (current.compareTo(min) <= 0) return "WARNING";
        if (current.compareTo(safety) <= 0) return "INFO";
        return "NORMAL";
    }
}
