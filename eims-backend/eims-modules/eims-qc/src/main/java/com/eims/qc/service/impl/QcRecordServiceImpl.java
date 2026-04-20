package com.eims.qc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eims.qc.domain.QcRecord;
import com.eims.qc.mapper.QcRecordMapper;
import com.eims.qc.service.IQcRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class QcRecordServiceImpl extends ServiceImpl<QcRecordMapper, QcRecord> implements IQcRecordService {

    @Override
    public IPage<QcRecord> queryPage(QcRecord query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<QcRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getCode())) {
            wrapper.like(QcRecord::getCode, query.getCode());
        }
        if (query.getMaterialId() != null) {
            wrapper.eq(QcRecord::getMaterialId, query.getMaterialId());
        }
        if (StringUtils.hasText(query.getResult())) {
            wrapper.eq(QcRecord::getResult, query.getResult());
        }
        wrapper.orderByDesc(QcRecord::getCreateTime);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void saveRecord(QcRecord record) {
        if (!StringUtils.hasText(record.getCode())) {
            record.setCode(generateCode());
        }
        if (record.getInspectTime() == null) {
            record.setInspectTime(LocalDateTime.now());
        }
        baseMapper.insert(record);
    }

    private String generateCode() {
        String prefix = "QC-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = baseMapper.selectCount(null) + 1;
        return prefix + "-" + String.format("%04d", count);
    }
}
