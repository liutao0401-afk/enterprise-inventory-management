package com.eims.scheduler;

import com.eims.service.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class AlertScheduler {

    @Autowired
    private AlertService alertService;

    /**
     * 每小时检测一次库存预警
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkStockAlerts() {
        log.info("开始执行库存预警检测...");
        try {
            alertService.checkAndSendStockAlerts();
            log.info("库存预警检测完成");
        } catch (Exception e) {
            log.error("库存预警检测失败", e);
        }
    }
}
