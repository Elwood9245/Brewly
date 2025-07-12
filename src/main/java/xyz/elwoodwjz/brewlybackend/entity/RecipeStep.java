package xyz.elwoodwjz.brewlybackend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStep {
    private String instruction;
    private Integer time; // seconds
    private Double waterAmount; // grams
} 