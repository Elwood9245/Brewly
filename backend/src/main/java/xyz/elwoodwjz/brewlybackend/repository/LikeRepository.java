package xyz.elwoodwjz.brewlybackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.elwoodwjz.brewlybackend.entity.Like;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, Like.LikeId> {
    
    /**
     * Find like by user ID and recipe ID
     */
    Optional<Like> findByUserIdAndRecipeId(UUID userId, UUID recipeId);
    
    /**
     * Check if user has liked a recipe
     */
    boolean existsByUserIdAndRecipeId(UUID userId, UUID recipeId);
    
    /**
     * Count likes for a recipe
     */
    long countByRecipeId(UUID recipeId);
    
    /**
     * Delete like by user ID and recipe ID
     */
    void deleteByUserIdAndRecipeId(UUID userId, UUID recipeId);
}
