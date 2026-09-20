package escuela.finanzas.dto.response;

import escuela.finanzas.entity.EstadoCorteCaja;

import java.math.BigDecimal;
import java.time.Instant;

public record CorteCajaResponse(
        Long id, Long institucionId, Long cuentaId, String cuentaCodigo, String cuentaNombre,
        String moneda, String plantel, EstadoCorteCaja estado,
        Instant abiertoEn, String abiertoPor, Long secuenciaInicial, BigDecimal saldoInicialSistema,
        String observacionesApertura, Instant cerradoEn, String cerradoPor, Long secuenciaFinal,
        Long movimientosContabilizados, BigDecimal totalIngresos, BigDecimal totalEgresos,
        BigDecimal saldoEsperado, BigDecimal efectivoDeclarado, BigDecimal diferencia,
        String justificacionDiferencia, String observacionesCierre, Long version) { }
