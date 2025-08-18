package xyz.elwoodwjz.brewlybackend.dto.bean;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import xyz.elwoodwjz.brewlybackend.entity.BlendType;
import xyz.elwoodwjz.brewlybackend.entity.RoastLevel;

@Data
public class BeanResponse {
    private UUID id;
    private UUID userId;
    private String name;
    private String origin;
    private BlendType blend;
    private String roaster;
    private RoastLevel roastType;
    private Instant roastedAt;
    private Integer restDays;
    private String flavour;
    private BigDecimal weight;
    private BigDecimal consumption;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isActive;
}