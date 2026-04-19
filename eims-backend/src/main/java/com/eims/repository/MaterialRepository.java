package com.eims.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.entity.Material;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MaterialRepository extends BaseMapper<Material> {
}
