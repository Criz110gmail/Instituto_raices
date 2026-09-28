package escuela.alumno.dto.response;

import escuela.alumno.entity.TipoSanguineo;

public record FichaMedicaAlumnoResponse(
        TipoSanguineo tipoSanguineo,
        String alergias,
        String padecimientos,
        String medicamentos,
        String discapacidadNecesidades,
        String restriccionesFisicas,
        String restriccionesAlimentarias,
        String servicioMedico,
        String numeroAfiliacion,
        String medicoTratante,
        String contactoEmergencia,
        String telefonoEmergencia,
        String observaciones,
        boolean autorizaAtencionEmergencia,
        Long version
) {}

