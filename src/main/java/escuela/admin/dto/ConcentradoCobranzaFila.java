package escuela.admin.dto;

import java.math.BigDecimal;

public record ConcentradoCobranzaFila(String grupo, long cargos, BigDecimal importe,
                                      BigDecimal aplicado, BigDecimal saldo,
                                      BigDecimal vencido, String moneda) { }
