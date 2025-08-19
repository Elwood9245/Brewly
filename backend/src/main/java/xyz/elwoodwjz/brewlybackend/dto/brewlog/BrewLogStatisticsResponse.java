package xyz.elwoodwjz.brewlybackend.dto.brewlog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrewLogStatisticsResponse {
    
    private int totalBrewLogs;
    private BigDecimal averageRating;
    private int totalBrewsThisMonth;
    private int totalBrewsThisWeek;
    
    // Method statistics
    private List<MethodStatistic> methodStatistics;
    
    // Rating distribution
    private Map<String, Integer> ratingDistribution;
    
    // Most used beans
    private List<BeanStatistic> topBeans;
    
    // Recent activity
    private List<RecentBrew> recentBrews;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MethodStatistic {
        private String method;
        private int count;
        private BigDecimal averageRating;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BeanStatistic {
        private String beanName;
        private int brewCount;
        private BigDecimal averageRating;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentBrew {
        private String beanName;
        private String method;
        private BigDecimal rating;
        private String date;
    }
}
