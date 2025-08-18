package xyz.elwoodwjz.brewlybackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "beans")
public class Bean {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "Bean name is required")
    @Size(max = 100, message = "Bean name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 100, message = "Origin must not exceed 100 characters")
    @Column(length = 100)
    private String origin;

    @NotNull(message = "Blend type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlendType blend;

    @Size(max = 100, message = "Roaster name must not exceed 100 characters")
    @Column(length = 100)
    private String roaster;

    @NotNull(message = "Roast type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "roast_type", nullable = false)
    private RoastLevel roastType;

    @Column(name = "roasted_at")
    private Instant roastedAt;

    @Min(value = 0, message = "Rest days must be non-negative")
    @Max(value = 365, message = "Rest days cannot exceed 365")
    @Column(name = "rest_days")
    private Integer restDays;

    @Size(max = 200, message = "Flavour profile must not exceed 200 characters")
    @Column(length = 200)
    private String flavour;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1g")
    @DecimalMax(value = "10000", message = "Weight cannot exceed 10000g")
    @Builder.Default
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal weight = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "Consumption must be non-negative")
    @Builder.Default
    @Column(precision = 8, scale = 2)
    private BigDecimal consumption = BigDecimal.ZERO;

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

}
