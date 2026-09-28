package escuela.admin.dto;

import escuela.academico.dto.request.MateriaRequest;
import escuela.academico.dto.response.MateriaResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MateriaForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 150) private String nombre;
    @Size(max = 4000) private String descripcion;
    private boolean activo = true;
    private Long version;

    public MateriaRequest request() {
        return new MateriaRequest(institucionId, codigo, nombre, descripcion, activo, version);
    }

    public static MateriaForm desde(MateriaResponse materia) {
        MateriaForm form = new MateriaForm();
        form.institucionId = materia.institucionId();
        form.codigo = materia.codigo();
        form.nombre = materia.nombre();
        form.descripcion = materia.descripcion();
        form.activo = materia.activo();
        form.version = materia.auditoria().version();
        return form;
    }
}

