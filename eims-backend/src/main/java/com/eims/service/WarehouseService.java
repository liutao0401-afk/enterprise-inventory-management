package com.eims.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.Warehouse;

import java.util.List;

public interface WarehouseService {
    Page<Warehouse> listPage(PageRequest pageRequest);
    List<Warehouse> getAll();
    Warehouse getById(Long id);
    void create(Warehouse warehouse);
    void update(Long id, Warehouse warehouse);
    void delete(Long id);
}
