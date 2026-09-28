package escuela.alumno.dto.request;

import escuela.alumno.entity.TipoSanguineo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FichaMedicaAlumnoRequest(
        @NotNull TipoSanguineo tipoSanguineo,
        @Size(max = 4000) String alergias,
        @Size(max = 4000) String padecimientos,
        @Size(max = 4000) String medicamentos,
        @Size(max = 4000) String discapacidadNecesidades,
        @Size(max = 4000) String restriccionesFisicas,
        @Size(max = 4000) String restriccionesAlimentarias,
        @Size(max = 150) String servicioMedico,
        @Size(max = 100) String numeroAfiliacion,
        @Size(max = 180) String medicoTratante,
        @Size(max = 180) String contactoEmergencia,
        @Size(max = 30) String telefonoEmergencia,
        @Size(max = 4000) String observaciones,
        boolean autorizaAtencionEmergencia,
        Long version
) {}

