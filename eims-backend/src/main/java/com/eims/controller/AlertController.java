package com.eims.controller;

import com.eims.dto.ApiResponse;
import com.eims.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(alertService.getAlertStats());
    }

    @PostMapping("/check")
    public ApiResponse<Void> checkAndSend() {
        alertService.checkAndSendStockAlerts();
        return ApiResponse.success("预警检测完成", null);
    }

    @PostMapping("/send")
    public ApiResponse<Void> sendAlert(@RequestBody Map<String, String> params) {
        String type = params.get("type");
        String title = params.get("title");
        String content = params.get("content");
        alertService.sendFeishuAlert(type, title, content);
        return ApiResponse.success("发送成功", null);
    }
}
