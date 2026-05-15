package xyz.elwoodwjz.brewlybackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import xyz.elwoodwjz.brewlybackend.config.AIConfig;
import xyz.elwoodwjz.brewlybackend.dto.ai.AIChatResponse;
import xyz.elwoodwjz.brewlybackend.entity.BrewLog;
import xyz.elwoodwjz.brewlybackend.entity.Bean;
import xyz.elwoodwjz.brewlybackend.entity.Recipe;
import xyz.elwoodwjz.brewlybackend.repository.BrewLogRepository;
import xyz.elwoodwjz.brewlybackend.repository.BeanRepository;
import xyz.elwoodwjz.brewlybackend.repository.RecipeRepository;
import xyz.elwoodwjz.brewlybackend.util.Truncate;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
public class AIService {

    private final AIConfig aiConfig;
    private final WebClient deepSeekWebClient;
    private final BrewLogRepository brewLogRepository;
    private final BeanRepository beanRepository;
    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;

    public AIService(AIConfig aiConfig,
                     WebClient deepSeekWebClient,
                     BrewLogRepository brewLogRepository,
                     BeanRepository beanRepository,
                     RecipeRepository recipeRepository,
                     ObjectMapper objectMapper) {
        this.aiConfig = aiConfig;
        this.deepSeekWebClient = deepSeekWebClient;
        this.brewLogRepository = brewLogRepository;
        this.beanRepository = beanRepository;
        this.recipeRepository = recipeRepository;
        this.objectMapper = objectMapper;
    }

    public AIChatResponse chatWithAI(UUID userId, String userMessage) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (userMessage == null || userMessage.trim().isEmpty()) {
                throw new IllegalArgumentException("User message cannot be null or empty");
            }
            if (aiConfig.getApiKey() == null || aiConfig.getApiKey().trim().isEmpty()) {
                throw new IllegalArgumentException("DeepSeek API key is not configured");
            }

            String userContext = buildUserContext(userId);
            String systemPrompt = buildSystemPrompt();

