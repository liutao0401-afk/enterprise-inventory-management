package com.eims.system.controller;

import com.eims.common.core.domain.Result;
import com.eims.system.domain.SysUser;
import com.eims.system.service.ISysUserService;
import com.eims.system.domain.vo.LoginVO;
import com.eims.system.service.ISysAuthService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService userService;
    private final ISysAuthService authService;

    @GetMapping
    public Result<IPage<SysUser>> list(SysUser query,
                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(userService.queryPage(query, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<SysUser> getInfo(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        userService.save(user);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysUser user) {
        userService.updateById(user);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        userService.removeByIds(ids);
        return Result.ok();
    }

    @PutMapping("/resetPwd")
    public Result<Void> resetPwd(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("id").toString());
        String password = params.get("password").toString();
        userService.resetPwd(userId, password);
        return Result.ok();
    }

    @GetMapping("/roles")
    public Result<List<Map<String, Object>>> getRoles() {
        return Result.ok(userService.getAllRoles());
    }
}
