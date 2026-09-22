package escuela.portal.dto;

import escuela.finanzas.dto.request.PagoRequest;
import escuela.finanzas.entity.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Getter @Setter
public class PortalPagoForm {
    @NotNull private Long plantelRegistroId;
    private String plantelEtiqueta;
    @NotNull private LocalDateTime fechaPago;
    @NotNull @DecimalMin("0.01") @Digits(integer=17, fraction=2) private BigDecimal monto;
    @NotNull private Long cuentaDeclaradaId;
    private String cuentaDeclaradaEtiqueta;
    @Size(max=180) private String nombrePagador;
    @NotBlank @Size(max=150) private String referencia;
    @Size(max=4000) private String observaciones;
    @Valid @Size(min=1, max=100) private List<PortalSolicitudPagoForm> solicitudes = new ArrayList<>();

    public PagoRequest request(Long institucionId, Long tutorId, String moneda, ZoneId zona) {
        return new PagoRequest(institucionId, plantelRegistroId, tutorId, nombrePagador,
                "FAM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT),
                fechaPago.atZone(zona).toInstant(), monto, moneda, MetodoPago.TRANSFERENCIA,
                cuentaDeclaradaId, referencia, observaciones,
                solicitudes.stream().map(PortalSolicitudPagoForm::request).toList());
    }
}
