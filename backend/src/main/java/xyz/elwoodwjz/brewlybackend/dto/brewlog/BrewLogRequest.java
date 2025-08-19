package xyz.elwoodwjz.brewlybackend.dto.brewlog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrewLogRequest {
    
    @NotNull(message = "Bean ID is required")
    private UUID beanId;
    
    @Size(max = 50, message = "Method must be less than 50 characters")
    private String method;
    
    @Size(max = 50, message = "Grind size must be less than 50 characters")
    private String grindSize;
    
    @DecimalMin(value = "0.1", message = "Bean weight must be at least 0.1 grams")
    @DecimalMax(value = "1000.0", message = "Bean weight must be less than 1000 grams")
    private BigDecimal beanWeightGram;
    
    @DecimalMin(value = "0.1", message = "Water weight must be at least 0.1 grams")
    @DecimalMax(value = "5000.0", message = "Water weight must be less than 5000 grams")
    private BigDecimal waterWeightGram;
    
    @DecimalMin(value = "70.0", message = "Water temperature must be at least 70°C")
    @DecimalMax(value = "100.0", message = "Water temperature must be at most 100°C")
    private BigDecimal waterTemperature;
    
    @Min(value = 1, message = "Brew time must be at least 1 second")
    @Max(value = 3600, message = "Brew time must be less than 1 hour")
    private Integer brewTimeSeconds;
    
    @Size(max = 2000, message = "Taste notes must be less than 2000 characters")
    private String tasteNotes;
    

    
    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Rating must be at most 10.0")
    private BigDecimal rating;
    
    private UUID recipeId;
    
    @Size(max = 100, message = "Imported recipe title must be less than 100 characters")
    private String importedRecipeTitle;
    
    @Size(max = 50, message = "Imported recipe method must be less than 50 characters")
    private String importedRecipeMethod;
    
    private List<String> importedRecipeSteps;
}
