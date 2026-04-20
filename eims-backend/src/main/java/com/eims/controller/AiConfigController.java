package com.eims.controller;

import com.eims.config.AiConfig;
import com.eims.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiConfigController {

    @Autowired
    private AiConfig aiConfig;

    @GetMapping("/config")
    public ApiResponse<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", aiConfig.isEnabled());
        config.put("provider", aiConfig.getProvider());
        config.put("model", aiConfig.getModel());
        config.put("apiEndpoint", aiConfig.getApiEndpoint());
        // 不返回API密钥
        config.put("hasApiKey", aiConfig.getApiKey() != null && !aiConfig.getApiKey().isEmpty());
        return ApiResponse.success(config);
    }

    @PostMapping("/config")
    public ApiResponse<Void> updateConfig(@RequestBody AiConfig config) {
        if (config.getApiKey() != null) {
            aiConfig.setApiKey(config.getApiKey());
        }
        if (config.getProvider() != null) {
            aiConfig.setProvider(config.getProvider());
        }
        if (config.getModel() != null) {
            aiConfig.setModel(config.getModel());
        }
        if (config.getApiEndpoint() != null) {
            aiConfig.setApiEndpoint(config.getApiEndpoint());
        }
        aiConfig.setEnabled(config.isEnabled());

        return ApiResponse.success("AI配置已更新", null);
    }

    @GetMapping("/test")
    public ApiResponse<Map<String, Object>> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("enabled", aiConfig.isEnabled());
        result.put("provider", aiConfig.getProvider());

        if (!aiConfig.isEnabled()) {
            result.put("status", "disabled");
            result.put("message", "AI功能已禁用");
            return ApiResponse.success(result);
        }

        if (aiConfig.getApiKey() == null || aiConfig.getApiKey().isEmpty()) {
            result.put("status", "error");
            result.put("message", "API密钥未配置");
            return ApiResponse.success(result);
        }

        result.put("status", "ready");
        result.put("message", "AI功能已就绪");
        return ApiResponse.success(result);
    }
}
