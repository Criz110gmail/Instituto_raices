package escuela.compras.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProveedorForm {
    @NotNull(message="Selecciona la institución") private Long institucionId;
    @NotBlank(message="Escribe la razón social") @Size(max=180) private String razonSocial;
    @Size(max=180) private String nombreComercial;
    @Pattern(regexp="^$|^[A-Za-zÑñ&]{3,4}[0-9]{6}[A-Za-z0-9]{3}$",message="El RFC debe tener 12 o 13 caracteres válidos") private String rfc;
    @Size(max=180) private String contactoNombre;
    @Size(max=30) private String telefono;
    @Email(message="Escribe un correo válido") @Size(max=254) private String correo;
    @Size(max=500) private String direccion;
    @Size(max=2000) private String notas;
    private boolean activo=true;
    private Long version;
}
