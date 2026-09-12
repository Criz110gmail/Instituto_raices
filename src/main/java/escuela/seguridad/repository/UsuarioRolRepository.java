package escuela.seguridad.repository;

import escuela.seguridad.entity.AlcanceRol;
import escuela.seguridad.entity.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {
    List<UsuarioRol> findAllByUsuarioIdOrderByRolNombreAsc(Long usuarioId);
    boolean existsByUsuarioIdAndRolIdAndAlcanceAndPlantelId(
            Long usuarioId, Long rolId, AlcanceRol alcance, Long plantelId);
    boolean existsByUsuarioIdAndRolIdAndAlcanceAndPlantelIsNull(
            Long usuarioId, Long rolId, AlcanceRol alcance);
}
