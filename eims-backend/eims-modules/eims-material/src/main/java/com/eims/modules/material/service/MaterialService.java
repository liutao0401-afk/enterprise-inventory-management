package com.eims.modules.material.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eims.modules.material.domain.Material;
import com.eims.modules.material.domain.MaterialQuery;

/**
 * 物资管理 Service
 */
public interface MaterialService {

    /**
     * 分页查询物资列表
     */
    Object page(MaterialQuery query);

    /**
     * 根据ID获取物资详情
     */
    Material getById(Long id);

    /**
     * 新增物资
     */
    Long save(Material material);

    /**
     * 更新物资
     */
    boolean updateById(Material material);

    /**
     * 删除物资
     */
    boolean deleteById(Long id);
}
