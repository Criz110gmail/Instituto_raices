package escuela.admin.dto;

import escuela.tutor.dto.request.PortalTutorCuentaRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortalTutorCuentaForm {
    @NotBlank
    @Size(max = 80)
    @Pattern(regexp = "[A-Za-z0-9._-]+", message = "Usa únicamente letras, números, punto, guion o guion bajo")
    private String username;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    public PortalTutorCuentaRequest request() {
        return new PortalTutorCuentaRequest(username, email);
    }
}
