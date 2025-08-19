package xyz.elwoodwjz.brewlybackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.elwoodwjz.brewlybackend.dto.brewlog.*;
import xyz.elwoodwjz.brewlybackend.entity.BrewLog;
import xyz.elwoodwjz.brewlybackend.entity.Bean;
import xyz.elwoodwjz.brewlybackend.repository.BrewLogRepository;
import xyz.elwoodwjz.brewlybackend.repository.BeanRepository;
import xyz.elwoodwjz.brewlybackend.exception.ResourceNotFoundException;
import xyz.elwoodwjz.brewlybackend.exception.UnauthorizedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrewLogService {
    
    private final BrewLogRepository brewLogRepository;
    private final BeanRepository beanRepository;
    
    @Transactional
    public BrewLogResponse createBrewLog(UUID userId, BrewLogRequest request) {
        // Validate that the bean exists and belongs to the user
        Bean bean = beanRepository.findById(request.getBeanId())
            .orElseThrow(() -> new ResourceNotFoundException("Bean not found"));
        
        if (!bean.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only create brew logs for your own beans");
        }
        
        BrewLog brewLog = BrewLog.builder()
            .userId(userId)
            .beanId(request.getBeanId())
            .beanName(request.getBeanName())
            .method(request.getMethod())
            .grindSize(request.getGrindSize())
            .beanWeightGram(request.getBeanWeightGram())
            .waterWeightGram(request.getWaterWeightGram())
            .waterTemperature(request.getWaterTemperature())
            .brewTimeSeconds(request.getBrewTimeSeconds())
            .tasteNotes(request.getTasteNotes())
            .rating(request.getRating())
            .recipeId(request.getRecipeId())
            .importedRecipeTitle(request.getImportedRecipeTitle())
            .importedRecipeMethod(request.getImportedRecipeMethod())
            .importedRecipeSteps(request.getImportedRecipeSteps())
            .build();
        
        BrewLog savedBrewLog = brewLogRepository.save(brewLog);
        return convertToResponse(savedBrewLog);
    }
    
    @Transactional(readOnly = true)
    public BrewLogResponse getBrewLogById(UUID userId, UUID brewLogId) {
        BrewLog brewLog = brewLogRepository.findById(brewLogId)
            .orElseThrow(() -> new ResourceNotFoundException("Brew log not found"));
        
        if (!brewLog.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only access your own brew logs");
        }
        
        return convertToResponse(brewLog);
    }
    
    @Transactional(readOnly = true)
    public BrewLogListResponse getUserBrewLogs(UUID userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "DESC"), 
                           sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BrewLog> brewLogPage = brewLogRepository.findAll(pageable);
        
        List<BrewLogResponse> brewLogResponses = brewLogPage.getContent().stream()
            .filter(brewLog -> brewLog.getUserId().equals(userId))
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return BrewLogListResponse.builder()
            .brewLogs(brewLogResponses)
            .totalElements((int) brewLogPage.getTotalElements())
            .totalPages(brewLogPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .hasNext(brewLogPage.hasNext())
            .hasPrevious(brewLogPage.hasPrevious())
            .build();
    }
    
    @Transactional
    public BrewLogResponse updateBrewLog(UUID userId, UUID brewLogId, BrewLogRequest request) {
        BrewLog brewLog = brewLogRepository.findById(brewLogId)
            .orElseThrow(() -> new ResourceNotFoundException("Brew log not found"));
        
        if (!brewLog.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only update your own brew logs");
        }
        
        // Update fields
        brewLog.setBeanId(request.getBeanId());
        brewLog.setBeanName(request.getBeanName());
        brewLog.setMethod(request.getMethod());
        brewLog.setGrindSize(request.getGrindSize());
        brewLog.setBeanWeightGram(request.getBeanWeightGram());
        brewLog.setWaterWeightGram(request.getWaterWeightGram());
        brewLog.setWaterTemperature(request.getWaterTemperature());
        brewLog.setBrewTimeSeconds(request.getBrewTimeSeconds());
        brewLog.setTasteNotes(request.getTasteNotes());
        brewLog.setRating(request.getRating());
        brewLog.setRecipeId(request.getRecipeId());
        brewLog.setImportedRecipeTitle(request.getImportedRecipeTitle());
        brewLog.setImportedRecipeMethod(request.getImportedRecipeMethod());
        brewLog.setImportedRecipeSteps(request.getImportedRecipeSteps());
        
        BrewLog updatedBrewLog = brewLogRepository.save(brewLog);
        return convertToResponse(updatedBrewLog);
    }
    
    @Transactional
    public void deleteBrewLog(UUID userId, UUID brewLogId) {
        BrewLog brewLog = brewLogRepository.findById(brewLogId)
            .orElseThrow(() -> new ResourceNotFoundException("Brew log not found"));
        
        if (!brewLog.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own brew logs");
        }
        
        brewLogRepository.delete(brewLog);
    }
    
    @Transactional(readOnly = true)
    public BrewLogStatisticsResponse getBrewLogStatistics(UUID userId) {
        List<BrewLog> userBrewLogs = brewLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        // Calculate basic statistics
        int totalBrewLogs = userBrewLogs.size();
        BigDecimal averageRating = calculateAverageRating(userBrewLogs);
        
        // Calculate brews this month and week
        Instant now = Instant.now();
        Instant monthAgo = now.minusSeconds(30 * 24 * 60 * 60);
        Instant weekAgo = now.minusSeconds(7 * 24 * 60 * 60);
        
        int brewsThisMonth = (int) userBrewLogs.stream()
            .filter(brewLog -> brewLog.getCreatedAt().isAfter(monthAgo))
            .count();
        
        int brewsThisWeek = (int) userBrewLogs.stream()
            .filter(brewLog -> brewLog.getCreatedAt().isAfter(weekAgo))
            .count();
        
        // Method statistics
        List<BrewLogStatisticsResponse.MethodStatistic> methodStats = calculateMethodStatistics(userBrewLogs);
        
        // Rating distribution
        Map<String, Integer> ratingDistribution = calculateRatingDistribution(userBrewLogs);
        
        // Top beans
        List<BrewLogStatisticsResponse.BeanStatistic> topBeans = calculateTopBeans(userBrewLogs);
        
        // Recent brews
        List<BrewLogStatisticsResponse.RecentBrew> recentBrews = userBrewLogs.stream()
            .limit(5)
            .map(this::convertToRecentBrew)
            .collect(Collectors.toList());
        
        return BrewLogStatisticsResponse.builder()
            .totalBrewLogs(totalBrewLogs)
            .averageRating(averageRating)
            .totalBrewsThisMonth(brewsThisMonth)
            .totalBrewsThisWeek(brewsThisWeek)
            .methodStatistics(methodStats)
            .ratingDistribution(ratingDistribution)
            .topBeans(topBeans)
            .recentBrews(recentBrews)
            .build();
    }
    
    private BrewLogResponse convertToResponse(BrewLog brewLog) {
        BigDecimal ratio = null;
        if (brewLog.getBeanWeightGram() != null && brewLog.getWaterWeightGram() != null && 
            brewLog.getBeanWeightGram().compareTo(BigDecimal.ZERO) > 0) {
            ratio = brewLog.getWaterWeightGram().divide(brewLog.getBeanWeightGram(), 2, RoundingMode.HALF_UP);
        }
        
        return BrewLogResponse.builder()
            .id(brewLog.getId())
            .userId(brewLog.getUserId())
            .beanId(brewLog.getBeanId())
            .beanName(brewLog.getBeanName())
            .method(brewLog.getMethod())
            .grindSize(brewLog.getGrindSize())
            .beanWeightGram(brewLog.getBeanWeightGram())
            .waterWeightGram(brewLog.getWaterWeightGram())
            .waterTemperature(brewLog.getWaterTemperature())
            .brewTimeSeconds(brewLog.getBrewTimeSeconds())
            .tasteNotes(brewLog.getTasteNotes())

            .rating(brewLog.getRating())
            .recipeId(brewLog.getRecipeId())
            .importedRecipeTitle(brewLog.getImportedRecipeTitle())
            .importedRecipeMethod(brewLog.getImportedRecipeMethod())
            .importedRecipeSteps(brewLog.getImportedRecipeSteps())
            .createdAt(brewLog.getCreatedAt())
            .updatedAt(brewLog.getUpdatedAt())
            .ratio(ratio)
            .formattedBrewTime(formatBrewTime(brewLog.getBrewTimeSeconds()))
            .formattedCreatedAt(formatInstant(brewLog.getCreatedAt()))
            .formattedUpdatedAt(formatInstant(brewLog.getUpdatedAt()))
            .build();
    }
    
    private String formatBrewTime(Integer seconds) {
        if (seconds == null) return null;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
    
    private String formatInstant(Instant instant) {
        if (instant == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
    
    /**
     * Calculate average rating from a list of brew logs
     * @param brewLogs List of brew logs to calculate average rating from
     * @return Average rating as BigDecimal, or null if no rated brews
     */
    private BigDecimal calculateAverageRating(List<BrewLog> brewLogs) {
        List<BrewLog> ratedBrews = brewLogs.stream()
            .filter(brewLog -> brewLog.getRating() != null)
            .toList();
        
        if (ratedBrews.isEmpty()) {
            return null;
        }
        
        BigDecimal totalRating = ratedBrews.stream()
            .map(BrewLog::getRating)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalRating.divide(BigDecimal.valueOf(ratedBrews.size()), 2, RoundingMode.HALF_UP);
    }
    
    private List<BrewLogStatisticsResponse.MethodStatistic> calculateMethodStatistics(List<BrewLog> brewLogs) {
        Map<String, List<BrewLog>> methodGroups = brewLogs.stream()
            .filter(brewLog -> brewLog.getMethod() != null)
            .collect(Collectors.groupingBy(BrewLog::getMethod));
        
        return methodGroups.entrySet().stream()
            .map(entry -> {
                List<BrewLog> methodBrews = entry.getValue();
                BigDecimal avgRating = calculateAverageRating(methodBrews);
                
                return BrewLogStatisticsResponse.MethodStatistic.builder()
                    .method(entry.getKey())
                    .count(methodBrews.size())
                    .averageRating(avgRating)
                    .build();
            })
            .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
            .collect(Collectors.toList());
    }
    
    private Map<String, Integer> calculateRatingDistribution(List<BrewLog> brewLogs) {
        Map<String, Integer> distribution = new HashMap<>();
        for (int i = 0; i <= 10; i++) {
            distribution.put(String.valueOf(i), 0);
        }
        
        brewLogs.stream()
            .filter(brewLog -> brewLog.getRating() != null)
            .forEach(brewLog -> {
                int rating = brewLog.getRating().intValue();
                String key = String.valueOf(rating);
                distribution.put(key, distribution.get(key) + 1);
            });
        
        return distribution;
    }
    
    private List<BrewLogStatisticsResponse.BeanStatistic> calculateTopBeans(List<BrewLog> brewLogs) {
        Map<String, List<BrewLog>> beanGroups = brewLogs.stream()
            .collect(Collectors.groupingBy(BrewLog::getBeanName));
        
        return beanGroups.entrySet().stream()
            .map(entry -> {
                List<BrewLog> beanBrews = entry.getValue();
                BigDecimal avgRating = calculateAverageRating(beanBrews);
                
                return BrewLogStatisticsResponse.BeanStatistic.builder()
                    .beanName(entry.getKey())
                    .brewCount(beanBrews.size())
                    .averageRating(avgRating)
                    .build();
            })
            .sorted((a, b) -> Integer.compare(b.getBrewCount(), a.getBrewCount()))
            .limit(5)
            .collect(Collectors.toList());
    }
    
    private BrewLogStatisticsResponse.RecentBrew convertToRecentBrew(BrewLog brewLog) {
        return BrewLogStatisticsResponse.RecentBrew.builder()
            .beanName(brewLog.getBeanName())
            .method(brewLog.getMethod())
            .rating(brewLog.getRating())
            .date(formatInstant(brewLog.getCreatedAt()))
            .build();
    }
}
