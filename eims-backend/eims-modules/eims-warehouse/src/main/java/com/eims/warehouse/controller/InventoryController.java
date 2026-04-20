package com.eims.warehouse.controller;

import com.eims.common.core.domain.Result;
import com.eims.warehouse.domain.InventoryLog;
import com.eims.warehouse.domain.vo.InventoryInVO;
import com.eims.warehouse.domain.vo.InventoryOutVO;
import com.eims.warehouse.service.IInventoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final IInventoryService inventoryService;

    @GetMapping("/log")
    public Result<IPage<InventoryLog>> logList(InventoryLog query,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(inventoryService.queryLogPage(query, pageNum, pageSize));
    }

    @PostMapping("/in")
    public Result<Void> in(@RequestBody InventoryInVO vo) {
        inventoryService.in(vo);
        return Result.ok();
    }

    @PostMapping("/out")
    public Result<Void> out(@RequestBody InventoryOutVO vo) {
        inventoryService.out(vo);
        return Result.ok();
    }

    @GetMapping("/stock")
    public Result<List<Map<String, Object>>> getStockList() {
        return Result.ok(inventoryService.getStockList());
    }
}
