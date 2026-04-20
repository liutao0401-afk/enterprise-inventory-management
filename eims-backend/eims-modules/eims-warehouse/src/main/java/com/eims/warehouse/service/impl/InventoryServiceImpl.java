package com.eims.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eims.material.domain.Material;
import com.eims.material.mapper.MaterialMapper;
import com.eims.warehouse.domain.InventoryLog;
import com.eims.warehouse.domain.vo.InventoryInVO;
import com.eims.warehouse.domain.vo.InventoryOutVO;
import com.eims.warehouse.mapper.InventoryLogMapper;
import com.eims.warehouse.service.IInventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryLogMapper, InventoryLog> implements IInventoryService {

    private final MaterialMapper materialMapper;

    public InventoryServiceImpl(MaterialMapper materialMapper) {
        this.materialMapper = materialMapper;
    }

    @Override
    public IPage<InventoryLog> queryLogPage(InventoryLog query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<InventoryLog> wrapper = new LambdaQueryWrapper<>();
        if (query.getMaterialId() != null) {
            wrapper.eq(InventoryLog::getMaterialId, query.getMaterialId());
        }
        if (StringUtils.hasText(query.getType())) {
            wrapper.eq(InventoryLog::getType, query.getType());
        }
        wrapper.orderByDesc(InventoryLog::getCreateTime);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void in(InventoryInVO vo) {
        Material material = materialMapper.selectById(vo.getMaterialId());
        if (material == null) {
            throw new RuntimeException("物资不存在");
        }

        BigDecimal beforeStock = material.getCurrentStock() != null ? material.getCurrentStock() : BigDecimal.ZERO;
        BigDecimal afterStock = beforeStock.add(vo.getQuantity());

        // 更新库存
        material.setCurrentStock(afterStock);
        materialMapper.updateById(material);

        // 记录流水
        InventoryLog log = new InventoryLog();
        log.setMaterialId(vo.getMaterialId());
        log.setWarehouseId(vo.getWarehouseId());
        log.setType("IN");
        log.setQuantity(vo.getQuantity());
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOrderCode(vo.getOrderCode());
        log.setReason(vo.getReason());
        baseMapper.insert(log);
    }

    @Override
    @Transactional
    public void out(InventoryOutVO vo) {
        Material material = materialMapper.selectById(vo.getMaterialId());
        if (material == null) {
            throw new RuntimeException("物资不存在");
        }

        BigDecimal beforeStock = material.getCurrentStock() != null ? material.getCurrentStock() : BigDecimal.ZERO;
        if (beforeStock.compareTo(vo.getQuantity()) < 0) {
            throw new RuntimeException("库存不足");
        }

        BigDecimal afterStock = beforeStock.subtract(vo.getQuantity());

        // 更新库存
        material.setCurrentStock(afterStock);
        materialMapper.updateById(material);

        // 记录流水
        InventoryLog log = new InventoryLog();
        log.setMaterialId(vo.getMaterialId());
        log.setWarehouseId(vo.getWarehouseId());
        log.setType("OUT");
        log.setQuantity(vo.getQuantity());
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOrderCode(vo.getOrderCode());
        log.setReason(vo.getReason());
        baseMapper.insert(log);
    }

    @Override
    public List<Map<String, Object>> getStockList() {
        List<Material> materials = materialMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Material m : materials) {
            result.add(Map.of(
                "materialId", m.getId(),
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
