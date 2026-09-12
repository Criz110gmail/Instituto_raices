package escuela.seguridad.repository;

import escuela.seguridad.entity.RecuperacionPassword;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecuperacionPasswordRepository extends JpaRepository<RecuperacionPassword, Long> {
    List<RecuperacionPassword> findAllByUsuarioIdAndUsadoEnIsNullAndRevocadoEnIsNull(Long usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RecuperacionPassword r join fetch r.usuario where r.tokenHash = :tokenHash")
    Optional<RecuperacionPassword> buscarPorTokenHashConBloqueo(@Param("tokenHash") String tokenHash);
}
