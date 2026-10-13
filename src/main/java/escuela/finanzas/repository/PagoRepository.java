package escuela.finanzas.repository;

import escuela.finanzas.entity.Pago;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long>, JpaSpecificationExecutor<Pago> {
    interface CoberturaAnual {
        Long getPagoId();Long getAlumnoId();String getAlumno();String getMatricula();String getConcepto();
        Integer getAnio();Integer getMeses();Integer getMesesPagados();Integer getMesesBonificados();
        java.math.BigDecimal getBeneficio();java.math.BigDecimal getAplicado();String getMoneda();String getEstado();
    }
    @Query(value="SELECT pago_id AS \"pagoId\",alumno_id AS \"alumnoId\",alumno,matricula,concepto,anio,meses,meses_pagados AS \"mesesPagados\",meses_bonificados AS \"mesesBonificados\",beneficio,aplicado,moneda,estado_cobertura AS estado FROM pago_cobertura_anual WHERE pago_id IN (:ids) ORDER BY pago_id,alumno_id,concepto_id,anio",nativeQuery=true)
    java.util.List<CoberturaAnual> coberturasAnuales(@Param("ids")java.util.Collection<Long> ids);
    Optional<Pago> findFirstByAcuerdoAnticipadoIdOrderByIdDesc(Long id);
    boolean existsByAcuerdoAnticipadoIdAndEstadoIn(Long id,java.util.Collection<escuela.finanzas.entity.EstadoPago> estados);
    boolean existsByFolioIgnoreCase(String folio);

    @Query(value = "select nextval('seq_pago_folio')", nativeQuery = true)
    long siguienteNumeroFolio();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pago p where p.id = :id")
    Optional<Pago> findByIdForUpdate(@Param("id") Long id);
}
