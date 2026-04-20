package com.eims.qc.controller;

import com.eims.common.core.domain.Result;
import com.eims.qc.domain.QcRecord;
import com.eims.qc.service.IQcRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qc")
@RequiredArgsConstructor
public class QCController {

    private final IQcRecordService qcRecordService;

    @GetMapping
    public Result<IPage<QcRecord>> list(QcRecord query,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(qcRecordService.queryPage(query, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<QcRecord> getInfo(@PathVariable Long id) {
        return Result.ok(qcRecordService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody QcRecord record) {
        qcRecordService.saveRecord(record);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody QcRecord record) {
        qcRecordService.updateById(record);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        qcRecordService.removeByIds(ids);
        return Result.ok();
    }
}
