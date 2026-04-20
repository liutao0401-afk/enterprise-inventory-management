package com.eims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_config")
public class AiConfig {
    private Long id;
    private String provider;
    private String apiKey;
    private String apiEndpoint;
    private String model;
    private Integer enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
