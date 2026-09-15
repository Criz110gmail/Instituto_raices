package escuela.cobranza.repository;

import escuela.cobranza.entity.AjusteCargo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface AjusteCargoRepository extends JpaRepository<AjusteCargo, Long>, JpaSpecificationExecutor<AjusteCargo> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AjusteCargo a where a.id = :id")
    Optional<AjusteCargo> findByIdForUpdate(@Param("id") Long id);
    boolean existsByReversaDeId(Long ajusteId);
    boolean existsByCargoIdAndBecaAlumnoIdAndReversaDeIsNull(Long cargoId, Long becaId);

    @Query("""
            select coalesce(sum(case when a.efecto = 'AUMENTO' then a.monto else -a.monto end), 0)
            from AjusteCargo a
            where a.cargo.id = :cargoId
              and (a.politicaRecargo.id = :politicaId
                   or a.reversaDe.politicaRecargo.id = :politicaId)
            """)
    BigDecimal totalRecargosAutomaticos(@Param("cargoId")Long cargoId,@Param("politicaId")Long politicaId);

    @Modifying
    @Query(value="""
      INSERT INTO ajuste_cargo(cargo_id,tipo,efecto,monto,base_calculo,porcentaje_aplicado,
        motivo,autorizado_por_id,fecha_efectiva,politica_recargo_id,clave_generacion,
        creado_por_id,actualizado_por_id)
      VALUES(:cargoId,'RECARGO','AUMENTO',:monto,:baseCalculo,:porcentaje,:motivo,:actorId,
        :fechaEfectiva,:politicaId,:clave,:actorId,:actorId)
      ON CONFLICT (clave_generacion) WHERE clave_generacion IS NOT NULL DO NOTHING
      """,nativeQuery=true)
    int insertarRecargoSiAusente(@Param("cargoId")Long cargoId,@Param("monto")BigDecimal monto,
      @Param("baseCalculo")BigDecimal baseCalculo,@Param("porcentaje")BigDecimal porcentaje,
      @Param("motivo")String motivo,@Param("actorId")Long actorId,@Param("fechaEfectiva")LocalDate fechaEfectiva,
      @Param("politicaId")Long politicaId,@Param("clave")String clave);
}
