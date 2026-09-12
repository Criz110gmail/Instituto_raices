package escuela.seguridad.repository;

import escuela.seguridad.entity.InvitacionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface InvitacionUsuarioRepository extends JpaRepository<InvitacionUsuario, Long> {
    Optional<InvitacionUsuario> findByTokenHash(String tokenHash);
    List<InvitacionUsuario> findAllByUsuarioIdAndUsadoEnIsNullAndRevocadoEnIsNull(Long usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InvitacionUsuario i where i.tokenHash = :tokenHash")
    Optional<InvitacionUsuario> buscarPorTokenHashConBloqueo(@Param("tokenHash") String tokenHash);
}
