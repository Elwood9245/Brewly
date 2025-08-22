package xyz.elwoodwjz.brewlybackend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AIConfig {
    @Value("${anthropic.api.key}")
    private String apiKey;
    
    @Value("${anthropic.model}")
    private String model;
    
    @Value("${anthropic.max-tokens:1000}")
    private Integer maxTokens;
    
    @Value("${anthropic.temperature:0.5}")
    private Double temperature;
}
