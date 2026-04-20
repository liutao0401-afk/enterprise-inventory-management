package com.eims.qc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eims.qc.domain.QcRecord;
import java.util.List;

public interface IQcRecordService {
    IPage<QcRecord> queryPage(QcRecord query, Integer pageNum, Integer pageSize);
    QcRecord getById(Long id);
    void saveRecord(QcRecord record);
    void updateById(QcRecord record);
    void removeByIds(List<Long> ids);
}