            List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userContext + "\n\nUser question: " + userMessage)
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getModel());
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", aiConfig.getMaxTokens());
            requestBody.put("temperature", aiConfig.getTemperature());
            requestBody.put("stream", false);
            requestBody.put("thinking", Map.of("type", "disabled"));

            JsonNode response = deepSeekWebClient.post()
                .uri(aiConfig.getApiPath())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

            String aiResponse = response.path("choices").get(0)
                .path("message").path("content").asText();

            return AIChatResponse.builder()
                .message(aiResponse)
                .timestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                .build();

        } catch (Exception e) {
            log.error("Error communicating with DeepSeek API", e);
            return AIChatResponse.builder()
                .message("Sorry, I'm having trouble processing your request right now. Please try again later.")
                .timestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                .build();
        }
    }

    public Flux<String> chatWithAIStream(UUID userId, String userMessage) {
        if (userId == null) {
            return Flux.error(new IllegalArgumentException("User ID cannot be null"));
        }
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("User message cannot be null or empty"));
        }
        if (aiConfig.getApiKey() == null || aiConfig.getApiKey().trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("DeepSeek API key is not configured"));
        }

        String userContext = buildUserContext(userId);
        String systemPrompt = buildSystemPrompt();

        List<Map<String, String>> messages = List.of(
            Map.of("role", "system", "content", systemPrompt),
            Map.of("role", "user", "content", userContext + "\n\nUser question: " + userMessage)
        );

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", aiConfig.getMaxTokens());
        requestBody.put("temperature", aiConfig.getTemperature());
        requestBody.put("stream", true);
        requestBody.put("thinking", Map.of("type", "disabled"));

        return deepSeekWebClient.post()
            .uri(aiConfig.getApiPath())
            .bodyValue(requestBody)
            .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(String.class)
            .doOnSubscribe(s -> log.info("Starting DeepSeek stream for user {}, message: {}",
                userId, Truncate.truncate(userMessage, 50)))
            .takeUntil(line -> line.contains("[DONE]"))
            .filter(line -> !line.contains("[DONE]"))
            .map(this::parseChunk)
            .filter(content -> !content.isEmpty())
            .doOnComplete(() -> log.info("DeepSeek stream completed for user {}", userId))
            .onErrorResume(this::handleError);
    }

    String parseChunk(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode delta = root.path("choices").path(0).path("delta");
            if (delta.isMissingNode()) {
                return "";
            }
            String content = delta.path("content").asText(null);
            if (content != null && !content.isEmpty()) {
                return content;
            }
            return delta.path("reasoning_content").asText("");
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse chunk: {}", Truncate.truncate(json, 100), e);
            return "";
        }
    }

    private Flux<String> handleError(Throwable error) {
        if (error instanceof WebClientResponseException e) {
            log.error("DeepSeek API returned {}: {}",
                e.getStatusCode(), Truncate.truncate(e.getResponseBodyAsString(), 200), e);
            return switch (e.getStatusCode().value()) {
                case 401 -> Flux.error(new RuntimeException("API key is invalid", e));
                case 429 -> Flux.error(new RuntimeException("Rate limited by DeepSeek, please wait", e));
                case 500 -> Flux.error(new RuntimeException("DeepSeek server error, please try again later", e));
                default -> Flux.error(new RuntimeException("DeepSeek API error: " + e.getStatusCode(), e));
            };
        }
        log.error("Unexpected error during DeepSeek stream", error);
        return Flux.error(new RuntimeException("An unexpected error occurred", error));
    }

    private String buildSystemPrompt() {
        return """
            You are a knowledgeable coffee brewing assistant for home baristas. Your role is to help users improve their brewing skills by analysing their brew logs and providing personalised advice.

            Key areas of expertise:
            - Coffee brewing methods (pour-over, espresso, French press, etc.)
            - Grind size and its impact on extraction
            - Water temperature and brewing time optimisation
            - Coffee-to-water ratios
            - Bean characteristics and their influence on brewing parameters
            - Troubleshooting common brewing issues
            - Flavour profile analysis and improvement suggestions

            Guidelines for responses:
            - Be encouraging and supportive
            - Provide specific, actionable advice
            - Reference the user's actual brewing data when possible
            - Explain the reasoning behind your suggestions
            - Keep responses concise but informative
            - Use British English spelling and terminology
            - Focus on practical improvements the user can implement
            """;
    }

    private String buildUserContext(UUID userId) {
        StringBuilder context = new StringBuilder();

        List<BrewLog> recentBrewLogs = brewLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (!recentBrewLogs.isEmpty()) {
            context.append("Recent Brew Logs:\n");
            recentBrewLogs.stream()
                .limit(10)
                .forEach(log -> {
                    context.append(String.format("- %s: %s method, %sg beans, %sg water, %s°C, %ds, Rating: %s/5\n",
                        log.getBeanName(),
                        log.getMethod(),
                        log.getBeanWeightGram(),
                        log.getWaterWeightGram(),
                        log.getWaterTemperature(),
                        log.getBrewTimeSeconds(),
                        log.getRating()
                    ));
                    if (log.getTasteNotes() != null && !log.getTasteNotes().trim().isEmpty()) {
                        context.append("  Taste notes: ").append(log.getTasteNotes()).append("\n");
                    }
                });
            context.append("\n");
        }

        List<Bean> activeBeans = beanRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
        if (!activeBeans.isEmpty()) {
            context.append("Current Bean Inventory:\n");
            activeBeans.forEach(bean -> {
                context.append(String.format("- %s (%s, %s roast, %s origin)\n",
                    bean.getName(),
                    bean.getBlend(),
                    bean.getRoastType(),
                    bean.getOrigin() != null ? bean.getOrigin() : "Unknown"
                ));
                if (bean.getFlavour() != null && !bean.getFlavour().trim().isEmpty()) {
                    context.append("  Flavour profile: ").append(bean.getFlavour()).append("\n");
                }
            });
            context.append("\n");
        }

        List<Recipe> userRecipes = recipeRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged()).getContent();
        if (!userRecipes.isEmpty()) {
            context.append("Personal Recipes:\n");
            userRecipes.forEach(recipe -> {
                context.append(String.format("- %s (%s method)\n",
                    recipe.getTitle(),
                    recipe.getMethod()
                ));
            });
            context.append("\n");
        }

        if (!recentBrewLogs.isEmpty()) {
            double avgRating = recentBrewLogs.stream()
                .filter(log -> log.getRating() != null)
                .mapToDouble(log -> log.getRating().doubleValue())
                .average()
                .orElse(0.0);

            context.append(String.format("Brewing Statistics:\n"));
            context.append(String.format("- Average rating: %.1f/5\n", avgRating));
            context.append(String.format("- Total brews logged: %d\n", recentBrewLogs.size()));
            context.append(String.format("- Most used method: %s\n",
                recentBrewLogs.stream()
                    .filter(log -> log.getMethod() != null)
                    .collect(Collectors.groupingBy(BrewLog::getMethod, Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Unknown")));
        }

        return context.toString();
    }
}
