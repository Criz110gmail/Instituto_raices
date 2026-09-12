package escuela.admin.dto;

import escuela.seguridad.dto.request.RestablecimientoPasswordRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestablecerPasswordForm {
    @NotBlank private String token;
    @NotBlank @Size(min = 12, max = 72) private String password;
    @NotBlank private String confirmarPassword;

    public RestablecimientoPasswordRequest request() {
        return new RestablecimientoPasswordRequest(token, password);
    }
}
