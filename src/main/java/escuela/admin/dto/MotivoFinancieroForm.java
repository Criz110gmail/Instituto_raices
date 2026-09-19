package escuela.admin.dto;

import escuela.finanzas.dto.request.MotivoFinancieroRequest;
import escuela.finanzas.dto.response.MotivoFinancieroResponse;
import escuela.finanzas.entity.NaturalezaMotivoFinanciero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotivoFinancieroForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 150) private String nombre;
    @NotNull private NaturalezaMotivoFinanciero naturaleza;
    @NotBlank @Size(max = 100) private String categoria;
    private boolean activo = true;
    private Long version;

    public MotivoFinancieroRequest request() {
        return new MotivoFinancieroRequest(institucionId, codigo, nombre, naturaleza, categoria, activo, version);
    }

    public static MotivoFinancieroForm desde(MotivoFinancieroResponse respuesta) {
        MotivoFinancieroForm form = new MotivoFinancieroForm();
        form.institucionId = respuesta.institucionId(); form.codigo = respuesta.codigo();
        form.nombre = respuesta.nombre(); form.naturaleza = respuesta.naturaleza();
        form.categoria = respuesta.categoria(); form.activo = respuesta.activo();
        form.version = respuesta.auditoria().version();
        return form;
    }
}
