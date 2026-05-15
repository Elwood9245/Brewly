package xyz.elwoodwjz.brewlybackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import xyz.elwoodwjz.brewlybackend.dto.ai.AIChatRequest;
import xyz.elwoodwjz.brewlybackend.dto.ai.AIChatResponse;
import xyz.elwoodwjz.brewlybackend.entity.User;
import xyz.elwoodwjz.brewlybackend.exception.UnauthorizedException;
import xyz.elwoodwjz.brewlybackend.security.CustomUserDetailsService.CustomUserPrincipal;
import xyz.elwoodwjz.brewlybackend.service.AIService;
import reactor.core.publisher.Flux;

@Slf4j
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

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatWithAIStream(
            Authentication authentication,
            @Valid @RequestBody AIChatRequest request) {

        User user = getUserFromAuthentication(authentication);
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        log.info("Streaming chat request for user: {}", user.getUsername());

        return aiService.chatWithAIStream(user.getId(), request.getMessage())
            .map(content -> ServerSentEvent.<String>builder().data(content).build())
            .doOnError(error -> log.error("Error streaming chat", error));
    }

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) authentication.getPrincipal()).getUser();
        }
        return null;
    }
}
