package com.eims.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SupplierRepository extends BaseMapper<Supplier> {
}
