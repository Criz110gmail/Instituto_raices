package escuela.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EstadoCuentaCargoFila(Long cargoId, String plantel, String ciclo, String concepto,
                                    String descripcion, LocalDate fechaEmision,
                                    LocalDate fechaVencimiento, BigDecimal importeOriginal,
                                    BigDecimal ajustes, BigDecimal importeTotal,
                                    BigDecimal aplicado, BigDecimal saldo,
                                    String situacion, String moneda) { }
