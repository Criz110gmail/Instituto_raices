package escuela.admin.dto;

import java.math.BigDecimal;

public record ResumenTesoreria(long movimientos, BigDecimal ingresosOperativos,
                               BigDecimal egresosOperativos, BigDecimal netoOperativo,
                               BigDecimal traspasosEntrada, BigDecimal traspasosSalida,
                               BigDecimal saldoApertura, BigDecimal saldoCierre,
                               boolean saldosDisponibles, String moneda) { }
