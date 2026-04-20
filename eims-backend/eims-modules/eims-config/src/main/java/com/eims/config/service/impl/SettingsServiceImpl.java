package com.eims.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eims.config.domain.AiConfig;
import com.eims.config.domain.NotificationConfig;
import com.eims.config.mapper.AiConfigMapper;
import com.eims.config.mapper.NotificationConfigMapper;
import com.eims.config.service.ISettingsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsServiceImpl implements ISettingsService {

    private final AiConfigMapper aiConfigMapper;
    private final NotificationConfigMapper notificationConfigMapper;

    public SettingsServiceImpl(AiConfigMapper aiConfigMapper, NotificationConfigMapper notificationConfigMapper) {
        this.aiConfigMapper = aiConfigMapper;
        this.notificationConfigMapper = notificationConfigMapper;
    }

    @Override
    public AiConfig getAiConfig() {
        AiConfig config = aiConfigMapper.selectOne(new LambdaQueryWrapper<>());
        if (config == null) {
            config = new AiConfig();
            config.setProvider("MINIMAX");
            config.setEnabled(0);
        }
        return config;
    }

    @Override
    public void saveAiConfig(AiConfig config) {
        AiConfig existing = aiConfigMapper.selectOne(new LambdaQueryWrapper<>());
        if (existing != null) {
            config.setId(existing.getId());
            aiConfigMapper.updateById(config);
        } else {
            aiConfigMapper.insert(config);
        }
    }

    @Override
    public NotificationConfig getFeishuConfig() {
        LambdaQueryWrapper<NotificationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationConfig::getType, "FEISHU");
        NotificationConfig config = notificationConfigMapper.selectOne(wrapper);
        if (config == null) {
            config = new NotificationConfig();
            config.setType("FEISHU");
            config.setEnabled(0);
        }
        return config;
    }

    @Override
    public void saveFeishuConfig(NotificationConfig config) {
        LambdaQueryWrapper<NotificationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationConfig::getType, "FEISHU");
        NotificationConfig existing = notificationConfigMapper.selectOne(wrapper);
        if (existing != null) {
            config.setId(existing.getId());
            notificationConfigMapper.updateById(config);
        } else {
            notificationConfigMapper.insert(config);
        }
    }

    @Override
    public void testFeishu(String webhookUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> body = new HashMap<>();
            body.put("msg_type", "text");
            body.put("content", Map.of("text", "EIMS 系统飞书连接测试成功！"));
            restTemplate.postForObject(webhookUrl, body, String.class);
        } catch (Exception e) {
            throw new RuntimeException("飞书推送失败: " + e.getMessage());
        }
    }
}
