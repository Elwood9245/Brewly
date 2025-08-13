package xyz.elwoodwjz.brewlybackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "brew_logs")
public class BrewLog {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "bean_id")
    private UUID beanId;

    @Column(name = "bean_name", length = 100, nullable = false)
    private String beanName;

    @Column(length = 50)
    private String method;

    @Column(name = "grind_size", length = 50)
    private String grindSize;

    @Column(name = "bean_weight_gram", precision = 8, scale = 2)
    private BigDecimal beanWeightGram;

    @Column(name = "water_weight_gram", precision = 8, scale = 2)
    private BigDecimal waterWeightGram;

    @Column(name = "water_temperature", precision = 5, scale = 2)
    private BigDecimal waterTemperature;

    @Column(name = "brew_time_seconds")
    private Integer brewTimeSeconds;

    @Column(name = "taste_notes", columnDefinition = "text")
    private String tasteNotes;

    @Column(name = "flavor_tags", columnDefinition = "text[]")
    private String[] flavorTags;

    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "recipe_id")
    private UUID recipeId;

    @Column(name = "imported_recipe_title", length = 100)
    private String importedRecipeTitle;

    @Column(name = "imported_recipe_method", length = 50)
    private String importedRecipeMethod;

    @Column(name = "imported_recipe_steps", columnDefinition = "jsonb")
    private String importedRecipeSteps;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
} 