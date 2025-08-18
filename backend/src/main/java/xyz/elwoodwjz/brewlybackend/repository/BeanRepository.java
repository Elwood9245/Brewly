package xyz.elwoodwjz.brewlybackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.elwoodwjz.brewlybackend.entity.Bean;
import java.util.List;
import java.util.UUID;

public interface BeanRepository extends JpaRepository<Bean, UUID> {
    
    /**
     * Find all beans for a specific user
     */
    List<Bean> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    /**
     * Find active beans for a specific user
     */
    List<Bean> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(UUID userId);
}
