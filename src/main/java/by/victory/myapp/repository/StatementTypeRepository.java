package by.victory.myapp.repository;

import by.victory.myapp.domain.StatementType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StatementType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatementTypeRepository extends JpaRepository<StatementType, Long> {}
