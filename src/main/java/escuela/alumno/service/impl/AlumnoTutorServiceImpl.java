package escuela.alumno.service.impl;

import escuela.alumno.dto.request.AlumnoTutorRequest;
import escuela.alumno.dto.response.AlumnoTutorResponse;
import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.AlumnoTutor;
import escuela.alumno.entity.ParentescoTutor;
import escuela.alumno.mapper.AlumnoTutorMapper;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.alumno.service.AlumnoTutorService;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class AlumnoTutorServiceImpl implements AlumnoTutorService {

    private final AlumnoTutorRepository repository;
    private final AlumnoRepository alumnoRepository;
    private final TutorRepository tutorRepository;
    private final AlumnoTutorMapper mapper;

    @Override
    public AlumnoTutorResponse crear(AlumnoTutorRequest request) {
        Alumno alumno = alumnoParaActualizar(request.alumnoId());
        Tutor tutor = tutor(request.tutorId());
        validarPropietarios(alumno, tutor, request.activo());
        validarReglas(request, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, alumno, tutor)));
    }

    @Override
    public AlumnoTutorResponse actualizar(Long id, AlumnoTutorRequest request) {
        AlumnoTutor entidad = buscar(id);
        verificar(entidad, request.version(), "Vínculo alumno-tutor");
        if (!entidad.getAlumno().getId().equals(request.alumnoId())
                || !entidad.getTutor().getId().equals(request.tutorId())) {
            throw new ReglaNegocioException("No se puede cambiar el alumno ni el tutor de un vínculo histórico");
        }
        Alumno alumno = alumnoParaActualizar(request.alumnoId());
        validarPropietarios(alumno, entidad.getTutor(), request.activo());
        validarReglas(request, id);
        mapper.actualizar(entidad, request);
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoTutorResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    public void desactivar(Long id, Long version) {
        AlumnoTutor entidad = buscar(id);
        verificar(entidad, version, "Vínculo alumno-tutor");
        entidad.setActivo(false);
    }

    private AlumnoTutor buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el vínculo alumno-tutor", id));
    }

    private Alumno alumnoParaActualizar(Long id) {
        return alumnoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", id));
    }

    private Tutor tutor(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el tutor", id));
    }

    private void validarPropietarios(Alumno alumno, Tutor tutor, boolean vinculoActivo) {
        if (!alumno.getInstitucion().getId().equals(tutor.getInstitucion().getId())) {
            throw new ReglaNegocioException("El alumno y el tutor deben pertenecer a la misma institución");
        }
        if (vinculoActivo && (!alumno.isActivo() || !tutor.isActivo())) {
            throw new ReglaNegocioException("El alumno y el tutor deben estar activos para mantener un vínculo vigente");
        }
    }

    private void validarReglas(AlumnoTutorRequest request, Long idExcluido) {
        if (request.fechaFin() != null && request.fechaFin().isBefore(request.fechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        String otro = limpiar(request.parentescoOtro());
        if (request.parentesco() == ParentescoTutor.OTRO && otro == null) {
            throw new ReglaNegocioException("Especifica el parentesco cuando seleccionas Otro");
        }
        if (request.parentesco() != ParentescoTutor.OTRO && otro != null) {
            throw new ReglaNegocioException("El parentesco personalizado sólo se utiliza con la opción Otro");
        }
        if (!request.activo()) return;
        List<AlumnoTutor> pareja = repository
                .findAllByAlumnoIdAndTutorIdAndActivoTrueAndIdNot(
                        request.alumnoId(), request.tutorId(), idExcluido);
        if (pareja.stream().anyMatch(vinculo -> seSuperponen(request.fechaInicio(),
                request.fechaFin(), vinculo.getFechaInicio(), vinculo.getFechaFin()))) {
            throw new RecursoDuplicadoException("Ya existe un vínculo activo entre el alumno y el tutor durante esas fechas");
        }
        if (request.contactoPrincipal()) {
            List<AlumnoTutor> principales = repository
                    .findAllByAlumnoIdAndContactoPrincipalTrueAndActivoTrueAndIdNot(
                            request.alumnoId(), idExcluido);
            if (principales.stream().anyMatch(vinculo -> seSuperponen(request.fechaInicio(),
                    request.fechaFin(), vinculo.getFechaInicio(), vinculo.getFechaFin()))) {
                throw new RecursoDuplicadoException("El alumno ya tiene un contacto principal durante esas fechas");
            }
        }
    }

    private boolean seSuperponen(LocalDate inicioA, LocalDate finA,
                                 LocalDate inicioB, LocalDate finB) {
        return (finA == null || !finA.isBefore(inicioB))
                && (finB == null || !finB.isBefore(inicioA));
    }
}
