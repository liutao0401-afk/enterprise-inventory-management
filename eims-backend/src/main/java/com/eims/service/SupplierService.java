package com.eims.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.Supplier;

import java.util.List;

public interface SupplierService {
    Page<Supplier> listPage(PageRequest pageRequest);
    List<Supplier> getAll();
    Supplier getById(Long id);
    void create(Supplier supplier);
    void update(Long id, Supplier supplier);
    void delete(Long id);
}
