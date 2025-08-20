package xyz.elwoodwjz.brewlybackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import xyz.elwoodwjz.brewlybackend.entity.RecipeComment;
import java.util.UUID;

public interface RecipeCommentRepository extends JpaRepository<RecipeComment, UUID> {
    
    /**
     * Find all comments for a recipe, ordered by creation date ascending
     */
    Page<RecipeComment> findByRecipeIdOrderByCreatedAtAsc(UUID recipeId, Pageable pageable);
    
    /**
     * Find comment by ID and user ID (for deletion validation)
     */
    boolean existsByIdAndUserId(UUID id, UUID userId);
    
    /**
     * Count comments for a recipe
     */
    long countByRecipeId(UUID recipeId);
    
    /**
     * Delete all comments for a recipe
     */
    void deleteByRecipeId(UUID recipeId);
}
