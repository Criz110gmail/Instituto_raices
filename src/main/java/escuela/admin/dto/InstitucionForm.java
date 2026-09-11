package escuela.admin.dto;

import escuela.institucion.dto.request.InstitucionRequest;
import escuela.institucion.dto.response.InstitucionResponse;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InstitucionForm {
    @NotBlank @Size(max=50) private String codigo;
    @NotBlank @Size(max=200) private String nombre;
    @Size(max=200) private String nombreComercial;
    @Size(max=250) private String razonSocial;
    @Size(max=13) private String rfc;
    @Email @Size(max=254) private String email;
    @Size(max=30) private String telefono;
    @Size(max=300) private String sitioWeb;
    private String domicilioFiscal;
    @Size(max=120) private String ciudad;
    @Size(max=120) private String estado;
    @Size(max=12) private String codigoPostal;
    @Pattern(regexp="[A-Z]{2}") private String pais;
    @NotBlank @Size(max=60) private String zonaHoraria = "America/Mexico_City";
    @NotBlank @Pattern(regexp="[A-Z]{3}") private String monedaPredeterminada = "MXN";
    private boolean activo = true;
    private Long version;

    public InstitucionRequest request() {
        return new InstitucionRequest(codigo,nombre,nombreComercial,razonSocial,rfc,email,telefono,
                sitioWeb,domicilioFiscal,ciudad,estado,codigoPostal,pais,zonaHoraria,
                monedaPredeterminada,activo,version);
    }

    public static InstitucionForm desde(InstitucionResponse r) {
        InstitucionForm f = new InstitucionForm();
        f.codigo=r.codigo(); f.nombre=r.nombre(); f.nombreComercial=r.nombreComercial();
        f.razonSocial=r.razonSocial(); f.rfc=r.rfc(); f.email=r.email(); f.telefono=r.telefono();
        f.sitioWeb=r.sitioWeb(); f.domicilioFiscal=r.domicilioFiscal(); f.ciudad=r.ciudad();
        f.estado=r.estado(); f.codigoPostal=r.codigoPostal(); f.pais=r.pais();
        f.zonaHoraria=r.zonaHoraria(); f.monedaPredeterminada=r.monedaPredeterminada();
        f.activo=r.activo(); f.version=r.auditoria().version();
        return f;
    }
}
