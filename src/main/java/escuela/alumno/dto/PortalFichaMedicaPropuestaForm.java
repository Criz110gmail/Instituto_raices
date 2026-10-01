package escuela.alumno.dto;

import escuela.alumno.entity.TipoSanguineo;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortalFichaMedicaPropuestaForm {
    @NotNull(message = "Selecciona el tipo sanguíneo o indica que no se conoce") private TipoSanguineo tipoSanguineo;
    @Size(max=4000) private String alergias;
    @Size(max=4000) private String padecimientos;
    @Size(max=4000) private String medicamentos;
    @Size(max=4000) private String discapacidadNecesidades;
    @Size(max=4000) private String restriccionesFisicas;
    @Size(max=4000) private String restriccionesAlimentarias;
    @Size(max=150) private String servicioMedico;
    @Size(max=100) private String numeroAfiliacion;
    @Size(max=180) private String medicoTratante;
    @Size(max=180) private String contactoEmergencia;
    @Size(max=30) private String telefonoEmergencia;
    @Size(max=4000) private String observaciones;
    private boolean autorizaAtencionEmergencia;
    @Size(max=2000) private String mensajeTutor;
    @AssertTrue(message = "Debes aceptar el consentimiento para enviar la propuesta")
    private boolean consentimiento;
}
