package com.eims.config.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.eims.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notification_config")
public class NotificationConfig extends BaseEntity {

    private String type;
    private Integer enabled;
    private String webhookUrl;
    private String config;
}
