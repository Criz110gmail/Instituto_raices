package escuela.inscripcion.mapper;

import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.Grado;
import escuela.alumno.entity.Alumno;
import escuela.inscripcion.dto.request.InscripcionRequest;
import escuela.inscripcion.dto.response.InscripcionResponse;
import escuela.inscripcion.entity.Inscripcion;
import escuela.institucion.entity.Plantel;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class InscripcionMapper {

    public Inscripcion nueva(InscripcionRequest dto, Alumno alumno, Plantel plantel,
                             CicloEscolar ciclo, Grado grado, Inscripcion anterior) {
        Inscripcion entidad = new Inscripcion();
        entidad.setAlumno(alumno);
        entidad.setPlantel(plantel);
        entidad.setCicloEscolar(ciclo);
        entidad.setGrado(grado);
        entidad.setInscripcionAnterior(anterior);
        actualizar(entidad, dto);
        return entidad;
    }

    public void actualizar(Inscripcion entidad, InscripcionRequest dto) {
        entidad.setNumeroInscripcion(codigo(dto.numeroInscripcion()));
        entidad.setFechaInscripcion(dto.fechaInscripcion());
        entidad.setFechaInicio(dto.fechaInicio());
        entidad.setFechaFin(dto.fechaFin());
        entidad.setEstado(dto.estado());
        entidad.setMotivoBajaCancelacion(limpiar(dto.motivoBajaCancelacion()));
        entidad.setObservaciones(limpiar(dto.observaciones()));
    }

    public InscripcionResponse respuesta(Inscripcion e) {
        Alumno alumno = e.getAlumno();
        return new InscripcionResponse(e.getId(), alumno.getInstitucion().getId(), alumno.getId(),
                alumno.getMatricula(), nombre(alumno.getNombres(), alumno.getPrimerApellido(),
                alumno.getSegundoApellido()), e.getPlantel().getId(), e.getPlantel().getNombre(),
                e.getCicloEscolar().getId(), e.getCicloEscolar().getNombre(), e.getGrado().getId(),
                e.getGrado().getNombre(), e.getNumeroInscripcion(), e.getFechaInscripcion(),
                e.getFechaInicio(), e.getFechaFin(), e.getEstado(), e.getMotivoBajaCancelacion(),
                e.getInscripcionAnterior() == null ? null : e.getInscripcionAnterior().getId(),
                e.getObservaciones(), desde(e));
    }

    private String nombre(String... partes) {
        return Stream.of(partes).filter(valor -> valor != null && !valor.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
