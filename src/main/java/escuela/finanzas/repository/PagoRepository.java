package escuela.finanzas.repository;

import escuela.finanzas.entity.Pago;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long>, JpaSpecificationExecutor<Pago> {
    boolean existsByFolioIgnoreCase(String folio);

    @Query(value = "select nextval('seq_pago_folio')", nativeQuery = true)
    long siguienteNumeroFolio();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pago p where p.id = :id")
    Optional<Pago> findByIdForUpdate(@Param("id") Long id);
}
