package xyz.elwoodwjz.brewlybackend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStep {
    private String instruction;
    private Integer time; // seconds
    private BigDecimal waterAmount; // grams
} 