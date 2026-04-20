package com.eims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.InventoryLog;
import com.eims.entity.Material;
import com.eims.repository.InventoryLogRepository;
import com.eims.repository.MaterialRepository;
import com.eims.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryLogRepository logRepository;
    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public Page<InventoryLog> logPage(PageRequest pageRequest) {
        Page<InventoryLog> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<InventoryLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(InventoryLog::getCreateTime);
        return logRepository.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void inStock(Map<String, Object> params) {
        Long materialId = Long.valueOf(params.get("materialId").toString());
        Long warehouseId = params.get("warehouseId") != null ? Long.valueOf(params.get("warehouseId").toString()) : null;
        BigDecimal quantity = new BigDecimal(params.get("quantity").toString());
        String orderCode = params.get("orderCode") != null ? params.get("orderCode").toString() : null;
        String reason = params.get("reason") != null ? params.get("reason").toString() : "采购入库";

        Material material = materialRepository.selectById(materialId);
        if (material == null) {
            throw new RuntimeException("物资不存在");
        }

        BigDecimal beforeStock = material.getCurrentStock() != null ? material.getCurrentStock() : BigDecimal.ZERO;
        BigDecimal afterStock = beforeStock.add(quantity);

        material.setCurrentStock(afterStock);
        materialRepository.updateById(material);

        InventoryLog log = new InventoryLog();
        log.setMaterialId(materialId);
        log.setWarehouseId(warehouseId);
        log.setType("IN");
        log.setQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOrderCode(orderCode);
        log.setReason(reason);
        logRepository.insert(log);
    }

    @Override
    @Transactional
    public void outStock(Map<String, Object> params) {
        Long materialId = Long.valueOf(params.get("materialId").toString());
        Long warehouseId = params.get("warehouseId") != null ? Long.valueOf(params.get("warehouseId").toString()) : null;
        BigDecimal quantity = new BigDecimal(params.get("quantity").toString());
        String orderCode = params.get("orderCode") != null ? params.get("orderCode").toString() : null;
        String reason = params.get("reason") != null ? params.get("reason").toString() : "领用出库";

        Material material = materialRepository.selectById(materialId);
        if (material == null) {
            throw new RuntimeException("物资不存在");
        }

        BigDecimal beforeStock = material.getCurrentStock() != null ? material.getCurrentStock() : BigDecimal.ZERO;
        if (beforeStock.compareTo(quantity) < 0) {
            throw new RuntimeException("库存不足");
        }
        BigDecimal afterStock = beforeStock.subtract(quantity);

        material.setCurrentStock(afterStock);
        materialRepository.updateById(material);

        InventoryLog log = new InventoryLog();
        log.setMaterialId(materialId);
        log.setWarehouseId(warehouseId);
        log.setType("OUT");
        log.setQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOrderCode(orderCode);
        log.setReason(reason);
        logRepository.insert(log);
    }

    @Override
    public List<Map<String, Object>> getStockList() {
        List<Material> materials = materialRepository.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Material m : materials) {
            result.add(Map.of(
                "materialId", m.getId() != null ? m.getId() : 0L,
                "materialCode", m.getCode() != null ? m.getCode() : "",
                "materialName", m.getName() != null ? m.getName() : "",
                "currentStock", m.getCurrentStock() != null ? m.getCurrentStock() : BigDecimal.ZERO,
                "safetyStock", m.getSafetyStock() != null ? m.getSafetyStock() : BigDecimal.ZERO,
                "minStock", m.getMinStock() != null ? m.getMinStock() : BigDecimal.ZERO,
                "status", getStockStatus(m)
            ));
        }
        return result;
    }

    private String getStockStatus(Material m) {
        BigDecimal current = m.getCurrentStock() != null ? m.getCurrentStock() : BigDecimal.ZERO;
        BigDecimal min = m.getMinStock() != null ? m.getMinStock() : BigDecimal.ZERO;
        BigDecimal safety = m.getSafetyStock() != null ? m.getSafetyStock() : BigDecimal.ZERO;

        if (current.compareTo(BigDecimal.ZERO) == 0) {
            return "CRITICAL";
        } else if (current.compareTo(min) <= 0) {
            return "WARNING";
        } else if (current.compareTo(safety) <= 0) {
            return "INFO";
        }
        return "NORMAL";
    }
}
