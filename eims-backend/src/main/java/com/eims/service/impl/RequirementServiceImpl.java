package com.eims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.PageRequest;
import com.eims.entity.Requirement;
import com.eims.repository.RequirementRepository;
import com.eims.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RequirementServiceImpl implements RequirementService {

    @Autowired
    private RequirementRepository requirementRepository;

    @Override
    public Page<Requirement> listPage(PageRequest pageRequest) {
        Page<Requirement> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(pageRequest.getKeyword())) {
            wrapper.and(w -> w.like(Requirement::getCode, pageRequest.getKeyword())
                    .or().like(Requirement::getPurpose, pageRequest.getKeyword()));
        }
        wrapper.orderByDesc(Requirement::getCreateTime);

        return requirementRepository.selectPage(page, wrapper);
    }

    @Override
    public List<Requirement> getAll() {
        return requirementRepository.selectList(null);
    }

    @Override
    public Requirement getById(Long id) {
        return requirementRepository.selectById(id);
    }

    @Override
    @Transactional
    public void create(Requirement requirement) {
        if (!StringUtils.hasText(requirement.getCode())) {
            requirement.setCode(generateCode());
        }
        if (!StringUtils.hasText(requirement.getStatus())) {
            requirement.setStatus("PENDING");
        }
        requirementRepository.insert(requirement);
    }

    @Override
    public void update(Long id, Requirement requirement) {
        requirement.setId(id);
        requirementRepository.updateById(requirement);
    }

    @Override
    public void delete(Long id) {
        Requirement requirement = new Requirement();
        requirement.setId(id);
        requirement.setStatus("CANCELLED");
        requirementRepository.updateById(requirement);
    }

    @Override
    @Transactional
    public void approve(Long id) {
        Requirement requirement = new Requirement();
        requirement.setId(id);
        requirement.setStatus("APPROVED");
        requirementRepository.updateById(requirement);
    }

    @Override
    @Transactional
    public void reject(Long id, String comment) {
        Requirement requirement = new Requirement();
        requirement.setId(id);
        requirement.setStatus("REJECTED");
        requirementRepository.updateById(requirement);
    }

    private String generateCode() {
        String prefix = "PR-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = requirementRepository.selectCount(null) + 1;
        return prefix + "-" + String.format("%03d", count);
    }
}
