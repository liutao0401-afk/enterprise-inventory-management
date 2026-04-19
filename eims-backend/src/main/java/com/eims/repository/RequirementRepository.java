package com.eims.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.entity.Requirement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RequirementRepository extends BaseMapper<Requirement> {
}
