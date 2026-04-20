package com.eims.config.controller;

import com.eims.common.core.domain.Result;
import com.eims.config.domain.NotificationConfig;
import com.eims.config.domain.AiConfig;
import com.eims.config.domain.SystemConfig;
import com.eims.config.service.ISettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SettingsController {

    private final ISettingsService settingsService;

    @GetMapping("/api/setting/ai-config")
    public Result<AiConfig> getAiConfig() {
        return Result.ok(settingsService.getAiConfig());
    }

    @PostMapping("/api/setting/ai-config")
    public Result<Void> saveAiConfig(@RequestBody AiConfig config) {
        settingsService.saveAiConfig(config);
        return Result.ok();
    }

    @GetMapping("/api/setting/feishu-config")
    public Result<NotificationConfig> getFeishuConfig() {
        return Result.ok(settingsService.getFeishuConfig());
    }

    @PostMapping("/api/setting/feishu-config")
    public Result<Void> saveFeishuConfig(@RequestBody NotificationConfig config) {
        settingsService.saveFeishuConfig(config);
        return Result.ok();
    }

    @PostMapping("/api/feishu/test")
    public Result<Void> testFeishu(@RequestBody Map<String, String> params) {
        settingsService.testFeishu(params.get("webhookUrl"));
        return Result.ok();
    }
}
