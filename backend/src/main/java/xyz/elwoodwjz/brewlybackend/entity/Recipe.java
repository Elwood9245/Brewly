package xyz.elwoodwjz.brewlybackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.elwoodwjz.brewlybackend.dto.recipe.RecipeStep;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recipes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Recipe {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 50)
    private String method;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String steps;
    
    // Helper methods for JSON serialization
    public List<RecipeStep> getStepsAsList() {
        if (steps == null || steps.isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(steps, new TypeReference<List<RecipeStep>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize steps", e);
        }
    }
    
    public void setStepsFromList(List<RecipeStep> stepsList) {
        if (stepsList == null) {
            this.steps = "[]";
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.steps = mapper.writeValueAsString(stepsList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize steps", e);
        }
    }

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeVisibility visibility = RecipeVisibility.PRIVATE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "bookmarked_from_id")
    private UUID bookmarkedFromId;
    
    @Builder.Default
    @Column(name = "is_bookmark")
    private Boolean isBookmark = false;
    
    @Column(name = "original_user_id")
    private UUID originalUserId;
    
    // Helper methods for bookmark functionality
    public boolean isBookmarked() {
        return Boolean.TRUE.equals(isBookmark);
    }
}