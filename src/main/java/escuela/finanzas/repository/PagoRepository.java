package escuela.finanzas.repository;

import escuela.finanzas.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PagoRepository extends JpaRepository<Pago, Long>, JpaSpecificationExecutor<Pago> {
    boolean existsByInstitucionIdAndFolioIgnoreCase(Long institucionId, String folio);
}
