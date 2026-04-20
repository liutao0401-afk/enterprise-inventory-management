package com.eims.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.QcRecord;

import java.util.List;

public interface QcRecordService {
    Page<QcRecord> listPage(PageRequest pageRequest);
    QcRecord getById(Long id);
    void create(QcRecord record);
    void update(Long id, QcRecord record);
    void delete(Long id);
}
