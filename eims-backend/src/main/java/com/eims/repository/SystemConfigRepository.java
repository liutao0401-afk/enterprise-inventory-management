package com.eims.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemConfigRepository extends BaseMapper<SystemConfig> {
}
