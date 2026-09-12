package escuela.admin.dto;

import escuela.academico.dto.request.GrupoRequest;
import escuela.academico.dto.response.GrupoResponse;
import escuela.academico.entity.Turno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoForm {
    @NotNull private Long institucionId;
    @NotNull private Long plantelId;
    @NotNull private Long cicloEscolarId;
    @NotNull private Long gradoId;
    @NotBlank @Size(max = 100) private String nombre;
    @NotNull private Turno turno = Turno.MATUTINO;
    @Size(max = 50) private String codigo;
    @Size(max = 100) private String aula;
    @Positive private Integer capacidad;
    private boolean activo = true;
    private Long version;

    public GrupoRequest request() {
        return new GrupoRequest(plantelId, cicloEscolarId, gradoId, nombre, turno,
                codigo, aula, capacidad, activo, version);
    }

    public static GrupoForm desde(GrupoResponse grupo, Long institucionId) {
        GrupoForm form = new GrupoForm();
        form.institucionId = institucionId;
        form.plantelId = grupo.plantelId();
        form.cicloEscolarId = grupo.cicloEscolarId();
        form.gradoId = grupo.gradoId();
        form.nombre = grupo.nombre();
        form.turno = grupo.turno();
        form.codigo = grupo.codigo();
        form.aula = grupo.aula();
        form.capacidad = grupo.capacidad();
        form.activo = grupo.activo();
        form.version = grupo.auditoria().version();
        return form;
    }
}
