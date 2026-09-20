package escuela.admin.dto;

import java.math.BigDecimal;

public record CorteCajaFila(Long id, String cuenta, String plantel, String estado,
                            String abiertoEn, String abiertoPor, String cerradoEn,
                            BigDecimal saldoInicial, BigDecimal saldoEsperado,
                            BigDecimal efectivoDeclarado, BigDecimal diferencia,
                            String moneda) { }
