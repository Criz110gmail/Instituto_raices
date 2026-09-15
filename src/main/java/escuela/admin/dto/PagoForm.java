package escuela.admin.dto;

import escuela.finanzas.dto.request.PagoRequest;
import escuela.finanzas.entity.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PagoForm {
    @NotNull private Long institucionId;
    @NotNull private Long plantelRegistroId;
    @NotNull private Long tutorId;
    private String tutorEtiqueta;
    @Size(max = 180) private String nombrePagador;
    @NotBlank @Size(max = 50) private String folio;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaPago;
    @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2)
    private BigDecimal monto;
    @NotBlank @Pattern(regexp = "[A-Za-z]{3}") private String moneda;
    @NotNull private MetodoPago metodo = MetodoPago.TRANSFERENCIA;
    private Long cuentaDeclaradaId;
    private String cuentaDeclaradaEtiqueta;
    @Size(max = 150) private String referencia;
    @Size(max = 4000) private String observaciones;
    @Valid @Size(max = 100) private List<SolicitudAplicacionPagoForm> solicitudes = new ArrayList<>();

    public PagoRequest request(String zonaHoraria) {
        Instant instante = fechaPago.atZone(ZoneId.of(zonaHoraria)).toInstant();
        return new PagoRequest(institucionId, plantelRegistroId, tutorId, nombrePagador,
                folio, instante, monto, moneda, metodo, cuentaDeclaradaId, referencia,
                observaciones, solicitudes.stream().map(SolicitudAplicacionPagoForm::request).toList());
    }
}
