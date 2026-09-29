package escuela.admin.dto;

import escuela.docente.dto.PortalMaestroCuentaRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PortalMaestroCuentaForm {
    @NotBlank @Size(max=80) private String username;
    @NotBlank @Email @Size(max=254) private String email;
    public PortalMaestroCuentaRequest request(){return new PortalMaestroCuentaRequest(username,email);}
}
