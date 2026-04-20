package com.eims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eims.entity.Material;
import com.eims.entity.NotificationConfig;
import com.eims.repository.MaterialRepository;
import com.eims.repository.NotificationConfigRepository;
import com.eims.service.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AlertServiceImpl implements AlertService {

    private final MaterialRepository materialRepository;
    private final NotificationConfigRepository notificationConfigRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public AlertServiceImpl(MaterialRepository materialRepository,
                          NotificationConfigRepository notificationConfigRepository) {
        this.materialRepository = materialRepository;
        this.notificationConfigRepository = notificationConfigRepository;
    }

    @Override
    public void checkAndSendStockAlerts() {
        // 检查飞书通知是否启用
        NotificationConfig config = notificationConfigRepository.selectOne(
            new LambdaQueryWrapper<NotificationConfig>()
                .eq(NotificationConfig::getType, "FEISHU")
                .eq(NotificationConfig::getEnabled, 1)
        );

        if (config == null || !StringUtils.hasText(config.getWebhookUrl())) {
            log.debug("飞书通知未配置或未启用");
            return;
        }

        // 查询所有物资
        List<Material> materials = materialRepository.selectList(
            new LambdaQueryWrapper<Material>()
                .eq(Material::getStatus, "ACTIVE")
        );

        for (Material material : materials) {
            checkMaterialAlert(material, config);
        }
    }

    private void checkMaterialAlert(Material material, NotificationConfig config) {
        BigDecimal currentStock = material.getCurrentStock() != null ? material.getCurrentStock() : BigDecimal.ZERO;
        BigDecimal minStock = material.getMinStock() != null ? material.getMinStock() : BigDecimal.ZERO;
        BigDecimal safetyStock = material.getSafetyStock() != null ? material.getSafetyStock() : BigDecimal.ZERO;

        String alertType;
        String alertLevel;
        String content;

        if (currentStock.compareTo(BigDecimal.ZERO) == 0) {
            alertType = "CRITICAL";
            alertLevel = "严重";
            content = String.format("【断货预警】物资 %s(%s) 库存为0，请立即处理！",
                material.getName(), material.getCode());
        } else if (currentStock.compareTo(minStock) <= 0) {
            alertType = "WARNING";
            alertLevel = "警告";
            content = String.format("【紧急补货】物资 %s(%s) 库存(%.2f)低于最低库存(%.2f)",
                material.getName(), material.getCode(), currentStock, minStock);
        } else if (currentStock.compareTo(safetyStock) <= 0) {
            alertType = "INFO";
            alertLevel = "提醒";
            content = String.format("【安全库存预警】物资 %s(%s) 库存(%.2f)低于安全库存(%.2f)",
                material.getName(), material.getCode(), currentStock, safetyStock);
        } else {
            return; // 库存正常，不预警
        }

        sendFeishuAlert(alertType, "库存" + alertLevel, content);
    }

    @Override
    public void sendFeishuAlert(String type, String title, String content) {
        NotificationConfig config = notificationConfigRepository.selectOne(
            new LambdaQueryWrapper<NotificationConfig>()
                .eq(NotificationConfig::getType, "FEISHU")
                .eq(NotificationConfig::getEnabled, 1)
        );

        if (config == null || !StringUtils.hasText(config.getWebhookUrl())) {
            log.warn("飞书通知未配置");
            return;
        }

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("msg_type", "interactive");
            body.put("card", buildFeishuCard(type, title, content));

            restTemplate.postForObject(config.getWebhookUrl(), body, String.class);
            log.info("飞书预警发送成功: {}", title);
        } catch (Exception e) {
            log.error("飞书预警发送失败: {}", e.getMessage());
        }
    }

    private Map<String, Object> buildFeishuCard(String type, String title, String content) {
        String color = switch (type) {
            case "CRITICAL" -> "red";
            case "WARNING" -> "orange";
            default -> "blue";
        };

        return Map.of(
            "config", Map.of("wide_screen_mode", true),
            "elements", List.of(
                Map.of(
                    "tag", "markdown",
                    "content", String.format("**%s**\n%s", title, content)
                ),
                Map.of(
                    "tag", "note",
                    "elements", List.of(
                        Map.of("tag", "plain_text", "content",
                            "EIMS 物资管理系统 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    )
                )
            )
        );
    }

    @Override
    public Map<String, Object> getAlertStats() {
        List<Material> materials = materialRepository.selectList(
            new LambdaQueryWrapper<Material>()
                .eq(Material::getStatus, "ACTIVE")
        );

        int critical = 0, warning = 0, info = 0, normal = 0;

        for (Material m : materials) {
            BigDecimal current = m.getCurrentStock() != null ? m.getCurrentStock() : BigDecimal.ZERO;
            BigDecimal min = m.getMinStock() != null ? m.getMinStock() : BigDecimal.ZERO;
            BigDecimal safety = m.getSafetyStock() != null ? m.getSafetyStock() : BigDecimal.ZERO;

            if (current.compareTo(BigDecimal.ZERO) == 0) {
                critical++;
            } else if (current.compareTo(min) <= 0) {
                warning++;
            } else if (current.compareTo(safety) <= 0) {
                info++;
            } else {
                normal++;
            }
        }

        return Map.of(
            "critical", critical,
            "warning", warning,
            "info", info,
            "normal", normal,
            "total", materials.size()
        );
    }
}
