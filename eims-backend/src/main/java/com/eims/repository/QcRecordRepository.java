package com.eims.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.entity.QcRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QcRecordRepository extends BaseMapper<QcRecord> {
}
