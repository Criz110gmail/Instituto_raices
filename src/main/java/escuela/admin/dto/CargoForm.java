package escuela.admin.dto;

import escuela.cobranza.dto.request.CargoManualRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
public class CargoForm {
    @NotNull private Long institucionId;
    @NotNull private Long plantelId;
    @NotNull private Long inscripcionId;
    @NotNull private Long conceptoCobroId;
    @NotBlank @Size(max = 250) private String descripcion;
    @NotNull private ModoPeriodoCargo modoPeriodo = ModoPeriodoCargo.MES_COMPLETO;
    @DateTimeFormat(pattern = "yyyy-MM") private YearMonth mesPeriodo = YearMonth.now();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaEspecifica = LocalDate.now();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate periodoCobroInicio;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate periodoCobroFin;
    private Long periodoAcademicoId;
    private boolean modificarFechaRegistro;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaEmision = LocalDate.now();
    @Size(max = 1000) private String motivoFechaRegistroDiferente;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaVencimiento;
    @NotNull @DecimalMin("0.00") @Digits(integer = 12, fraction = 2)
    private BigDecimal importeOriginal;
    @NotNull @Pattern(regexp = "[A-Za-z]{3}") private String moneda;

    public CargoManualRequest request() {
        LocalDate inicio = switch (modoPeriodo) {
            case MES_COMPLETO -> mesPeriodo.atDay(1);
            case FECHA_ESPECIFICA -> fechaEspecifica;
            case RANGO_PERSONALIZADO -> periodoCobroInicio;
        };
        LocalDate fin = switch (modoPeriodo) {
            case MES_COMPLETO -> mesPeriodo.atEndOfMonth();
            case FECHA_ESPECIFICA -> fechaEspecifica;
            case RANGO_PERSONALIZADO -> periodoCobroFin;
        };
        return new CargoManualRequest(inscripcionId, conceptoCobroId, descripcion,
                inicio, fin, periodoAcademicoId, fechaEmision,
                modificarFechaRegistro ? motivoFechaRegistroDiferente : null,
                fechaVencimiento, importeOriginal, moneda);
    }
}
