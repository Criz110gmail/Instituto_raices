package escuela.admin.dto;
import escuela.cobranza.dto.request.TipoBecaRequest;
import escuela.cobranza.dto.response.TipoBecaResponse;
import jakarta.validation.constraints.*;
import lombok.Getter;import lombok.Setter;
@Getter @Setter public class TipoBecaForm {
 @NotNull private Long institucionId; @NotBlank @Size(max=50) private String codigo;
 @NotBlank @Size(max=150) private String nombre; @Size(max=2000) private String descripcion;
 private boolean activo=true; private Long version;
 public TipoBecaRequest request(){return new TipoBecaRequest(institucionId,codigo,nombre,descripcion,activo,version);}
 public static TipoBecaForm desde(TipoBecaResponse r){TipoBecaForm f=new TipoBecaForm();f.institucionId=r.institucionId();f.codigo=r.codigo();f.nombre=r.nombre();f.descripcion=r.descripcion();f.activo=r.activo();f.version=r.auditoria().version();return f;}
}
