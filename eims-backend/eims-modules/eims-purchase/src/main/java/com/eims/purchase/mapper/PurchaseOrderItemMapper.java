package com.eims.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.purchase.domain.PurchaseOrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseOrderItemMapper extends BaseMapper<PurchaseOrderItem> {
}
