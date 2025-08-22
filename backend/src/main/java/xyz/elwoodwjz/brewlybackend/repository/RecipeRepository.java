package xyz.elwoodwjz.brewlybackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.elwoodwjz.brewlybackend.entity.Recipe;
import xyz.elwoodwjz.brewlybackend.entity.RecipeVisibility;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    /**
     * Find recipes by user ID with pagination
     */
    Page<Recipe> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    /**
     * Find public recipes by method
     */
    Page<Recipe> findByVisibilityAndMethodIgnoreCaseOrderByCreatedAtDesc(
        RecipeVisibility visibility, String method, Pageable pageable);
    
    /**
     * Find public recipes by title or description containing keyword
     */
    @Query("SELECT r FROM Recipe r WHERE r.visibility = :visibility " +
           "AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY r.createdAt DESC")
    Page<Recipe> findByVisibilityAndKeywordOrderByCreatedAtDesc(
        @Param("visibility") RecipeVisibility visibility, 
        @Param("keyword") String keyword, 
        Pageable pageable);
    
    /**
     * Find all public recipes ordered by creation date descending
     */
    Page<Recipe> findByVisibilityOrderByCreatedAtDesc(RecipeVisibility visibility, Pageable pageable);
    
    /**
     * Find public recipes ordered by like count descending (most popular)
     */
    @Query("SELECT r FROM Recipe r WHERE r.visibility = :visibility " +
           "ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.recipeId = r.id) DESC, r.createdAt DESC")
    Page<Recipe> findPublicRecipesOrderByLikesDesc(@Param("visibility") RecipeVisibility visibility, Pageable pageable);
    
    /**
     * Find user's own recipes (non-bookmarked)
     */
    Page<Recipe> findByUserIdAndIsBookmarkFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    /**
     * Find user's bookmarked recipes
     */
    Page<Recipe> findByUserIdAndIsBookmarkTrueOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    /**
     * Check if user has already bookmarked a recipe
     */
    boolean existsByUserIdAndBookmarkedFromId(UUID userId, UUID recipeId);
}
