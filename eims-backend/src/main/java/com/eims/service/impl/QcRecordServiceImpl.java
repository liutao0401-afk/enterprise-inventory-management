package com.eims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.QcRecord;
import com.eims.repository.QcRecordRepository;
import com.eims.service.QcRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class QcRecordServiceImpl implements QcRecordService {

    @Autowired
    private QcRecordRepository qcRecordRepository;

    @Override
    public Page<QcRecord> listPage(PageRequest pageRequest) {
        Page<QcRecord> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<QcRecord> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(pageRequest.getKeyword())) {
            wrapper.like(QcRecord::getCode, pageRequest.getKeyword());
        }
        wrapper.orderByDesc(QcRecord::getCreateTime);

        return qcRecordRepository.selectPage(page, wrapper);
    }

    @Override
    public QcRecord getById(Long id) {
        return qcRecordRepository.selectById(id);
    }

    @Override
    public void create(QcRecord record) {
        if (!StringUtils.hasText(record.getCode())) {
            record.setCode(generateCode());
        }
        if (record.getInspectTime() == null) {
            record.setInspectTime(LocalDateTime.now());
        }
        qcRecordRepository.insert(record);
    }

    @Override
    public void update(Long id, QcRecord record) {
        record.setId(id);
        qcRecordRepository.updateById(record);
    }

    @Override
    public void delete(Long id) {
        qcRecordRepository.deleteById(id);
    }

    private String generateCode() {
        String prefix = "QC-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = qcRecordRepository.selectCount(null) + 1;
        return prefix + "-" + String.format("%04d", count);
    }
}
