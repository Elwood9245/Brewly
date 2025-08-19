package xyz.elwoodwjz.brewlybackend.dto.brewlog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrewLogResponse {
    
    private UUID id;
    private UUID userId;
    private UUID beanId;
    private String beanName;
    private String method;
    private String grindSize;
    private BigDecimal beanWeightGram;
    private BigDecimal waterWeightGram;
    private BigDecimal waterTemperature;
    private Integer brewTimeSeconds;
    private String tasteNotes;
    private BigDecimal rating;
    private UUID recipeId;
    private String importedRecipeTitle;
    private String importedRecipeMethod;
    private List<String> importedRecipeSteps;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Calculated fields
    private BigDecimal ratio; // water to bean ratio
    private String formattedBrewTime; // formatted as MM:SS
    private String formattedCreatedAt; // formatted date
    private String formattedUpdatedAt; // formatted date
}
