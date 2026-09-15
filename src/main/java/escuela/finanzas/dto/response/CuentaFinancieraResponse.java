package escuela.finanzas.dto.response;

import escuela.common.dto.response.AuditoriaResponse;
import escuela.finanzas.entity.TipoCuentaFinanciera;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuentaFinancieraResponse(
        Long id,
        Long institucionId,
        String institucionNombre,
        Long plantelId,
        String plantelNombre,
        String codigo,
        String nombre,
        TipoCuentaFinanciera tipo,
        String bancoNombre,
        String titular,
        String numeroCuenta,
        String clabe,
        String moneda,
        BigDecimal saldoInicial,
        LocalDate fechaSaldoInicial,
        boolean activo,
        AuditoriaResponse auditoria
) { }
