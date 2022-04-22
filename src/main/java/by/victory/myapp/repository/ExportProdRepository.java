package by.victory.myapp.repository;

import by.victory.myapp.domain.ExportProd;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ExportProd entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExportProdRepository extends JpaRepository<ExportProd, Long> {}
