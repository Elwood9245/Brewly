package xyz.elwoodwjz.brewlybackend.dto.recipe;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import xyz.elwoodwjz.brewlybackend.entity.RecipeVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;
    
    @Size(max = 50, message = "Method must not exceed 50 characters")
    private String method;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Steps are required")
    @Size(min = 1, message = "At least one step is required")
    private List<RecipeStep> steps;
    
    @NotNull(message = "Visibility is required")
    private RecipeVisibility visibility;
}
