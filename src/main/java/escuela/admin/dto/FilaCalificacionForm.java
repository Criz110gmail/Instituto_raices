package escuela.admin.dto;

import escuela.calificacion.dto.CapturaCalificacionRequest;
import escuela.calificacion.dto.FilaCalificacionResponse;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FilaCalificacionForm {
    private Long inscripcionId;
    private Long calificacionId;
    private String matricula;
    private String alumno;
    private BigDecimal valorNumerico;
    @Size(max = 100) private String valorCualitativo;
    @Size(max = 1000) private String observaciones;
    private Long version;

    public CapturaCalificacionRequest.Fila request() {
        return new CapturaCalificacionRequest.Fila(inscripcionId, calificacionId,
                valorNumerico, valorCualitativo, observaciones, version);
    }

    public static FilaCalificacionForm desde(FilaCalificacionResponse fila) {
        FilaCalificacionForm form = new FilaCalificacionForm();
        form.inscripcionId = fila.inscripcionId();
        form.calificacionId = fila.calificacionId();
        form.matricula = fila.matricula();
        form.alumno = fila.alumno();
        form.valorNumerico = fila.valorNumerico();
        form.valorCualitativo = fila.valorCualitativo();
        form.observaciones = fila.observaciones();
        form.version = fila.version();
        return form;
    }
}
