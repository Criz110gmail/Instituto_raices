package escuela.cobranza.repository;

import escuela.cobranza.entity.Cargo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CargoRepository extends JpaRepository<Cargo, Long>, JpaSpecificationExecutor<Cargo> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cargo c where c.id = :id")
    Optional<Cargo> findByIdForUpdate(@Param("id") Long id);

    Optional<Cargo> findByClaveGeneracion(String claveGeneracion);

    Optional<Cargo> findByReemplazaCargoId(Long cargoId);

    @Query("select count(s)>0 from SolicitudAplicacionPago s where s.cargo.id=:id and s.pago.estado='PENDIENTE_VALIDACION'")
    boolean tienePagoEnRevision(@Param("id") Long id);

    @Query("select c.claveGeneracion from Cargo c where c.cuotaAlumno.id=:cuotaId")
    List<String> clavesGeneradasPorCuota(@Param("cuotaId") Long cuotaId);

    List<Cargo> findAllByInscripcionIdOrderByFechaVencimientoDescIdDesc(Long inscripcionId);

    @Query(value = """
            SELECT DISTINCT c.* FROM cargo c
            JOIN inscripcion i ON i.id = c.inscripcion_id
            JOIN alumno a ON a.id = i.alumno_id
            JOIN alumno_tutor v ON v.alumno_id = a.id
            JOIN concepto_cobro cc ON cc.id = c.concepto_cobro_id
            WHERE a.institucion_id = :institucionId
              AND v.tutor_id = :tutorId
              AND v.activo = true AND v.es_responsable_financiero = true
              AND v.fecha_inicio <= CURRENT_DATE
              AND (v.fecha_fin IS NULL OR v.fecha_fin >= CURRENT_DATE)
              AND c.estado_registro = 'EMITIDO'
              AND GREATEST(c.importe_original + COALESCE((
                    SELECT SUM(CASE WHEN ac.efecto = 'AUMENTO' THEN ac.monto ELSE -ac.monto END)
                    FROM ajuste_cargo ac WHERE ac.cargo_id = c.id
                  ), 0), 0) - COALESCE((
                    SELECT SUM(CASE WHEN ap.operacion = 'APLICAR' THEN ap.monto ELSE -ap.monto END)
                    FROM aplicacion_pago ap WHERE ap.cargo_id = c.id
                  ), 0) > 0
              AND (a.busqueda_autocomplete LIKE ('%' || lower(:texto) || '%')
                OR cc.busqueda_autocomplete LIKE ('%' || lower(:texto) || '%'))
            ORDER BY c.fecha_vencimiento, c.id
            """, nativeQuery = true)
    Slice<Cargo> buscarParaSolicitudPago(@Param("institucionId") Long institucionId,
                                         @Param("tutorId") Long tutorId,
                                         @Param("texto") String texto,
                                         Pageable limite);

    String PORTAL_HOY = "(CURRENT_TIMESTAMP AT TIME ZONE inst.zona_horaria)::date";
    @Query(value="""
        SELECT c.* FROM cargo c JOIN inscripcion i ON i.id=c.inscripcion_id
        JOIN alumno a ON a.id=i.alumno_id JOIN institucion inst ON inst.id=a.institucion_id
        JOIN concepto_cobro cc ON cc.id=c.concepto_cobro_id
        WHERE a.institucion_id=:institucionId AND i.plantel_id=:plantelId AND c.moneda=:moneda
          AND c.estado_registro='EMITIDO' AND a.activo=true
          AND EXISTS(SELECT 1 FROM alumno_tutor v JOIN tutor t ON t.id=v.tutor_id
            WHERE v.alumno_id=a.id AND v.tutor_id=:tutorId AND v.activo=true AND t.activo=true
            AND v.es_responsable_financiero=true
            AND v.fecha_inicio <= (CURRENT_TIMESTAMP AT TIME ZONE inst.zona_horaria)::date
            AND (v.fecha_fin IS NULL OR v.fecha_fin >= (CURRENT_TIMESTAMP AT TIME ZONE inst.zona_horaria)::date))
          AND NOT EXISTS(SELECT 1 FROM solicitud_aplicacion_pago s JOIN pago p ON p.id=s.pago_id
            WHERE s.cargo_id=c.id AND p.estado='PENDIENTE_VALIDACION')
          AND GREATEST(c.importe_original+COALESCE((SELECT SUM(CASE WHEN aj.efecto='AUMENTO' THEN aj.monto ELSE -aj.monto END)
            FROM ajuste_cargo aj WHERE aj.cargo_id=c.id),0),0)-COALESCE((SELECT SUM(CASE WHEN ap.operacion='APLICAR' THEN ap.monto ELSE -ap.monto END)
            FROM aplicacion_pago ap WHERE ap.cargo_id=c.id),0)>0
          AND (a.busqueda_autocomplete LIKE ('%'||lower(:texto)||'%') OR cc.busqueda_autocomplete LIKE ('%'||lower(:texto)||'%'))
        ORDER BY c.fecha_vencimiento,c.id
        """,nativeQuery=true)
    Slice<Cargo> buscarParaSaldoFavor(@Param("institucionId")Long institucionId,@Param("tutorId")Long tutorId,
       @Param("plantelId")Long plantelId,@Param("moneda")String moneda,@Param("texto")String texto,Pageable pagina);
    String PORTAL_BASE = """
            SELECT c.* FROM cargo c JOIN inscripcion i ON i.id=c.inscripcion_id
            JOIN alumno a ON a.id=i.alumno_id JOIN plantel pl ON pl.id=i.plantel_id
            JOIN institucion inst ON inst.id=a.institucion_id
            JOIN concepto_cobro cc ON cc.id=c.concepto_cobro_id
            WHERE a.institucion_id=:institucionId AND a.activo=true
              AND EXISTS (SELECT 1 FROM alumno_tutor v JOIN tutor t ON t.id=v.tutor_id
                  WHERE v.alumno_id=a.id AND v.tutor_id=:tutorId AND t.activo=true
                    AND t.institucion_id=:institucionId AND v.activo=true
                    AND v.es_responsable_financiero=true AND v.puede_ver_finanzas=true
                    AND v.fecha_inicio <=
            """ + PORTAL_HOY + " AND (v.fecha_fin IS NULL OR v.fecha_fin >= " + PORTAL_HOY + ")) "
            + " AND c.estado_registro='EMITIDO' AND (c.fecha_vencimiento >= " + PORTAL_HOY
            + " OR pl.permitir_transferencias_vencidas OR c.transferencia_vencida_autorizada) " + """
              AND NOT EXISTS (SELECT 1 FROM solicitud_aplicacion_pago s JOIN pago p ON p.id=s.pago_id
                  WHERE s.cargo_id=c.id AND p.estado='PENDIENTE_VALIDACION')
              AND GREATEST(c.importe_original + COALESCE((SELECT SUM(CASE WHEN aj.efecto='AUMENTO'
                  THEN aj.monto ELSE -aj.monto END) FROM ajuste_cargo aj WHERE aj.cargo_id=c.id),0),0)
                  - COALESCE((SELECT SUM(CASE WHEN ap.operacion='APLICAR' THEN ap.monto ELSE -ap.monto END)
                      FROM aplicacion_pago ap WHERE ap.cargo_id=c.id),0) > 0
            """;

    @Query(value = PORTAL_BASE + """
              AND (a.busqueda_autocomplete LIKE ('%'||lower(:texto)||'%') OR cc.busqueda_autocomplete LIKE ('%'||lower(:texto)||'%'))
            ORDER BY c.fecha_vencimiento,c.id
            """, nativeQuery = true)
    Slice<Cargo> buscarParaPortal(@Param("institucionId") Long institucionId,
                                  @Param("tutorId") Long tutorId, @Param("texto") String texto, Pageable limite);

    @Query(value = PORTAL_BASE + " AND c.id=:cargoId", nativeQuery = true)
    Optional<Cargo> buscarVigenteParaPortal(@Param("cargoId") Long cargoId,
                                            @Param("institucionId") Long institucionId,
                                            @Param("tutorId") Long tutorId);

    @Query("""
            select c from Cargo c join c.conceptoCobro concepto join PoliticaRecargo p
              on p.conceptoCobro.id = concepto.id
            where c.id > :ultimoId and c.estadoRegistro = 'EMITIDO'
              and p.activo = true and p.generacionAutomatica = true
              and concepto.activo = true and concepto.permiteRecargo = true
              and c.fechaVencimiento <= :fechaCorte
              and c.inscripcion.alumno.institucion.id = :institucionId
              and (:plantelId is null or c.inscripcion.plantel.id = :plantelId)
            order by c.id
            """)
    Slice<Cargo> buscarParaRecargo(@Param("institucionId")Long institucionId,
                                    @Param("plantelId")Long plantelId,
                                    @Param("fechaCorte")LocalDate fechaCorte,
                                    @Param("ultimoId")Long ultimoId,Pageable limite);

    @Modifying
    @Query(value = """
            INSERT INTO cargo (
                inscripcion_id, concepto_cobro_id, cuota_alumno_id, clave_generacion,
                descripcion, periodo_cobro_inicio, periodo_cobro_fin, fecha_emision,
                fecha_vencimiento, importe_original, moneda, estado_registro,
                creado_por_id, actualizado_por_id
            ) VALUES (
                :inscripcionId, :conceptoId, :cuotaId, :claveGeneracion,
                :descripcion, :periodoInicio, :periodoFin, :fechaEmision,
                :fechaVencimiento, :importe, :moneda, 'EMITIDO', :actorId, :actorId
            )
            ON CONFLICT (clave_generacion) DO NOTHING
            """, nativeQuery = true)
    int insertarAutomaticoSiAusente(@Param("inscripcionId") Long inscripcionId,
                                     @Param("conceptoId") Long conceptoId,
                                     @Param("cuotaId") Long cuotaId,
                                     @Param("claveGeneracion") String claveGeneracion,
                                     @Param("descripcion") String descripcion,
                                     @Param("periodoInicio") LocalDate periodoInicio,
                                     @Param("periodoFin") LocalDate periodoFin,
                                     @Param("fechaEmision") LocalDate fechaEmision,
                                     @Param("fechaVencimiento") LocalDate fechaVencimiento,
                                     @Param("importe") BigDecimal importe,
                                     @Param("moneda") String moneda,
                                     @Param("actorId") Long actorId);
}
