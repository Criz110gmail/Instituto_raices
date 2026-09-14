package escuela.admin.dto;

import escuela.inscripcion.dto.request.InscripcionRequest;
import escuela.inscripcion.dto.response.InscripcionResponse;
import escuela.inscripcion.entity.EstadoInscripcion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class InscripcionForm {
    @NotNull private Long institucionId;
    @NotNull private Long alumnoId;
    @NotNull private Long plantelId;
    @NotNull private Long cicloEscolarId;
    @NotNull private Long gradoId;
    @NotBlank @Size(max = 60) private String numeroInscripcion;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInscripcion = LocalDate.now();
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio = LocalDate.now();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;
    @NotNull private EstadoInscripcion estado = EstadoInscripcion.PREINSCRITA;
    @Size(max = 2000) private String motivoBajaCancelacion;
    private Long inscripcionAnteriorId;
    @Size(max = 4000) private String observaciones;
    private Long version;

    public InscripcionRequest request() {
        return new InscripcionRequest(alumnoId, plantelId, cicloEscolarId, gradoId,
                numeroInscripcion, fechaInscripcion, fechaInicio, fechaFin, estado,
                motivoBajaCancelacion, inscripcionAnteriorId, observaciones, version);
    }

    public static InscripcionForm desde(InscripcionResponse r) {
        InscripcionForm f = new InscripcionForm();
        f.institucionId = r.institucionId();
        f.alumnoId = r.alumnoId();
        f.plantelId = r.plantelId();
        f.cicloEscolarId = r.cicloEscolarId();
        f.gradoId = r.gradoId();
        f.numeroInscripcion = r.numeroInscripcion();
        f.fechaInscripcion = r.fechaInscripcion();
        f.fechaInicio = r.fechaInicio();
        f.fechaFin = r.fechaFin();
        f.estado = r.estado();
        f.motivoBajaCancelacion = r.motivoBajaCancelacion();
        f.inscripcionAnteriorId = r.inscripcionAnteriorId();
        f.observaciones = r.observaciones();
        f.version = r.auditoria().version();
        return f;
    }

    public static InscripcionForm continuidad(InscripcionResponse anterior) {
        InscripcionForm f = new InscripcionForm();
        f.institucionId = anterior.institucionId();
        f.alumnoId = anterior.alumnoId();
        f.inscripcionAnteriorId = anterior.id();
        f.fechaInicio = anterior.fechaFin() == null ? LocalDate.now()
                : anterior.fechaFin().plusDays(1);
        f.fechaInscripcion = LocalDate.now();
        f.estado = EstadoInscripcion.PREINSCRITA;
        return f;
    }
}
