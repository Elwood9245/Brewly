package xyz.elwoodwjz.brewlybackend.dto.recipe;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import xyz.elwoodwjz.brewlybackend.entity.RecipeVisibility;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeResponse {
    private UUID id;
    private UUID userId;
    private String title;
    private String method;
    private String description;
    private List<RecipeStep> steps;
    private RecipeVisibility visibility;
    private Instant createdAt;
    private Instant updatedAt;
    private Long likeCount;
    private Long commentCount;
    private Boolean isLikedByCurrentUser;
}
