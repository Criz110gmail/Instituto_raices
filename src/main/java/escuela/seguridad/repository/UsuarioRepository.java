package escuela.seguridad.repository;

import escuela.seguridad.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    List<Usuario> findAllByUsernameIgnoreCase(String username);
    Optional<Usuario> findByInstitucionCodigoIgnoreCaseAndUsernameIgnoreCase(String institucionCodigo, String username);
    boolean existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(Long institucionId, String username, Long id);
    boolean existsByInstitucionIdAndEmailIgnoreCaseAndIdNot(Long institucionId, String email, Long id);

    @Query(value = """
            SELECT u.* FROM usuario u
            WHERE u.institucion_id = :institucionId
              AND u.estado <> 'INACTIVO'
              AND u.busqueda_autocomplete LIKE ('%' || lower(:texto) || '%')
              AND NOT EXISTS (
                  SELECT 1 FROM tutor t
                  WHERE t.usuario_id = u.id AND (:tutorId IS NULL OR t.id <> :tutorId)
              )
            ORDER BY u.username, u.id
            """, nativeQuery = true)
    Slice<Usuario> buscarParaAutocompletado(@Param("institucionId") Long institucionId,
                                            @Param("texto") String texto,
                                            @Param("tutorId") Long tutorId,
                                            Pageable limite);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where u.id = :id")
    Optional<Usuario> buscarPorIdConBloqueo(@Param("id") Long id);
}
