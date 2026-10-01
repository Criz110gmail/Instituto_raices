package escuela.compras.repository;

import escuela.compras.entity.Compra;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CompraRepository extends JpaRepository<Compra,Long>,JpaSpecificationExecutor<Compra>{
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select c from Compra c where c.id=:id") Optional<Compra> findByIdForUpdate(@Param("id")Long id);
}
