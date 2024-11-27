package by.victory.myapp.repository;

import by.victory.myapp.domain.ImportProd;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ImportProd entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImportProdRepository extends JpaRepository<ImportProd, Long> {}
