package escuela.seguridad.repository;

import escuela.seguridad.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    List<Usuario> findAllByInstitucionIdOrderByUsernameAsc(Long institucionId);
    Optional<Usuario> findByInstitucionCodigoIgnoreCaseAndUsernameIgnoreCase(String institucionCodigo, String username);
    boolean existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(Long institucionId, String username, Long id);
    boolean existsByInstitucionIdAndEmailIgnoreCaseAndIdNot(Long institucionId, String email, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where u.id = :id")
    Optional<Usuario> buscarPorIdConBloqueo(@Param("id") Long id);
}
