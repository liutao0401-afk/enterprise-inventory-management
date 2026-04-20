package com.eims.system.controller;

import com.eims.common.core.domain.Result;
import com.eims.system.domain.vo.LoginVO;
import com.eims.system.service.ISysAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SysAuthController {

    private final ISysAuthService authService;

    @PostMapping("/api/auth/login")
    public Result<Map<String, Object>> login(@RequestBody LoginVO loginVO) {
        Map<String, Object> result = authService.login(loginVO);
        return Result.ok(result);
    }

    @PostMapping("/api/auth/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    @GetMapping("/api/auth/info")
    public Result<Map<String, Object>> getUserInfo() {
        return Result.ok(authService.getUserInfo());
    }
}
