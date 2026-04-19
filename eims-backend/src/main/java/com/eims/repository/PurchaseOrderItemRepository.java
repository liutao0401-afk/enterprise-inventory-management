package com.eims.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.entity.PurchaseOrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseOrderItemRepository extends BaseMapper<PurchaseOrderItem> {
}
