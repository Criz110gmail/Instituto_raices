package escuela.admin.dto;

import escuela.alumno.dto.request.AlumnoRequest;
import escuela.alumno.dto.response.AlumnoResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class AlumnoForm {
    @NotNull private Long institucionId;
    @NotBlank @Size(max = 50) private String matricula;
    @NotBlank @Size(max = 150) private String nombres;
    @NotBlank @Size(max = 100) private String primerApellido;
    @Size(max = 100) private String segundoApellido;
    @Pattern(regexp = "^$|^[A-Za-z0-9]{18}$", message = "La CURP debe contener 18 caracteres alfanuméricos") private String curp;
    @NotNull @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;
    @Size(max = 30) private String sexo;
    @Size(max = 150) private String lugarNacimiento;
    @Size(max = 80) private String nacionalidad;
    @Size(max = 30) private String telefono;
    @Email @Size(max = 254) private String email;
    @Size(max = 150) private String calle;
    @Size(max = 30) private String numeroExterior;
    @Size(max = 30) private String numeroInterior;
    @Size(max = 120) private String colonia;
    @Size(max = 100) private String ciudad;
    @Size(max = 100) private String estado;
    @Size(max = 15) private String codigoPostal;
    @Size(max = 2) private String pais = "MX";
    @NotNull @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaIngreso = LocalDate.now();
    @Size(max = 4000) private String observaciones;
    private boolean activo = true;
    private Long version;

    public AlumnoRequest request() {
        return new AlumnoRequest(institucionId, matricula, nombres, primerApellido,
                segundoApellido, curp, fechaNacimiento, sexo, lugarNacimiento,
                nacionalidad, telefono, email, calle, numeroExterior, numeroInterior,
                colonia, ciudad, estado, codigoPostal, pais, fechaIngreso,
                observaciones, activo, version);
    }

    public static AlumnoForm desde(AlumnoResponse alumno) {
        AlumnoForm form = new AlumnoForm();
        form.institucionId = alumno.institucionId();
        form.matricula = alumno.matricula();
        form.nombres = alumno.nombres();
        form.primerApellido = alumno.primerApellido();
        form.segundoApellido = alumno.segundoApellido();
        form.curp = alumno.curp();
        form.fechaNacimiento = alumno.fechaNacimiento();
        form.sexo = alumno.sexo();
        form.lugarNacimiento = alumno.lugarNacimiento();
        form.nacionalidad = alumno.nacionalidad();
        form.telefono = alumno.telefono();
        form.email = alumno.email();
        form.calle = alumno.calle();
        form.numeroExterior = alumno.numeroExterior();
        form.numeroInterior = alumno.numeroInterior();
        form.colonia = alumno.colonia();
        form.ciudad = alumno.ciudad();
        form.estado = alumno.estado();
        form.codigoPostal = alumno.codigoPostal();
        form.pais = alumno.pais();
        form.fechaIngreso = alumno.fechaIngreso();
        form.observaciones = alumno.observaciones();
        form.activo = alumno.activo();
        form.version = alumno.auditoria().version();
        return form;
    }
}
