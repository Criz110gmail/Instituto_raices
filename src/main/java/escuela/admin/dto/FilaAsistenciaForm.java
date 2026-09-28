package escuela.admin.dto;

import escuela.asistencia.dto.CapturaAsistenciaRequest;
import escuela.asistencia.dto.FilaAsistenciaResponse;
import escuela.asistencia.entity.EstadoAsistencia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FilaAsistenciaForm {
    @NotNull private Long inscripcionId;
    private Long asistenciaId;
    private String matricula;
    private String alumno;
    @NotNull private EstadoAsistencia estado = EstadoAsistencia.PRESENTE;
    @Size(max = 1000) private String observaciones;
    private Long version;
    public CapturaAsistenciaRequest.Fila request() { return new CapturaAsistenciaRequest.Fila(inscripcionId, estado, observaciones, version); }
    public static FilaAsistenciaForm desde(FilaAsistenciaResponse f) {
        FilaAsistenciaForm x = new FilaAsistenciaForm(); x.inscripcionId=f.inscripcionId(); x.asistenciaId=f.asistenciaId();
        x.matricula=f.matricula(); x.alumno=f.alumno(); x.estado=f.estado(); x.observaciones=f.observaciones(); x.version=f.version(); return x;
    }
}
