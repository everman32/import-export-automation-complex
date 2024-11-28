package by.victory.myapp.repository;

import by.victory.myapp.domain.ProductUnit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductUnit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {}
