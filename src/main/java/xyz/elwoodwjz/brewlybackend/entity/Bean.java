package xyz.elwoodwjz.brewlybackend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String origin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlendType blend;

    @Column(length = 100)
    private String roaster;

    @Enumerated(EnumType.STRING)
    @Column(name = "roast_type", nullable = false)
    private RoastLevel roastType;

    @Column(length = 200)
    private String flavour;

    @Column(nullable = false, precision = 8, scale = 2)
    private Double weight = 0.0;

    @Column(precision = 8, scale = 2)
    private Double consumption = 0.0;

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

}
