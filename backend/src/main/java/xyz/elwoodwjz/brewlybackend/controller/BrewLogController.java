package xyz.elwoodwjz.brewlybackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import xyz.elwoodwjz.brewlybackend.dto.brewlog.*;
import xyz.elwoodwjz.brewlybackend.service.BrewLogService;
import xyz.elwoodwjz.brewlybackend.security.CustomUserDetailsService.CustomUserPrincipal;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/brewlogs")
@RequiredArgsConstructor
public class BrewLogController {
    
    private final BrewLogService brewLogService;

    private UUID getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) authentication.getPrincipal()).getUser().getId();
        }
        return null;
    }
    
    @PostMapping
    public ResponseEntity<BrewLogResponse> createBrewLog(
            @Valid @RequestBody BrewLogRequest request,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        BrewLogResponse response = brewLogService.createBrewLog(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<BrewLogListResponse> getUserBrewLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        BrewLogListResponse response = brewLogService.getUserBrewLogs(userId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BrewLogResponse> getBrewLogById(@PathVariable UUID id, Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        BrewLogResponse response = brewLogService.getBrewLogById(userId, id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BrewLogResponse> updateBrewLog(@PathVariable UUID id, @Valid @RequestBody BrewLogRequest request,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        BrewLogResponse response = brewLogService.updateBrewLog(userId, id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrewLog(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        brewLogService.deleteBrewLog(userId, id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<BrewLogStatisticsResponse> getBrewLogStatistics(
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        BrewLogStatisticsResponse response = brewLogService.getBrewLogStatistics(userId);
        return ResponseEntity.ok(response);
    }
}
