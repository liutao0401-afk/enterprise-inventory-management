package com.eims.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eims.warehouse.domain.Warehouse;
import java.util.List;

public interface IWarehouseService {
    IPage<Warehouse> queryPage(Warehouse query, Integer pageNum, Integer pageSize);
    List<Warehouse> getAll();
    Warehouse getById(Long id);
    void save(Warehouse warehouse);
    void updateById(Warehouse warehouse);
    void removeByIds(List<Long> ids);
}
