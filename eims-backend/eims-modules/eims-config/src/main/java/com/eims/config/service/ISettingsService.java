package com.eims.config.service;

import com.eims.config.domain.AiConfig;
import com.eims.config.domain.NotificationConfig;

public interface ISettingsService {
    AiConfig getAiConfig();
    void saveAiConfig(AiConfig config);
    NotificationConfig getFeishuConfig();
    void saveFeishuConfig(NotificationConfig config);
    void testFeishu(String webhookUrl);
}
