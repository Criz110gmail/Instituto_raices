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
import java.time.YearMonth;

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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
    @DateTimeFormat(pattern = "yyyy-MM") private YearMonth primerMes;
    @DateTimeFormat(pattern = "yyyy-MM") private YearMonth ultimoMes;
    private Integer diaVencimiento;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaVencimientoUnico;
    private boolean generacionAutomatica;
    @Size(max = 2000) private String motivoImportePersonalizado;
    @NotNull private EstadoCuota estado = EstadoCuota.ACTIVA;
    private Long version;
    private boolean generarCargoAhora;
    private Long retornoInscripcionId;

    public void prepararCalendario(LocalDate inicioPermitido, LocalDate finPermitido) {
        if (frecuencia == FrecuenciaCuota.UNICA) {
            if (fechaInicio == null) fechaInicio = inicioPermitido;
            if (fechaFin == null) fechaFin = finPermitido;
        } else if (frecuencia == FrecuenciaCuota.MENSUAL) {
            // Preserve partial-month boundaries when editing an existing configuration.
            if (primerMes != null && (fechaInicio == null || !primerMes.equals(YearMonth.from(fechaInicio)))) {
                fechaInicio = primerMes.equals(YearMonth.from(inicioPermitido))
                        ? inicioPermitido : primerMes.atDay(1);
            }
            if (ultimoMes != null && (fechaFin == null || !ultimoMes.equals(YearMonth.from(fechaFin)))) {
                fechaFin = ultimoMes.equals(YearMonth.from(finPermitido))
                        ? finPermitido : ultimoMes.atEndOfMonth();
            }
        }
    }

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
        form.primerMes = YearMonth.from(cuota.fechaInicio());
        form.ultimoMes = YearMonth.from(cuota.fechaFin());
        form.diaVencimiento = cuota.diaVencimiento();
        form.fechaVencimientoUnico = cuota.fechaVencimientoUnico();
        form.generacionAutomatica = cuota.generacionAutomatica();
        form.motivoImportePersonalizado = cuota.motivoImportePersonalizado();
        form.estado = cuota.estado();
        form.version = cuota.auditoria().version();
        return form;
    }
}
