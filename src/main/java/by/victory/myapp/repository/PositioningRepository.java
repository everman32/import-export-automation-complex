package by.victory.myapp.repository;

import by.victory.myapp.domain.Positioning;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Positioning entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PositioningRepository extends JpaRepository<Positioning, Long> {}
