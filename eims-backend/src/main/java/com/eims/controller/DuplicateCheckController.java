package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eims.dto.ApiResponse;
import com.eims.entity.Material;
import com.eims.entity.Requirement;
import com.eims.repository.MaterialRepository;
import com.eims.repository.RequirementRepository;
import com.eims.service.AiDuplicateCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/duplicate-check")
public class DuplicateCheckController {

    @Autowired
    private AiDuplicateCheckService duplicateCheckService;

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @PostMapping("/check")
    public ApiResponse<AiDuplicateCheckService.DuplicateCheckResult> check(
            @RequestBody Map<String, Object> request) {

        Long materialId = request.get("materialId") != null
                ? Long.valueOf(request.get("materialId").toString()) : null;

        // 获取物资信息
        Material material = null;
        String materialName = "";
        String spec = "";

        if (materialId != null) {
            material = materialRepository.selectById(materialId);
            if (material != null) {
                materialName = material.getName();
                spec = material.getSpec();
            }
        }

        // 从请求中获取物资名称（如果物资ID为空）
        if (materialName.isEmpty() && request.get("materialName") != null) {
            materialName = request.get("materialName").toString();
        }
        if (spec.isEmpty() && request.get("spec") != null) {
            spec = request.get("spec").toString();
        }

        String purpose = request.get("purpose") != null ? request.get("purpose").toString() : "";
        BigDecimal quantity = request.get("quantity") != null
                ? new BigDecimal(request.get("quantity").toString()) : BigDecimal.ZERO;

        // 查询30天内的历史需求
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)");
        wrapper.in(Requirement::getStatus, "PENDING", "APPROVED", "PURCHASING");
        wrapper.orderByDesc(Requirement::getCreateTime);
        wrapper.last("LIMIT 50");

        List<Requirement> historyRequirements = requirementRepository.selectList(wrapper);

        // 构建检测请求
        AiDuplicateCheckService.DuplicateCheckRequest checkRequest =
                new AiDuplicateCheckService.DuplicateCheckRequest();
        checkRequest.setMaterialId(materialId);
        checkRequest.setMaterialName(materialName);
        checkRequest.setSpec(spec);
        checkRequest.setPurpose(purpose);
        checkRequest.setQuantity(quantity);
        checkRequest.setHistoryRequirements(historyRequirements);

        // 根据模式选择检测方式
        String checkMode = request.get("checkMode") != null
                ? request.get("checkMode").toString() : "SYSTEM";

        AiDuplicateCheckService.DuplicateCheckResult result;
        if ("AI".equalsIgnoreCase(checkMode)) {
            result = duplicateCheckService.checkWithAI(checkRequest);
        } else {
            result = duplicateCheckService.checkWithLogic(checkRequest);
        }

        // 如果是AI检测，保存检测结果
        if ("AI".equalsIgnoreCase(checkMode) && materialId != null) {
            saveAiCheckResult(materialId, result);
        }

        return ApiResponse.success(result);
    }

    private void saveAiCheckResult(Long materialId, AiDuplicateCheckService.DuplicateCheckResult result) {
        // 可以在这里保存AI检测结果到数据库
        // 目前只是记录日志
    }
}
