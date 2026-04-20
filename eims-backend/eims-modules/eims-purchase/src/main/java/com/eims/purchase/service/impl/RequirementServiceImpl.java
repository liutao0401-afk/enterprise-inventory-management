package com.eims.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eims.purchase.domain.Requirement;
import com.eims.purchase.domain.vo.DuplicateCheckVO;
import com.eims.purchase.mapper.RequirementMapper;
import com.eims.purchase.service.IRequirementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RequirementServiceImpl extends ServiceImpl<RequirementMapper, Requirement> implements IRequirementService {

    @Override
    public IPage<Requirement> queryPage(Requirement query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getCode())) {
            wrapper.like(Requirement::getCode, query.getCode());
        }
        if (query.getMaterialId() != null) {
            wrapper.eq(Requirement::getMaterialId, query.getMaterialId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Requirement::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getDeptName())) {
            wrapper.like(Requirement::getDeptName, query.getDeptName());
        }
        wrapper.orderByDesc(Requirement::getCreateTime);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void saveRequirement(Requirement requirement) {
        if (requirement.getTotalAmount() == null && requirement.getQuantity() != null && requirement.getUnitPrice() != null) {
            requirement.setTotalAmount(requirement.getQuantity().multiply(requirement.getUnitPrice()));
        }
        if (!StringUtils.hasText(requirement.getCode())) {
            requirement.setCode(generateCode());
        }
        if (!StringUtils.hasText(requirement.getStatus())) {
            requirement.setStatus("PENDING");
        }
        baseMapper.insert(requirement);
    }

    @Override
    public Map<String, Object> checkDuplicate(DuplicateCheckVO checkVO) {
        Map<String, Object> result = new HashMap<>();
        result.put("isDuplicate", false);
        result.put("similarItems", new ArrayList<>());
        result.put("reason", "未发现重复需求");

        if (checkVO.getMaterialId() == null) {
            return result;
        }

        // 查询30天内的需求
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Requirement::getMaterialId, checkVO.getMaterialId());
        wrapper.in(Requirement::getStatus, "PENDING", "APPROVED", "PURCHASING");
        wrapper.apply("create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)");
        List<Requirement> similarRequirements = baseMapper.selectList(wrapper);

        if (!similarRequirements.isEmpty()) {
            result.put("isDuplicate", true);
            result.put("similarItems", similarRequirements.stream()
                .map(r -> Map.of("code", r.getCode(), "quantity", r.getQuantity(), "purpose", r.getPurpose() != null ? r.getPurpose() : ""))
                .toList());
            result.put("reason", "发现 " + similarRequirements.size() + " 条相似需求");
        }

        return result;
    }

    @Override
    public void approve(Long id) {
        Requirement requirement = new Requirement();
        requirement.setId(id);
        requirement.setStatus("APPROVED");
        baseMapper.updateById(requirement);
    }

    @Override
    public void reject(Long id, String comment) {
        Requirement requirement = new Requirement();
        requirement.setId(id);
        requirement.setStatus("REJECTED");
        requirement.setApprovalComment(comment);
        baseMapper.updateById(requirement);
    }

    private String generateCode() {
        String prefix = "PR-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = baseMapper.selectCount(null) + 1;
        return prefix + "-" + String.format("%03d", count);
    }
}
