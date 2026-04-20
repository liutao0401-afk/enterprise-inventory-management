package com.eims.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.Requirement;

import java.util.List;

public interface RequirementService {
    Page<Requirement> listPage(PageRequest pageRequest);
    List<Requirement> getAll();
    Requirement getById(Long id);
    void create(Requirement requirement);
    void update(Long id, Requirement requirement);
    void delete(Long id);
    void approve(Long id);
    void reject(Long id, String comment);
}
