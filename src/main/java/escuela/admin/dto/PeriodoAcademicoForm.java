package escuela.admin.dto;

import escuela.academico.dto.request.PeriodoAcademicoRequest;
import escuela.academico.dto.response.PeriodoAcademicoResponse;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.TipoPeriodoAcademico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PeriodoAcademicoForm {
    @NotNull private Long institucionId;
    @NotNull private Long cicloEscolarId;
    @NotNull private Long nivelEducativoId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 150) private String nombre;
    @NotNull private TipoPeriodoAcademico tipo = TipoPeriodoAcademico.TRIMESTRE;
    @Positive private int orden = 1;
    @NotNull private LocalDate fechaInicio;
    @NotNull private LocalDate fechaFin;
    @NotNull private EstadoAcademico estado = EstadoAcademico.PLANIFICADO;
    private String observaciones;
    private Long version;

    public PeriodoAcademicoRequest request() {
        return new PeriodoAcademicoRequest(cicloEscolarId, nivelEducativoId, codigo, nombre,
                tipo, orden, fechaInicio, fechaFin, estado, observaciones, version);
    }

    public static PeriodoAcademicoForm desde(PeriodoAcademicoResponse periodo, Long institucionId) {
        PeriodoAcademicoForm form = new PeriodoAcademicoForm();
        form.institucionId = institucionId;
        form.cicloEscolarId = periodo.cicloEscolarId();
        form.nivelEducativoId = periodo.nivelEducativoId();
        form.codigo = periodo.codigo();
        form.nombre = periodo.nombre();
        form.tipo = periodo.tipo();
        form.orden = periodo.orden();
        form.fechaInicio = periodo.fechaInicio();
        form.fechaFin = periodo.fechaFin();
        form.estado = periodo.estado();
        form.observaciones = periodo.observaciones();
        form.version = periodo.auditoria().version();
        return form;
    }
}
