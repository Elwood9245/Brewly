package xyz.elwoodwjz.brewlybackend.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.elwoodwjz.brewlybackend.config.AIConfig;
import xyz.elwoodwjz.brewlybackend.dto.ai.AIChatResponse;
import xyz.elwoodwjz.brewlybackend.entity.BrewLog;
import xyz.elwoodwjz.brewlybackend.entity.Bean;
import xyz.elwoodwjz.brewlybackend.entity.Recipe;
import xyz.elwoodwjz.brewlybackend.repository.BrewLogRepository;
import xyz.elwoodwjz.brewlybackend.repository.BeanRepository;
import xyz.elwoodwjz.brewlybackend.repository.RecipeRepository;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {
    
    private final AIConfig aiConfig;
    private final BrewLogRepository brewLogRepository;
    private final BeanRepository beanRepository;
    private final RecipeRepository recipeRepository;
    
    public AIChatResponse chatWithAI(UUID userId, String userMessage) {
        try {
            // Validate inputs
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (userMessage == null || userMessage.trim().isEmpty()) {
                throw new IllegalArgumentException("User message cannot be null or empty");
            }

            if (aiConfig.getApiKey() == null || aiConfig.getApiKey().trim().isEmpty()) {
                throw new IllegalArgumentException("Anthropic API key is not configured");
            }

            String userContext = buildUserContext(userId);
            
            // Create system prompt for coffee brewing assistant
            String systemPrompt = buildSystemPrompt();
            
            // Create Anthropic client
            AnthropicClient client = AnthropicOkHttpClient.builder()
                .apiKey(aiConfig.getApiKey())
                .build();
            
            // Prepare the full message content
            String fullMessage = systemPrompt + "\n\n" + userContext + "\n\nUser question: " + userMessage;
            
            // Create message parameters
            MessageCreateParams params = MessageCreateParams.builder()
                .model(aiConfig.getModel())
                .maxTokens(aiConfig.getMaxTokens())
                .temperature(aiConfig.getTemperature())
                .addUserMessage(fullMessage)
                .build();
            
            // Make request to Claude API
            Message message = client.messages().create(params);
            
            // Extract the response text from the first content block
            String aiResponse = message.content().get(0).text().orElseThrow(() -> 
                new RuntimeException("No text content in response")).text();
            
            return AIChatResponse.builder()
                .message(aiResponse)
                .timestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                .build();
                
        } catch (Exception e) {
            log.error("Error communicating with Claude API", e);
            return AIChatResponse.builder()
                .message("Sorry, I'm having trouble processing your request right now. Please try again later.")
                .timestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                .build();
        }
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
        
        // Get user's recent brew logs
        List<BrewLog> recentBrewLogs = brewLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (!recentBrewLogs.isEmpty()) {
            context.append("Recent Brew Logs:\n");
            recentBrewLogs.stream()
                .limit(10) // Limit to last 10 brews
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
        
        // Get user's active beans
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
        
        // Get user's recipes
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
        
        // Calculate brewing statistics
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
                    .collect(Collectors.groupingBy(BrewLog::getMethod, Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Unknown")));
        }
        
        return context.toString();
    }
}
