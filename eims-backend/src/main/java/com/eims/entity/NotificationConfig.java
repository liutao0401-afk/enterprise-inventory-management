package com.eims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notification_config")
public class NotificationConfig {
    private Long id;
    private String type;
    private Integer enabled;
    private String webhookUrl;
    private String config;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
