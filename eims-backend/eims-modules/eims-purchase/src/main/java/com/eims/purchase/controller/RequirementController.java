package com.eims.purchase.controller;

import com.eims.common.core.domain.Result;
import com.eims.purchase.domain.Requirement;
import com.eims.purchase.domain.vo.DuplicateCheckVO;
import com.eims.purchase.service.IRequirementService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requirement")
@RequiredArgsConstructor
public class RequirementController {

    private final IRequirementService requirementService;

    @GetMapping
    public Result<IPage<Requirement>> list(Requirement query,
                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(requirementService.queryPage(query, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Requirement> getInfo(@PathVariable Long id) {
        return Result.ok(requirementService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Requirement requirement) {
        requirementService.saveRequirement(requirement);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Requirement requirement) {
        requirementService.updateById(requirement);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        requirementService.removeByIds(ids);
        return Result.ok();
    }

    @PostMapping("/check-duplicate")
    public Result<Map<String, Object>> checkDuplicate(@RequestBody DuplicateCheckVO checkVO) {
        return Result.ok(requirementService.checkDuplicate(checkVO));
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        requirementService.approve(id);
        return Result.ok();
    }

    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody Map<String, String> params) {
        requirementService.reject(id, params.get("comment"));
        return Result.ok();
    }
}
