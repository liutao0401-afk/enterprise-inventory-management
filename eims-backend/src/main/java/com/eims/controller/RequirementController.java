package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.ApiResponse;
import com.eims.dto.PageRequest;
import com.eims.entity.Material;
import com.eims.entity.Requirement;
import com.eims.repository.MaterialRepository;
import com.eims.repository.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/requirement")
public class RequirementController {

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @GetMapping("/list")
    public ApiResponse<Page<Requirement>> list(PageRequest pageRequest) {
        Page<Requirement> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();

        if (pageRequest.getKeyword() != null && !pageRequest.getKeyword().isEmpty()) {
            wrapper.like(Requirement::getCode, pageRequest.getKeyword());
        }

        wrapper.orderByDesc(Requirement::getCreateTime);
        Page<Requirement> result = requirementRepository.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Requirement> getById(@PathVariable Long id) {
        Requirement requirement = requirementRepository.selectById(id);
        if (requirement == null) {
            return ApiResponse.error("需求不存在");
        }
        return ApiResponse.success(requirement);
    }

    @PostMapping
    public ApiResponse<Requirement> create(@RequestBody Requirement requirement,
                                           @RequestAttribute("userId") Long userId) {
        // 生成单号
        String code = generateCode();
        requirement.setCode(code);
        requirement.setRequesterId(userId);
        requirement.setStatus("PENDING");

        // 计算总金额
        if (requirement.getUnitPrice() != null && requirement.getQuantity() != null) {
            requirement.setTotalAmount(requirement.getUnitPrice().multiply(requirement.getQuantity()));
        }

        requirementRepository.insert(requirement);
        return ApiResponse.success("创建成功", requirement);
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id,
                                      @RequestParam(required = false) String comment,
                                      @RequestAttribute("userId") Long userId) {
        Requirement requirement = requirementRepository.selectById(id);
        if (requirement == null) {
            return ApiResponse.error("需求不存在");
        }

        requirement.setStatus("APPROVED");
        requirement.setApprovalId(userId);
        requirement.setApprovalTime(LocalDateTime.now());
        requirement.setApprovalComment(comment);

        requirementRepository.updateById(requirement);
        return ApiResponse.success("审批通过", null);
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id,
                                    @RequestParam String comment,
                                    @RequestAttribute("userId") Long userId) {
        Requirement requirement = requirementRepository.selectById(id);
        if (requirement == null) {
            return ApiResponse.error("需求不存在");
        }

        requirement.setStatus("REJECTED");
        requirement.setApprovalId(userId);
        requirement.setApprovalTime(LocalDateTime.now());
        requirement.setApprovalComment(comment);

        requirementRepository.updateById(requirement);
        return ApiResponse.success("已驳回", null);
    }

    @GetMapping("/pending")
    public ApiResponse<List<Requirement>> pending() {
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Requirement::getStatus, "PENDING");
        wrapper.orderByAsc(Requirement::getCreateTime);
        List<Requirement> list = requirementRepository.selectList(wrapper);
        return ApiResponse.success(list);
    }

    @PostMapping("/check-duplicate")
    public ApiResponse<List<Requirement>> checkDuplicate(@RequestBody Requirement requirement) {
        // 查询30天内的相似需求
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)");
        wrapper.in(Requirement::getStatus, "PENDING", "APPROVED", "PURCHASING");

        if (requirement.getMaterialId() != null) {
            wrapper.eq(Requirement::getMaterialId, requirement.getMaterialId());
        }

        List<Requirement> list = requirementRepository.selectList(wrapper);
        return ApiResponse.success(list);
    }

    private String generateCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 查询今天最大的序号
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper();
        wrapper.likeRight(Requirement::getCode, "PR-" + date);
        wrapper.orderByDesc(Requirement::getCode);
        wrapper.last("LIMIT 1");

        Requirement last = requirementRepository.selectOne(wrapper);
        int seq = 1;
        if (last != null) {
            String lastCode = last.getCode();
            String lastSeq = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            seq = Integer.parseInt(lastSeq) + 1;
        }

        return String.format("PR-%s-%03d", date, seq);
    }
}
