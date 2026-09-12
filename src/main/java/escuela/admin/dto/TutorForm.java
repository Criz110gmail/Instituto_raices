package escuela.admin.dto;

import escuela.tutor.dto.request.TutorRequest;
import escuela.tutor.dto.response.TutorResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class TutorForm {
    @NotNull private Long institucionId;
    private Long usuarioId;
    @NotBlank @Size(max = 150) private String nombres;
    @NotBlank @Size(max = 100) private String primerApellido;
    @Size(max = 100) private String segundoApellido;
    @NotBlank @Size(max = 30) private String telefonoPrincipal;
    @Size(max = 30) private String telefonoSecundario;
    @Email @Size(max = 254) private String email;
    @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;
    @Size(max = 150) private String calle;
    @Size(max = 30) private String numeroExterior;
    @Size(max = 30) private String numeroInterior;
    @Size(max = 120) private String colonia;
    @Size(max = 100) private String ciudad;
    @Size(max = 100) private String estado;
    @Size(max = 15) private String codigoPostal;
    @Size(max = 2) private String pais = "MX";
    @Size(max = 120) private String ocupacion;
    @Size(max = 180) private String lugarTrabajo;
    @Size(max = 30) private String telefonoTrabajo;
    private boolean activo = true;
    private Long version;

    public TutorRequest request() {
        return new TutorRequest(institucionId, usuarioId, nombres, primerApellido,
                segundoApellido, telefonoPrincipal, telefonoSecundario, email,
                fechaNacimiento, calle, numeroExterior, numeroInterior, colonia,
                ciudad, estado, codigoPostal, pais, ocupacion, lugarTrabajo,
                telefonoTrabajo, activo, version);
    }

    public static TutorForm desde(TutorResponse tutor) {
        TutorForm form = new TutorForm();
        form.institucionId = tutor.institucionId();
        form.usuarioId = tutor.usuarioId();
        form.nombres = tutor.nombres();
        form.primerApellido = tutor.primerApellido();
        form.segundoApellido = tutor.segundoApellido();
        form.telefonoPrincipal = tutor.telefonoPrincipal();
        form.telefonoSecundario = tutor.telefonoSecundario();
        form.email = tutor.email();
        form.fechaNacimiento = tutor.fechaNacimiento();
        form.calle = tutor.calle();
        form.numeroExterior = tutor.numeroExterior();
        form.numeroInterior = tutor.numeroInterior();
        form.colonia = tutor.colonia();
        form.ciudad = tutor.ciudad();
        form.estado = tutor.estado();
        form.codigoPostal = tutor.codigoPostal();
        form.pais = tutor.pais();
        form.ocupacion = tutor.ocupacion();
        form.lugarTrabajo = tutor.lugarTrabajo();
        form.telefonoTrabajo = tutor.telefonoTrabajo();
        form.activo = tutor.activo();
        form.version = tutor.auditoria().version();
        return form;
    }
}
