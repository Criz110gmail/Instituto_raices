package escuela.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteTesoreriaFila(LocalDate periodo, String cuenta, String tipoCuenta,
                                   String alcanceCuenta, long movimientos,
                                   BigDecimal ingresosOperativos, BigDecimal egresosOperativos,
                                   BigDecimal netoOperativo, BigDecimal traspasosEntrada,
                                   BigDecimal traspasosSalida, String moneda) { }
