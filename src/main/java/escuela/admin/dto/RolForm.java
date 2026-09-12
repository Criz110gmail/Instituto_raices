package escuela.admin.dto;

import escuela.seguridad.dto.request.RolRequest;
import escuela.seguridad.dto.response.RolResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class RolForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 150) private String nombre;
    @Size(max = 2000) private String descripcion;
    private boolean activo = true;
    private Set<Long> permisoIds = new LinkedHashSet<>();
    private Long version;

    public RolRequest request() {
        return new RolRequest(institucionId, codigo, nombre, descripcion, activo, version);
    }

    public static RolForm desde(RolResponse rol, Set<Long> permisos) {
        RolForm form = new RolForm();
        form.institucionId = rol.institucionId();
        form.codigo = rol.codigo();
        form.nombre = rol.nombre();
        form.descripcion = rol.descripcion();
        form.activo = rol.activo();
        form.permisoIds = new LinkedHashSet<>(permisos);
        form.version = rol.auditoria().version();
        return form;
    }
}
