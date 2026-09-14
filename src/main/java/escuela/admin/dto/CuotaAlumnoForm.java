package escuela.admin.dto;

import escuela.cobranza.dto.request.CuotaAlumnoRequest;
import escuela.cobranza.dto.response.CuotaAlumnoResponse;
import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.entity.FrecuenciaCuota;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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
public class CuotaAlumnoForm {
    @NotNull private Long institucionId;
    @NotNull private Long plantelId;
    @NotNull private Long inscripcionId;
    @NotNull private Long conceptoCobroId;
    @NotNull @DecimalMin("0.00") @Digits(integer = 12, fraction = 2)
    private BigDecimal importeBase;
    @NotNull @Pattern(regexp = "[A-Za-z]{3}") private String moneda;
    @NotNull private FrecuenciaCuota frecuencia = FrecuenciaCuota.MENSUAL;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
    private Integer diaVencimiento;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaVencimientoUnico;
    private boolean generacionAutomatica;
    @Size(max = 2000) private String motivoImportePersonalizado;
    @NotNull private EstadoCuota estado = EstadoCuota.ACTIVA;
    private Long version;

    public CuotaAlumnoRequest request() {
        Integer diaMensual = frecuencia == FrecuenciaCuota.MENSUAL ? diaVencimiento : null;
        LocalDate fechaUnica = frecuencia == FrecuenciaCuota.UNICA ? fechaVencimientoUnico : null;
        return new CuotaAlumnoRequest(inscripcionId, conceptoCobroId, importeBase, moneda,
                frecuencia, fechaInicio, fechaFin, diaMensual, fechaUnica,
                generacionAutomatica, motivoImportePersonalizado, estado, version);
    }

    public static CuotaAlumnoForm desde(CuotaAlumnoResponse cuota) {
        CuotaAlumnoForm form = new CuotaAlumnoForm();
        form.institucionId = cuota.institucionId();
        form.plantelId = cuota.plantelId();
        form.inscripcionId = cuota.inscripcionId();
        form.conceptoCobroId = cuota.conceptoCobroId();
        form.importeBase = cuota.importeBase();
        form.moneda = cuota.moneda();
        form.frecuencia = cuota.frecuencia();
        form.fechaInicio = cuota.fechaInicio();
        form.fechaFin = cuota.fechaFin();
        form.diaVencimiento = cuota.diaVencimiento();
        form.fechaVencimientoUnico = cuota.fechaVencimientoUnico();
        form.generacionAutomatica = cuota.generacionAutomatica();
        form.motivoImportePersonalizado = cuota.motivoImportePersonalizado();
        form.estado = cuota.estado();
        form.version = cuota.auditoria().version();
        return form;
    }
}
