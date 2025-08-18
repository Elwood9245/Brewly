package xyz.elwoodwjz.brewlybackend.dto.bean;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import xyz.elwoodwjz.brewlybackend.entity.BlendType;
import xyz.elwoodwjz.brewlybackend.entity.RoastLevel;

@Data
public class BeanRequest {
    @NotNull(message = "User ID is required")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "User ID must be a valid UUID")
    private String userId;
    
    @NotBlank(message = "Bean name is required")
    @Size(max = 100, message = "Bean name must not exceed 100 characters")
    private String name;
    
    @Size(max = 100, message = "Origin must not exceed 100 characters")
    private String origin;
    
    @NotNull(message = "Blend type is required")
    private BlendType blend;
    
    @Size(max = 100, message = "Roaster name must not exceed 100 characters")
    private String roaster;
    
    @NotNull(message = "Roast type is required")
    private RoastLevel roastType;
    
    private Instant roastedAt;
    
    @Min(value = 0, message = "Rest days must be non-negative")
    @Max(value = 365, message = "Rest days cannot exceed 365")
    private Integer restDays;
    
    @Size(max = 200, message = "Flavour profile must not exceed 200 characters")
    private String flavour;
    
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1g")
    @DecimalMax(value = "10000", message = "Weight cannot exceed 10000g")
    private BigDecimal weight;
    
    @DecimalMin(value = "0", message = "Consumption must be non-negative")
    private BigDecimal consumption;
    
    private String notes;
    
    private Boolean isActive;
}
