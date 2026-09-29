package escuela.admin.dto;

import escuela.docente.dto.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MaestroForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max=50) private String numeroEmpleado;
    @NotBlank @Size(max=150) private String nombres;
    @NotBlank @Size(max=100) private String primerApellido;
    @Size(max=100) private String segundoApellido;
    @NotBlank @Email @Size(max=254) private String email;
    @Size(max=30) private String telefono;
    private Long version;
    public MaestroRequest request(){return new MaestroRequest(institucionId,numeroEmpleado,nombres,primerApellido,segundoApellido,email,telefono,version);}
    public static MaestroForm desde(MaestroResponse r){var f=new MaestroForm();f.institucionId=r.institucionId();f.numeroEmpleado=r.numeroEmpleado();f.nombres=r.nombres();f.primerApellido=r.primerApellido();f.segundoApellido=r.segundoApellido();f.email=r.email();f.telefono=r.telefono();f.version=r.version();return f;}
}
