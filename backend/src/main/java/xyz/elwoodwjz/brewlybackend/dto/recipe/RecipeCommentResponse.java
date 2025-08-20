package xyz.elwoodwjz.brewlybackend.dto.recipe;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeCommentResponse {
    private UUID id;
    private UUID recipeId;
    private UUID userId;
    private String username; // Will be populated from User entity
    private String content;
    private Instant createdAt;
}
