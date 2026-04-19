package com.eims.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.entity.InventoryLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryLogRepository extends BaseMapper<InventoryLog> {
}
