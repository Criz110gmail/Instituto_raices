package escuela.alumno.service.impl;

import escuela.alumno.dto.request.AlumnoRequest;
import escuela.alumno.dto.response.AlumnoResponse;
import escuela.alumno.entity.Alumno;
import escuela.alumno.mapper.AlumnoMapper;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.service.AlumnoService;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository repository;
    private final InstitucionRepository institucionRepository;
    private final AlumnoMapper mapper;

    @Override
    public AlumnoResponse crear(AlumnoRequest request) {
        Institucion institucion = institucionActiva(request.institucionId());
        validarFechas(request);
        validarUnicos(request, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, institucion)));
    }

    @Override
    public AlumnoResponse actualizar(Long id, AlumnoRequest request) {
        Alumno entidad = buscar(id);
        verificar(entidad, request.version(), "Alumno");
        if (!entidad.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución de un alumno");
        }
        validarFechas(request);
        validarUnicos(request, id);
        mapper.actualizar(entidad, request, entidad.getInstitucion());
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    public void desactivar(Long id, Long version) {
        Alumno entidad = buscar(id);
        verificar(entidad, version, "Alumno");
        entidad.setActivo(false);
    }

    private Alumno buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", id));
    }

    private Institucion institucionActiva(Long id) {
        Institucion institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("La institución debe estar activa para registrar alumnos");
        }
        return institucion;
    }

    private void validarFechas(AlumnoRequest request) {
        if (request.fechaNacimiento() == null || request.fechaIngreso() == null) return;
        if (request.fechaNacimiento().isAfter(LocalDate.now())
                || request.fechaIngreso().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("Las fechas de nacimiento e ingreso no pueden ser futuras");
        }
        if (request.fechaIngreso().isBefore(request.fechaNacimiento())) {
            throw new ReglaNegocioException("La fecha de ingreso no puede ser anterior al nacimiento");
        }
    }

    private void validarUnicos(AlumnoRequest request, Long idExcluido) {
        if (repository.existsByInstitucionIdAndMatriculaIgnoreCaseAndIdNot(
                request.institucionId(), codigo(request.matricula()), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un alumno con esa matrícula en la institución");
        }
        String curp = codigo(request.curp());
        if (curp != null && repository.existsByInstitucionIdAndCurpIgnoreCaseAndIdNot(
                request.institucionId(), curp, idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un alumno con esa CURP en la institución");
        }
    }
}
