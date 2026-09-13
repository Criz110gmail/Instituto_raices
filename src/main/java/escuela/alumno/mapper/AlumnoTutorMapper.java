package escuela.alumno.mapper;

import escuela.alumno.dto.request.AlumnoTutorRequest;
import escuela.alumno.dto.response.AlumnoTutorResponse;
import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.AlumnoTutor;
import escuela.alumno.entity.ParentescoTutor;
import escuela.tutor.entity.Tutor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class AlumnoTutorMapper {

    public AlumnoTutor nuevo(AlumnoTutorRequest dto, Alumno alumno, Tutor tutor) {
        AlumnoTutor entidad = new AlumnoTutor();
        entidad.setAlumno(alumno);
        entidad.setTutor(tutor);
        actualizar(entidad, dto);
        return entidad;
    }

    public void actualizar(AlumnoTutor entidad, AlumnoTutorRequest dto) {
        entidad.setParentesco(dto.parentesco());
        entidad.setParentescoOtro(dto.parentesco() == ParentescoTutor.OTRO
                ? limpiar(dto.parentescoOtro()) : null);
        entidad.setContactoPrincipal(dto.contactoPrincipal());
        entidad.setResponsableFinanciero(dto.responsableFinanciero());
        entidad.setPuedeAutorizar(dto.puedeAutorizar());
        entidad.setPuedeRecoger(dto.puedeRecoger());
        entidad.setPuedeVerFinanzas(dto.puedeVerFinanzas());
        entidad.setPuedeRecibirNotificaciones(dto.puedeRecibirNotificaciones());
        entidad.setFechaInicio(dto.fechaInicio());
        entidad.setFechaFin(dto.fechaFin());
        entidad.setObservaciones(limpiar(dto.observaciones()));
        entidad.setActivo(dto.activo());
    }

    public AlumnoTutorResponse respuesta(AlumnoTutor entidad) {
        Alumno alumno = entidad.getAlumno();
        Tutor tutor = entidad.getTutor();
        return new AlumnoTutorResponse(entidad.getId(), alumno.getInstitucion().getId(),
                alumno.getId(), alumno.getMatricula(), nombre(alumno.getNombres(),
                alumno.getPrimerApellido(), alumno.getSegundoApellido()), tutor.getId(),
                nombre(tutor.getNombres(), tutor.getPrimerApellido(), tutor.getSegundoApellido()),
                entidad.getParentesco(), entidad.getParentescoOtro(),
                entidad.isContactoPrincipal(), entidad.isResponsableFinanciero(),
                entidad.isPuedeAutorizar(), entidad.isPuedeRecoger(),
                entidad.isPuedeVerFinanzas(), entidad.isPuedeRecibirNotificaciones(),
                entidad.getFechaInicio(), entidad.getFechaFin(), entidad.getObservaciones(),
                entidad.isActivo(), desde(entidad));
    }

    private String nombre(String... partes) {
        return Stream.of(partes).filter(valor -> valor != null && !valor.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
