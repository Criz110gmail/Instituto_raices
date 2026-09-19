package escuela.finanzas.repository;

import escuela.finanzas.entity.MotivoFinanciero;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MotivoFinancieroRepository extends JpaRepository<MotivoFinanciero, Long>,
        JpaSpecificationExecutor<MotivoFinanciero> {
    Optional<MotivoFinanciero> findByInstitucionIdAndCodigoIgnoreCase(Long institucionId, String codigo);
    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId, String codigo, Long id);
    java.util.List<MotivoFinanciero> findAllByInstitucionIdAndActivoTrueOrderByNombreAsc(Long institucionId);

    @Modifying
    @Query(value = """
            INSERT INTO motivo_financiero (institucion_id, codigo, nombre, naturaleza, categoria,
                creado_por_id, actualizado_por_id)
            VALUES (:institucionId, 'COBROS_ESCOLARES', 'Cobros escolares', 'INGRESO', 'COBRANZA',
                :actorId, :actorId)
            ON CONFLICT (institucion_id, lower(codigo)) DO NOTHING
            """, nativeQuery = true)
    int crearCobrosEscolaresSiAusente(@Param("institucionId") Long institucionId,
                                       @Param("actorId") Long actorId);

    @Modifying
    @Query(value = """
            INSERT INTO motivo_financiero (institucion_id, codigo, nombre, naturaleza, categoria,
                creado_por_id, actualizado_por_id)
            VALUES (:institucionId, 'TRASPASO_INTERNO', 'Traspaso interno', 'AMBOS', 'TRANSFERENCIAS',
                :actorId, :actorId)
            ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    int crearTraspasoInternoSiAusente(@Param("institucionId") Long institucionId,
                                      @Param("actorId") Long actorId);
}
