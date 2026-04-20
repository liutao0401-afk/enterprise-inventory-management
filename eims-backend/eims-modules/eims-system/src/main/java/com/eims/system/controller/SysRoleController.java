package com.eims.system.controller;

import com.eims.common.core.domain.Result;
import com.eims.system.domain.SysRole;
import com.eims.system.service.ISysRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService roleService;

    @GetMapping
    public Result<IPage<SysRole>> list(SysRole query,
                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(roleService.queryPage(query, pageNum, pageSize));
    }

    @GetMapping("/all")
    public Result<List<SysRole>> getAllRoles() {
        return Result.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public Result<SysRole> getInfo(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        roleService.save(role);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) {
        roleService.updateById(role);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        roleService.removeByIds(ids);
        return Result.ok();
    }
}
