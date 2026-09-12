package escuela.admin.dto;

import escuela.academico.dto.request.GradoRequest;
import escuela.academico.dto.response.GradoResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradoForm {
    @NotNull private Long institucionId;
    @NotNull private Long nivelEducativoId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 150) private String nombre;
    @Positive private int orden = 1;
    private boolean activo = true;
    private Long version;

    public GradoRequest request() {
        return new GradoRequest(nivelEducativoId, codigo, nombre, orden, activo, version);
    }

    public static GradoForm desde(GradoResponse grado, Long institucionId) {
        GradoForm form = new GradoForm();
        form.institucionId = institucionId;
        form.nivelEducativoId = grado.nivelEducativoId();
        form.codigo = grado.codigo();
        form.nombre = grado.nombre();
        form.orden = grado.orden();
        form.activo = grado.activo();
        form.version = grado.auditoria().version();
        return form;
    }
}
