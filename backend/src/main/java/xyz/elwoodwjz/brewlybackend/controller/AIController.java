package xyz.elwoodwjz.brewlybackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import xyz.elwoodwjz.brewlybackend.dto.ai.AIChatRequest;
import xyz.elwoodwjz.brewlybackend.dto.ai.AIChatResponse;
import xyz.elwoodwjz.brewlybackend.entity.User;
import xyz.elwoodwjz.brewlybackend.security.CustomUserDetailsService.CustomUserPrincipal;
import xyz.elwoodwjz.brewlybackend.service.AIService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    
    private final AIService aiService;
    
    @PostMapping("/chat")
    public ResponseEntity<AIChatResponse> chatWithAI(
            Authentication authentication,
            @Valid @RequestBody AIChatRequest request) {
        
        User user = getUserFromAuthentication(authentication);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        
        AIChatResponse response = aiService.chatWithAI(user.getId(), request.getMessage());
        return ResponseEntity.ok(response);
    }
    
    // Helper method to get user from authentication
    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) authentication.getPrincipal()).getUser();
        }
        return null;
    }
}
