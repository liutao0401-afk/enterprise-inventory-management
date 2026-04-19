package com.eims.modules.material.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.modules.material.domain.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资 Mapper
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {

    /**
     * 分页查询物资
     */
    IPage<Material> selectPageByQuery(Page<?> page, @Param("query") MaterialQuery query);

    /**
     * 查询库存不足的物资
     */
    List<Material> selectLowStockMaterials(@Param("tenantId") Long tenantId);
}
