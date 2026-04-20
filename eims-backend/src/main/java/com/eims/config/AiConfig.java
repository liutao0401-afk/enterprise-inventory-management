package com.eims.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfig {
    private boolean enabled = false;
    private String provider = "MINIMAX";
    private String apiKey;
    private String apiEndpoint = "https://api.minimax.chat/v1";
    private String model = "MiniMax-01";
}
