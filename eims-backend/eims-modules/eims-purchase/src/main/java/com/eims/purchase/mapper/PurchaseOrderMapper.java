package com.eims.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.purchase.domain.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {
}
