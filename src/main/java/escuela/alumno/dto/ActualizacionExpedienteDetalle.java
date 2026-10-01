package escuela.alumno.dto;

import escuela.alumno.entity.*;
import java.time.*;

public record ActualizacionExpedienteDetalle(
        Long id, Long alumnoId, String alumno, String matricula, String tutor,
        TipoActualizacionExpediente tipo, EstadoActualizacionExpediente estado,
        String mensajeTutor, String consentimientoTexto, Instant consentimientoEn,
        TipoDocumentoAlumno tipoDocumento, String descripcionDocumento,
        LocalDate fechaDocumento, LocalDate vigenteHasta, String nombreArchivo,
        TipoSanguineo tipoSanguineo, String alergias, String padecimientos,
        String medicamentos, String discapacidadNecesidades, String restriccionesFisicas,
        String restriccionesAlimentarias, String servicioMedico, String numeroAfiliacion,
        String medicoTratante, String contactoEmergencia, String telefonoEmergencia,
        String observacionesMedicas, Boolean autorizaAtencionEmergencia,
        String respuestaAdmin, Instant revisadoEn, String revisadoPor, Long version) {
}
