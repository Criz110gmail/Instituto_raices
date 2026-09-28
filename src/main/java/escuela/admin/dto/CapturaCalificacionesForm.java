package escuela.admin.dto;

import escuela.calificacion.dto.CapturaCalificacionRequest;
import escuela.calificacion.dto.HojaCalificacionesResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CapturaCalificacionesForm {
    @NotNull private Long institucionId;
    @NotNull private Long grupoId;
    private String grupoTexto;
    @NotNull private Long periodoId;
    private String periodoTexto;
    @NotNull private Long materiaGradoId;
    private String materiaTexto;
    @Valid private List<FilaCalificacionForm> filas = new ArrayList<>();

    public CapturaCalificacionRequest request() {
        return new CapturaCalificacionRequest(grupoId, periodoId, materiaGradoId,
                filas.stream().map(FilaCalificacionForm::request).toList());
    }

    public static CapturaCalificacionesForm desde(HojaCalificacionesResponse hoja,
                                                   Long institucionId) {
        CapturaCalificacionesForm form = new CapturaCalificacionesForm();
        form.institucionId = institucionId;
        form.grupoId = hoja.grupoId();
        form.grupoTexto = hoja.grupo() + " · " + hoja.grado();
        form.periodoId = hoja.periodoId();
        form.periodoTexto = hoja.periodo();
        form.materiaGradoId = hoja.materiaGradoId();
        form.materiaTexto = hoja.materia();
        form.filas = hoja.filas().stream().map(FilaCalificacionForm::desde).toList();
        return form;
    }
}
