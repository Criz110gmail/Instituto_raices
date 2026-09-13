package escuela.admin.dto;

import escuela.alumno.dto.request.AlumnoTutorRequest;
import escuela.alumno.dto.response.AlumnoTutorResponse;
import escuela.alumno.entity.ParentescoTutor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class AlumnoTutorForm {
    @NotNull private Long institucionId;
    @NotNull private Long alumnoId;
    @NotNull private Long tutorId;
    @NotNull private ParentescoTutor parentesco;
    @Size(max = 100) private String parentescoOtro;
    private boolean contactoPrincipal;
    private boolean responsableFinanciero;
    private boolean puedeAutorizar;
    private boolean puedeRecoger;
    private boolean puedeVerFinanzas;
    private boolean puedeRecibirNotificaciones = true;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio = LocalDate.now();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;
    @Size(max = 4000) private String observaciones;
    private boolean activo = true;
    private Long version;

    public AlumnoTutorRequest request() {
        return new AlumnoTutorRequest(alumnoId, tutorId, parentesco, parentescoOtro,
                contactoPrincipal, responsableFinanciero, puedeAutorizar, puedeRecoger,
                puedeVerFinanzas, puedeRecibirNotificaciones, fechaInicio, fechaFin,
                observaciones, activo, version);
    }

    public static AlumnoTutorForm desde(AlumnoTutorResponse vinculo) {
        AlumnoTutorForm form = new AlumnoTutorForm();
        form.institucionId = vinculo.institucionId();
        form.alumnoId = vinculo.alumnoId();
        form.tutorId = vinculo.tutorId();
        form.parentesco = vinculo.parentesco();
        form.parentescoOtro = vinculo.parentescoOtro();
        form.contactoPrincipal = vinculo.contactoPrincipal();
        form.responsableFinanciero = vinculo.responsableFinanciero();
        form.puedeAutorizar = vinculo.puedeAutorizar();
        form.puedeRecoger = vinculo.puedeRecoger();
        form.puedeVerFinanzas = vinculo.puedeVerFinanzas();
        form.puedeRecibirNotificaciones = vinculo.puedeRecibirNotificaciones();
        form.fechaInicio = vinculo.fechaInicio();
        form.fechaFin = vinculo.fechaFin();
        form.observaciones = vinculo.observaciones();
        form.activo = vinculo.activo();
        form.version = vinculo.auditoria().version();
        return form;
    }
}
