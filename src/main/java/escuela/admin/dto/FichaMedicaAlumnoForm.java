package escuela.admin.dto;

import escuela.alumno.dto.request.FichaMedicaAlumnoRequest;
import escuela.alumno.dto.response.FichaMedicaAlumnoResponse;
import escuela.alumno.entity.TipoSanguineo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FichaMedicaAlumnoForm {
    @NotNull private TipoSanguineo tipoSanguineo = TipoSanguineo.DESCONOCIDO;
    @Size(max = 4000) private String alergias;
    @Size(max = 4000) private String padecimientos;
    @Size(max = 4000) private String medicamentos;
    @Size(max = 4000) private String discapacidadNecesidades;
    @Size(max = 4000) private String restriccionesFisicas;
    @Size(max = 4000) private String restriccionesAlimentarias;
    @Size(max = 150) private String servicioMedico;
    @Size(max = 100) private String numeroAfiliacion;
    @Size(max = 180) private String medicoTratante;
    @Size(max = 180) private String contactoEmergencia;
    @Size(max = 30) private String telefonoEmergencia;
    @Size(max = 4000) private String observaciones;
    private boolean autorizaAtencionEmergencia;
    private Long version;

    public FichaMedicaAlumnoRequest request() {
        return new FichaMedicaAlumnoRequest(tipoSanguineo, alergias, padecimientos,
                medicamentos, discapacidadNecesidades, restriccionesFisicas,
                restriccionesAlimentarias, servicioMedico, numeroAfiliacion,
                medicoTratante, contactoEmergencia, telefonoEmergencia, observaciones,
                autorizaAtencionEmergencia, version);
    }

    public static FichaMedicaAlumnoForm desde(FichaMedicaAlumnoResponse ficha) {
        FichaMedicaAlumnoForm form = new FichaMedicaAlumnoForm();
        if (ficha == null) return form;
        form.tipoSanguineo = ficha.tipoSanguineo();
        form.alergias = ficha.alergias();
        form.padecimientos = ficha.padecimientos();
        form.medicamentos = ficha.medicamentos();
        form.discapacidadNecesidades = ficha.discapacidadNecesidades();
        form.restriccionesFisicas = ficha.restriccionesFisicas();
        form.restriccionesAlimentarias = ficha.restriccionesAlimentarias();
        form.servicioMedico = ficha.servicioMedico();
        form.numeroAfiliacion = ficha.numeroAfiliacion();
        form.medicoTratante = ficha.medicoTratante();
        form.contactoEmergencia = ficha.contactoEmergencia();
        form.telefonoEmergencia = ficha.telefonoEmergencia();
        form.observaciones = ficha.observaciones();
        form.autorizaAtencionEmergencia = ficha.autorizaAtencionEmergencia();
        form.version = ficha.version();
        return form;
    }
}

