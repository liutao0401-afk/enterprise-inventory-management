package com.eims.service;

import java.util.Map;

public interface AlertService {
    void checkAndSendStockAlerts();
    void sendFeishuAlert(String type, String title, String content);
    Map<String, Object> getAlertStats();
}
