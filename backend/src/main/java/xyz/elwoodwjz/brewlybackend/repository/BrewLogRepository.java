package xyz.elwoodwjz.brewlybackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.elwoodwjz.brewlybackend.entity.BrewLog;
import java.util.UUID;
import java.util.List;

@Repository
public interface BrewLogRepository extends JpaRepository<BrewLog, UUID> {
    
    // Find all brew logs for a specific user
    List<BrewLog> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    // Count brew logs by user
    long countByUserId(UUID userId);
}
