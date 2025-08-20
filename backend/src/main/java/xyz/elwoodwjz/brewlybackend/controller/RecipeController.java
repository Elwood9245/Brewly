package xyz.elwoodwjz.brewlybackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import xyz.elwoodwjz.brewlybackend.dto.recipe.*;
import xyz.elwoodwjz.brewlybackend.service.RecipeService;
import xyz.elwoodwjz.brewlybackend.security.JwtUtil;
import xyz.elwoodwjz.brewlybackend.security.CustomUserDetailsService.CustomUserPrincipal;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RecipeController {
    
    private final RecipeService recipeService;
    
    // Helper method to get user ID from authentication
    private UUID getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) authentication.getPrincipal()).getUser().getId();
        }
        return null;
    }
    
    // Recipe CRUD operations
    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(
            @Valid @RequestBody RecipeRequest request,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        RecipeResponse response = recipeService.createRecipe(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        RecipeResponse response = recipeService.getRecipeById(id, userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable UUID id,
            @Valid @RequestBody RecipeRequest request,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        RecipeResponse response = recipeService.updateRecipe(id, userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        recipeService.deleteRecipe(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    // User recipes
    @GetMapping("/user")
    public ResponseEntity<Page<RecipeResponse>> getUserRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RecipeResponse> recipes = recipeService.getUserRecipes(userId, pageable);
        return ResponseEntity.ok(recipes);
    }
    
    // Public recipes
    @GetMapping("/public")
    public ResponseEntity<Page<RecipeResponse>> getPublicRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<RecipeResponse> recipes;
        if ("popular".equals(sort)) {
            recipes = recipeService.getPopularPublicRecipes(pageable);
        } else {
            recipes = recipeService.getPublicRecipes(pageable);
        }
        
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/public/search")
    public ResponseEntity<Page<RecipeResponse>> searchPublicRecipes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RecipeResponse> recipes = recipeService.searchPublicRecipes(keyword, pageable);
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/public/method/{method}")
    public ResponseEntity<Page<RecipeResponse>> getPublicRecipesByMethod(
            @PathVariable String method,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RecipeResponse> recipes = recipeService.getPublicRecipesByMethod(method, pageable);
        return ResponseEntity.ok(recipes);
    }
    
    // Like operations
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeRecipe(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        recipeService.likeRecipe(id, userId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> unlikeRecipe(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        recipeService.unlikeRecipe(id, userId);
        return ResponseEntity.ok().build();
    }
    
    // Comment operations
    @PostMapping("/{id}/comments")
    public ResponseEntity<RecipeCommentResponse> addComment(
            @PathVariable UUID id,
            @Valid @RequestBody RecipeCommentRequest request,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        RecipeCommentResponse response = recipeService.addComment(id, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<RecipeCommentResponse>> getRecipeComments(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<RecipeCommentResponse> comments = recipeService.getRecipeComments(id, pageable);
        return ResponseEntity.ok(comments);
    }
    
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        recipeService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
