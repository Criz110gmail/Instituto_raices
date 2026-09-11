package escuela.admin.dto;

import escuela.academico.dto.request.NivelEducativoRequest;
import escuela.academico.dto.response.NivelEducativoResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NivelEducativoForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 150) private String nombre;
    private String descripcion;
    @Positive private int orden = 1;
    private boolean activo = true;
    private Long version;

    public NivelEducativoRequest request() {
        return new NivelEducativoRequest(institucionId, codigo, nombre, descripcion, orden,
                activo, version);
    }

    public static NivelEducativoForm desde(NivelEducativoResponse r) {
        NivelEducativoForm f = new NivelEducativoForm();
        f.institucionId = r.institucionId();
        f.codigo = r.codigo();
        f.nombre = r.nombre();
        f.descripcion = r.descripcion();
        f.orden = r.orden();
        f.activo = r.activo();
        f.version = r.auditoria().version();
        return f;
    }
}
