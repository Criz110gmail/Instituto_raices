package escuela.compras.repository;

import escuela.compras.entity.Proveedor;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProveedorRepository extends JpaRepository<Proveedor,Long>,JpaSpecificationExecutor<Proveedor>{
    boolean existsByInstitucionIdAndRfcIgnoreCaseAndIdNot(Long institucionId,String rfc,Long id);
    List<Proveedor> findAllByInstitucionIdAndActivoTrueOrderByRazonSocialAsc(Long institucionId);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Proveedor p where p.id=:id") Optional<Proveedor> findByIdForUpdate(@Param("id")Long id);
}
