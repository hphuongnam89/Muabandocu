package com.cho2hand.marketplace.repository.location;
import com.cho2hand.marketplace.entity.location.Location;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByIdAndActiveTrue(Long id);
    List<Location> findByActiveTrueOrderByNameAsc();
    List<Location> findByParentLocationIdAndActiveTrue(Long parentLocationId);
}
