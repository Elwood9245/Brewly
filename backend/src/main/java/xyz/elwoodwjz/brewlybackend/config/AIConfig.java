package xyz.elwoodwjz.brewlybackend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AIConfig {
    @Value("${deepseek.api.key}")
    private String apiKey;
    
    @Value("${deepseek.model}")
    private String model;
    
    @Value("${deepseek.max-tokens:1000}")
    private Integer maxTokens;
    
    @Value("${deepseek.temperature:0.5}")
    private Double temperature;
    
    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.path:/chat/completions}")
    private String apiPath;
}
