package escuela.comunicacion.repository;

import escuela.comunicacion.entity.NotificacionUsuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificacionUsuarioRepository extends JpaRepository<NotificacionUsuario,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths={"usuario","evento","aviso","pago"})
    @Query("select n from NotificacionUsuario n where n.id=:id") Optional<NotificacionUsuario> findByIdForUpdate(@Param("id")Long id);
}
