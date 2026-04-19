package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.ApiResponse;
import com.eims.dto.PageRequest;
import com.eims.entity.InventoryLog;
import com.eims.entity.Material;
import com.eims.repository.InventoryLogRepository;
import com.eims.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private InventoryLogRepository inventoryLogRepository;

    @PostMapping("/in")
    public ApiResponse<Void> inbound(@RequestBody InventoryLog log,
                                     @RequestAttribute("userId") Long userId) {
        Material material = materialRepository.selectById(log.getMaterialId());
        if (material == null) {
            return ApiResponse.error("物资不存在");
        }

        // 更新库存
        BigDecimal beforeStock = material.getCurrentStock();
        BigDecimal afterStock = beforeStock.add(log.getQuantity());
        material.setCurrentStock(afterStock);
        materialRepository.updateById(material);

        // 记录流水
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setType("IN");
        log.setOperatorId(userId);
        log.setCreateTime(LocalDateTime.now());
        inventoryLogRepository.insert(log);

        return ApiResponse.success("入库成功", null);
    }

    @PostMapping("/out")
    public ApiResponse<Void> outbound(@RequestBody InventoryLog log,
                                       @RequestAttribute("userId") Long userId) {
        Material material = materialRepository.selectById(log.getMaterialId());
        if (material == null) {
            return ApiResponse.error("物资不存在");
        }

        // 检查库存
        BigDecimal beforeStock = material.getCurrentStock();
        if (beforeStock.compareTo(log.getQuantity()) < 0) {
            return ApiResponse.error("库存不足");
        }

        // 更新库存
        BigDecimal afterStock = beforeStock.subtract(log.getQuantity());
        material.setCurrentStock(afterStock);
        materialRepository.updateById(material);

        // 记录流水
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setType("OUT");
        log.setOperatorId(userId);
        log.setCreateTime(LocalDateTime.now());
        inventoryLogRepository.insert(log);

        return ApiResponse.success("出库成功", null);
    }

    @GetMapping("/log")
    public ApiResponse<Page<InventoryLog>> log(PageRequest pageRequest) {
        Page<InventoryLog> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<InventoryLog> wrapper = new LambdaQueryWrapper<>();

        if (pageRequest.getKeyword() != null && !pageRequest.getKeyword().isEmpty()) {
            wrapper.eq(InventoryLog::getMaterialId, pageRequest.getKeyword());
        }

        wrapper.orderByDesc(InventoryLog::getCreateTime);
        Page<InventoryLog> result = inventoryLogRepository.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/log/{materialId}")
    public ApiResponse<List<InventoryLog>> logByMaterial(@PathVariable Long materialId) {
        LambdaQueryWrapper<InventoryLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryLog::getMaterialId, materialId);
        wrapper.orderByDesc(InventoryLog::getCreateTime);
        wrapper.last("LIMIT 100");
        List<InventoryLog> list = inventoryLogRepository.selectList(wrapper);
        return ApiResponse.success(list);
    }
}
