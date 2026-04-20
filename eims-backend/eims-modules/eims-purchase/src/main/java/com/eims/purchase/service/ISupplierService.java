package com.eims.purchase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eims.purchase.domain.Supplier;
import java.util.List;

public interface ISupplierService {
    IPage<Supplier> queryPage(Supplier query, Integer pageNum, Integer pageSize);
    List<Supplier> getAll();
    Supplier getById(Long id);
    void save(Supplier supplier);
    void updateById(Supplier supplier);
    void removeByIds(List<Long> ids);
}
