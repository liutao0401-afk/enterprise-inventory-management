package com.eims.controller;

import com.eims.dto.ApiResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/feishu")
public class FeishuController {

    // 飞书Webhook地址（从配置获取或直接配置）
    private static String WEBHOOK_URL = "https://open.feishu.cn/open-apis/bot/v2/hook/your-webhook-id";

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/config")
    public ApiResponse<Void> updateConfig(@RequestBody FeishuConfig config) {
        if (config.getWebhookUrl() != null && !config.getWebhookUrl().isEmpty()) {
            WEBHOOK_URL = config.getWebhookUrl();
        }
        return ApiResponse.success("飞书配置已更新", null);
    }

    @GetMapping("/config")
    public ApiResponse<FeishuConfig> getConfig() {
        FeishuConfig config = new FeishuConfig();
        config.setWebhookUrl(WEBHOOK_URL);
        config.setEnabled(WEBHOOK_URL != null && !WEBHOOK_URL.contains("your-webhook"));
        return ApiResponse.success(config);
    }

    @PostMapping("/test")
    public ApiResponse<Map<String, Object>> test() {
        Map<String, Object> result = new HashMap<>();

        if (WEBHOOK_URL.contains("your-webhook")) {
            result.put("success", false);
            result.put("message", "请先配置飞书Webhook地址");
            return ApiResponse.success(result);
        }

        try {
            FeishuMessage message = new FeishuMessage();
            message.setMsgType("text");
            message.setContent(new FeishuMessage.TextContent("这是一条测试消息\n时间: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

            restTemplate.postForEntity(WEBHOOK_URL, message, String.class);

            result.put("success", true);
            result.put("message", "测试消息发送成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发送失败: " + e.getMessage());
        }

        return ApiResponse.success(result);
    }

    /**
     * 发送采购需求通知
     */
    public void sendRequirementNotify(String title, String content, String atUser) {
        try {
            FeishuMessage message = new FeishuMessage();
            message.setMsgType("interactive");
            Map<String, Object> card = buildNotifyCard(title, content, atUser);
            message.setCard(card);

            restTemplate.postForEntity(WEBHOOK_URL, message, String.class);
        } catch (Exception e) {
            // 记录日志，不影响主流程
        }
    }

    /**
     * 发送库存预警通知
     */
    public void sendStockAlert(String materialName, String currentStock, String safetyStock, String level) {
        try {
            String emoji = "INFO".equals(level) ? "ℹ️" : "⚠️".equals(level) ? "⚠️" : "🔴";
            String title = emoji + " 库存" + ("CRITICAL".equals(level) ? "紧急预警" : "预警提醒");
            String content = String.format("**物资名称**: %s\n**当前库存**: %s\n**安全库存**: %s\n**预警级别**: %s",
                    materialName, currentStock, safetyStock, level);

            sendRequirementNotify(title, content, null);
        } catch (Exception e) {
            // 记录日志，不影响主流程
        }
    }

    private Map<String, Object> buildNotifyCard(String title, String content, String atUser) {
        Map<String, Object> card = new HashMap<>();
        card.put("header", Map.of(
                "title", Map.of("tag", "plain_text", "content", title),
                "template", "red"
        ));

        StringElements elements = new StringElements();
        elements.setTag("markdown");
        elements.setContent(content + "\n");

        if (atUser != null && !atUser.isEmpty()) {
            elements.setContent(elements.getContent() + "<at id=\"" + atUser + "\"></at>\n");
        }

        card.put("elements", new Object[]{elements, Map.of("tag", "hr"), Map.of(
                "tag", "note",
                "elements", new Object[]{
                        Map.of("tag", "plain_text", "content", "EIMS 企业物资管理系统")
                }
        )});

        return card;
    }

    @Data
    public static class FeishuConfig {
        private String webhookUrl;
        private boolean enabled;
    }

    @Data
    public static class FeishuMessage {
        private String msgType;
        private Object content;
        private Map<String, Object> card;
    }

    @Data
    public static class StringElements {
        private String tag = "markdown";
        private String content;
    }
}
