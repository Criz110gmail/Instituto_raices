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

@Getter
@Setter
public class CargoForm {
    @NotNull private Long institucionId;
    @NotNull private Long plantelId;
    @NotNull private Long inscripcionId;
    @NotNull private Long conceptoCobroId;
    @NotBlank @Size(max = 250) private String descripcion;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate periodoCobroInicio;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate periodoCobroFin;
    private Long periodoAcademicoId;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaEmision = LocalDate.now();
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaVencimiento;
    @NotNull @DecimalMin("0.00") @Digits(integer = 12, fraction = 2)
    private BigDecimal importeOriginal;
    @NotNull @Pattern(regexp = "[A-Za-z]{3}") private String moneda;

    public CargoManualRequest request() {
        return new CargoManualRequest(inscripcionId, conceptoCobroId, descripcion,
                periodoCobroInicio, periodoCobroFin, periodoAcademicoId, fechaEmision,
                fechaVencimiento, importeOriginal, moneda);
    }
}
