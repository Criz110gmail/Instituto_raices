package escuela.docente.dto;
import escuela.alumno.dto.response.FichaMedicaAlumnoResponse;
import java.time.LocalDate;
public record AlumnoMaestroDetalle(Long alumnoId,String matricula,String alumno,LocalDate fechaNacimiento,String sexo,
 String telefono,String email,String plantel,String grado,String grupo,boolean fotografia,FichaMedicaAlumnoResponse fichaMedica) { }
