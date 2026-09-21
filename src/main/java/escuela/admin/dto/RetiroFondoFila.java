package escuela.admin.dto;

import java.math.BigDecimal;

public record RetiroFondoFila(Long id, String fecha, String cuenta, String plantel,
                              String beneficiario, String motivo, String concepto,
                              String referencia, BigDecimal monto, String moneda,
                              String autorizadoPor, String estado, Long movimientoId) { }
