package escuela.admin.dto;

import escuela.seguridad.dto.request.UsuarioRequest;
import escuela.seguridad.dto.response.UsuarioResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max = 80) private String username;
    @NotBlank @Email @Size(max = 254) private String email;
    private Long version;

    public UsuarioRequest request() {
        return new UsuarioRequest(institucionId, username, email, version);
    }

    public static UsuarioForm desde(UsuarioResponse usuario) {
        UsuarioForm form = new UsuarioForm();
        form.institucionId = usuario.institucionId();
        form.username = usuario.username();
        form.email = usuario.email();
        form.version = usuario.auditoria().version();
        return form;
    }
}
