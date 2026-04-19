package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.ApiResponse;
import com.eims.dto.PageRequest;
import com.eims.entity.QcRecord;
import com.eims.repository.QcRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/qc")
public class QcController {

    @Autowired
    private QcRecordRepository qcRecordRepository;

    @GetMapping("/list")
    public ApiResponse<Page<QcRecord>> list(PageRequest pageRequest) {
        Page<QcRecord> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<QcRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(QcRecord::getCreateTime);
        Page<QcRecord> result = qcRecordRepository.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<QcRecord> getById(@PathVariable Long id) {
        QcRecord record = qcRecordRepository.selectById(id);
        if (record == null) {
            return ApiResponse.error("质检记录不存在");
        }
        return ApiResponse.success(record);
    }

    @PostMapping
    public ApiResponse<QcRecord> create(@RequestBody QcRecord record,
                                        @RequestAttribute("userId") Long userId) {
        // 生成质检单号
        String code = generateCode();
        record.setCode(code);
        record.setInspectorId(userId);
        record.setInspectTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());

        qcRecordRepository.insert(record);
        return ApiResponse.success("质检记录已创建", record);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody QcRecord record) {
        record.setId(id);
        qcRecordRepository.updateById(record);
        return ApiResponse.success("更新成功", null);
    }

    private String generateCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<QcRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(QcRecord::getCode, "QC-" + date);
        wrapper.orderByDesc(QcRecord::getCode);
        wrapper.last("LIMIT 1");

        QcRecord last = qcRecordRepository.selectOne(wrapper);
        int seq = 1;
        if (last != null) {
            String lastCode = last.getCode();
            String lastSeq = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            seq = Integer.parseInt(lastSeq) + 1;
        }

        return String.format("QC-%s-%03d", date, seq);
    }
}
