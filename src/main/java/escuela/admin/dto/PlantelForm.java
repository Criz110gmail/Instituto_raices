package escuela.admin.dto;

import escuela.institucion.dto.request.PlantelRequest;
import escuela.institucion.dto.response.PlantelResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlantelForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 200) private String nombre;
    @Size(max = 30) private String telefono;
    @Email @Size(max = 254) private String email;
    @Size(max = 200) private String calle;
    @Size(max = 30) private String numeroExterior;
    @Size(max = 30) private String numeroInterior;
    @Size(max = 150) private String colonia;
    @Size(max = 120) private String ciudad;
    @Size(max = 120) private String estado;
    @Size(max = 12) private String codigoPostal;
    @Pattern(regexp = "[A-Z]{2}") private String pais = "MX";
    private boolean activo = true;
    private Long version;

    public PlantelRequest request() {
        return new PlantelRequest(institucionId, codigo, nombre, telefono, email, calle,
                numeroExterior, numeroInterior, colonia, ciudad, estado, codigoPostal,
                pais, activo, version);
    }

    public static PlantelForm desde(PlantelResponse r) {
        PlantelForm f = new PlantelForm();
        f.institucionId = r.institucionId();
        f.codigo = r.codigo();
        f.nombre = r.nombre();
        f.telefono = r.telefono();
        f.email = r.email();
        f.calle = r.calle();
        f.numeroExterior = r.numeroExterior();
        f.numeroInterior = r.numeroInterior();
        f.colonia = r.colonia();
        f.ciudad = r.ciudad();
        f.estado = r.estado();
        f.codigoPostal = r.codigoPostal();
        f.pais = r.pais();
        f.activo = r.activo();
        f.version = r.auditoria().version();
        return f;
    }
}
