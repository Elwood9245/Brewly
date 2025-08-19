package xyz.elwoodwjz.brewlybackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xyz.elwoodwjz.brewlybackend.entity.BrewLog;
import java.util.UUID;
import java.util.List;
import java.time.Instant;

@Repository
public interface BrewLogRepository extends JpaRepository<BrewLog, UUID> {
    
    // Find all brew logs for a specific user
    List<BrewLog> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    // Find brew logs by user with pagination
    @Query("SELECT bl FROM BrewLog bl WHERE bl.userId = :userId ORDER BY bl.createdAt DESC")
    List<BrewLog> findByUserIdWithPagination(@Param("userId") UUID userId);
    
    // Find brew logs by bean
    List<BrewLog> findByBeanIdOrderByCreatedAtDesc(UUID beanId);
    
    // Find brew logs by recipe
    List<BrewLog> findByRecipeIdOrderByCreatedAtDesc(UUID recipeId);
    
    // Search brew logs by taste notes (case-insensitive)
    @Query("SELECT bl FROM BrewLog bl WHERE bl.userId = :userId AND LOWER(bl.tasteNotes) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY bl.createdAt DESC")
    List<BrewLog> searchByTasteNotes(@Param("userId") UUID userId, @Param("searchTerm") String searchTerm);
    
    // Find brew logs by method
    List<BrewLog> findByUserIdAndMethodOrderByCreatedAtDesc(UUID userId, String method);
    
    // Find brew logs by rating range
    @Query("SELECT bl FROM BrewLog bl WHERE bl.userId = :userId AND bl.rating BETWEEN :minRating AND :maxRating ORDER BY bl.createdAt DESC")
    List<BrewLog> findByRatingRange(@Param("userId") UUID userId, @Param("minRating") Double minRating, @Param("maxRating") Double maxRating);
    
    // Find brew logs by date range
    @Query("SELECT bl FROM BrewLog bl WHERE bl.userId = :userId AND bl.createdAt BETWEEN :startDate AND :endDate ORDER BY bl.createdAt DESC")
    List<BrewLog> findByDateRange(@Param("userId") UUID userId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    // Count brew logs by user
    long countByUserId(UUID userId);
    
    // Get average rating by user
    @Query("SELECT AVG(bl.rating) FROM BrewLog bl WHERE bl.userId = :userId AND bl.rating IS NOT NULL")
    Double getAverageRatingByUser(@Param("userId") UUID userId);
    
    // Get most used methods by user
    @Query("SELECT bl.method, COUNT(bl) FROM BrewLog bl WHERE bl.userId = :userId AND bl.method IS NOT NULL GROUP BY bl.method ORDER BY COUNT(bl) DESC")
    List<Object[]> getMostUsedMethodsByUser(@Param("userId") UUID userId);
}
