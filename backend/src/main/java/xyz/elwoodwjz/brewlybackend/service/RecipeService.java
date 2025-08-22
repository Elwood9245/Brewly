package xyz.elwoodwjz.brewlybackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.elwoodwjz.brewlybackend.dto.recipe.*;
import xyz.elwoodwjz.brewlybackend.entity.*;
import xyz.elwoodwjz.brewlybackend.exception.ResourceNotFoundException;
import xyz.elwoodwjz.brewlybackend.exception.UnauthorizedException;
import xyz.elwoodwjz.brewlybackend.repository.*;
import xyz.elwoodwjz.brewlybackend.entity.RecipeVisibility;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    private final RecipeCommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public RecipeResponse createRecipe(UUID userId, RecipeRequest request) {
        Recipe recipe = Recipe.builder()
            .userId(userId)
            .title(request.getTitle())
            .method(request.getMethod())
            .description(request.getDescription())
            .visibility(request.getVisibility())
            .build();
        
        recipe.setStepsFromList(request.getSteps());
        Recipe savedRecipe = recipeRepository.save(recipe);
        
        return convertToResponse(savedRecipe, userId);
    }
    
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(UUID recipeId, UUID currentUserId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        // Check if user can access the recipe
        if (recipe.getVisibility() == RecipeVisibility.PRIVATE && 
            !recipe.getUserId().equals(currentUserId)) {
            throw new UnauthorizedException("You don't have permission to view this recipe");
        }
        
        return convertToResponse(recipe, currentUserId);
    }
    
    @Transactional
    public RecipeResponse updateRecipe(UUID recipeId, UUID userId, RecipeRequest request) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        if (!recipe.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only update your own recipes");
        }
        
        recipe.setTitle(request.getTitle());
        recipe.setMethod(request.getMethod());
        recipe.setDescription(request.getDescription());
        recipe.setVisibility(request.getVisibility());
        recipe.setStepsFromList(request.getSteps());
        
        Recipe updatedRecipe = recipeRepository.save(recipe);
        return convertToResponse(updatedRecipe, userId);
    }
    
    @Transactional
    public void deleteRecipe(UUID recipeId, UUID userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        if (!recipe.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own recipes");
        }
        
        // Delete associated likes and comments first
        likeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
        commentRepository.deleteByRecipeId(recipeId);
        
        recipeRepository.delete(recipe);
    }
    
    @Transactional(readOnly = true)
    public Page<RecipeResponse> searchPublicRecipes(String keyword, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByVisibilityAndKeywordOrderByCreatedAtDesc(
            RecipeVisibility.PUBLIC, keyword, pageable);
        return recipes.map(recipe -> convertToResponse(recipe, null));
    }
    
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getPublicRecipesByMethod(String method, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByVisibilityAndMethodIgnoreCaseOrderByCreatedAtDesc(
            RecipeVisibility.PUBLIC, method, pageable);
        return recipes.map(recipe -> convertToResponse(recipe, null));
    }
    
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getPublicRecipes(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByVisibilityOrderByCreatedAtDesc(
            RecipeVisibility.PUBLIC, pageable);
        return recipes.map(recipe -> convertToResponse(recipe, null));
    }
    
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getPopularPublicRecipes(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findPublicRecipesOrderByLikesDesc(
            RecipeVisibility.PUBLIC, pageable);
        return recipes.map(recipe -> convertToResponse(recipe, null));
    }
    
    // Like functionality
    @Transactional
    public void likeRecipe(UUID recipeId, UUID userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        if (recipe.getVisibility() == RecipeVisibility.PRIVATE && 
            !recipe.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to like this recipe");
        }
        
        if (likeRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            throw new IllegalArgumentException("You have already liked this recipe");
        }
        
        Like like = Like.builder()
            .userId(userId)
            .recipeId(recipeId)
            .build();
        
        likeRepository.save(like);
    }
    
    @Transactional
    public void unlikeRecipe(UUID recipeId, UUID userId) {
        if (!likeRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            throw new IllegalArgumentException("You have not liked this recipe");
        }
        
        likeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }
    
    // Comment functionality
    @Transactional
    public RecipeCommentResponse addComment(UUID recipeId, UUID userId, RecipeCommentRequest request) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        if (recipe.getVisibility() == RecipeVisibility.PRIVATE && 
            !recipe.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to comment on this recipe");
        }
        
        RecipeComment comment = RecipeComment.builder()
            .recipeId(recipeId)
            .userId(userId)
            .content(request.getContent())
            .build();
        
        RecipeComment savedComment = commentRepository.save(comment);
        return convertCommentToResponse(savedComment);
    }
    
    @Transactional(readOnly = true)
    public Page<RecipeCommentResponse> getRecipeComments(UUID recipeId, Pageable pageable) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        if (recipe.getVisibility() == RecipeVisibility.PRIVATE) {
            throw new UnauthorizedException("You don't have permission to view comments for this recipe");
        }
        
        Page<RecipeComment> comments = commentRepository.findByRecipeIdOrderByCreatedAtAsc(recipeId, pageable);
        return comments.map(this::convertCommentToResponse);
    }
    
    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        RecipeComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        // Only the comment author or recipe owner can delete the comment
        Recipe recipe = recipeRepository.findById(comment.getRecipeId())
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        if (!comment.getUserId().equals(userId) && !recipe.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own comments or comments on your recipes");
        }
        
        commentRepository.delete(comment);
    }
    
    // Helper methods
    private RecipeResponse convertToResponse(Recipe recipe, UUID currentUserId) {
        Long likeCount = likeRepository.countByRecipeId(recipe.getId());
        Long commentCount = commentRepository.countByRecipeId(recipe.getId());
        Boolean isLikedByCurrentUser = currentUserId != null && 
            likeRepository.existsByUserIdAndRecipeId(currentUserId, recipe.getId());
        
        return RecipeResponse.builder()
            .id(recipe.getId())
            .userId(recipe.getUserId())
            .title(recipe.getTitle())
            .method(recipe.getMethod())
            .description(recipe.getDescription())
            .steps(recipe.getStepsAsList())
            .visibility(recipe.getVisibility())
            .createdAt(recipe.getCreatedAt())
            .updatedAt(recipe.getUpdatedAt())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isLikedByCurrentUser(isLikedByCurrentUser)
            .build();
    }
    
    private RecipeCommentResponse convertCommentToResponse(RecipeComment comment) {
        User user = userRepository.findById(comment.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return RecipeCommentResponse.builder()
            .id(comment.getId())
            .recipeId(comment.getRecipeId())
            .userId(comment.getUserId())
            .username(user.getUsername())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .build();
    }
    
    // Bookmark functionality
    @Transactional
    public RecipeResponse bookmarkRecipe(UUID userId, UUID recipeId) {
        Recipe originalRecipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        // Check if user can bookmark this recipe
        if (originalRecipe.getVisibility() == RecipeVisibility.PRIVATE && 
            !originalRecipe.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to bookmark this recipe");
        }
        
        // Check if user has already bookmarked this recipe
        if (recipeRepository.existsByUserIdAndBookmarkedFromId(userId, recipeId)) {
            throw new IllegalArgumentException("You have already bookmarked this recipe");
        }
        
        // Check if user is trying to bookmark their own recipe
        if (originalRecipe.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You cannot bookmark your own recipe");
        }
        
        // Create bookmarked recipe
        Recipe bookmarkedRecipe = Recipe.builder()
            .userId(userId)
            .title(originalRecipe.getTitle() + " (Bookmarked)")
            .method(originalRecipe.getMethod())
            .description(originalRecipe.getDescription())
            .visibility(RecipeVisibility.PRIVATE) // Bookmarked recipes are always private
            .bookmarkedFromId(recipeId)
            .isBookmark(true)
            .originalUserId(originalRecipe.getUserId())
            .build();
        
        bookmarkedRecipe.setStepsFromList(originalRecipe.getStepsAsList());
        Recipe savedBookmarkedRecipe = recipeRepository.save(bookmarkedRecipe);
        
        return convertToResponse(savedBookmarkedRecipe, userId);
    }
    
    @Transactional
    public void unbookmarkRecipe(UUID userId, UUID recipeId) {
        Recipe bookmarkedRecipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        if (!bookmarkedRecipe.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only unbookmark your own bookmarked recipes");
        }
        
        if (!bookmarkedRecipe.isBookmarked()) {
            throw new IllegalArgumentException("This is not a bookmarked recipe");
        }
        
        recipeRepository.delete(bookmarkedRecipe);
    }
    
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getMyRecipes(UUID userId, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByUserIdAndIsBookmarkFalseOrderByCreatedAtDesc(userId, pageable);
        return recipes.map(recipe -> convertToResponse(recipe, userId));
    }
    
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getBookmarkedRecipes(UUID userId, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByUserIdAndIsBookmarkTrueOrderByCreatedAtDesc(userId, pageable);
        return recipes.map(recipe -> convertToResponse(recipe, userId));
    }
    
    @Transactional(readOnly = true)
    public boolean isRecipeBookmarkedByUser(UUID userId, UUID recipeId) {
        return recipeRepository.existsByUserIdAndBookmarkedFromId(userId, recipeId);
    }
    
    // Get all user's available recipes (both own and bookmarked) for brew log creation
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getUserAvailableRecipes(UUID userId, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return recipes.map(recipe -> convertToResponse(recipe, userId));
    }
}
