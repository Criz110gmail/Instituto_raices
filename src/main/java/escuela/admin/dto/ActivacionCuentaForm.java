package escuela.admin.dto;

import escuela.seguridad.dto.request.ActivacionUsuarioRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivacionCuentaForm {
    @NotBlank private String token;
    @NotBlank @Size(min = 12, max = 72) private String password;
    @NotBlank private String confirmarPassword;

    public ActivacionUsuarioRequest request() {
        return new ActivacionUsuarioRequest(token, password);
    }
}
