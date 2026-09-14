package escuela.admin.service;

import escuela.admin.dto.OpcionAutocompletado;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoRepository;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusquedaAutocompletadoService {

    private static final int MINIMO_CARACTERES = 3;
    private static final int MAXIMO_RESULTADOS = 20;
    private final AlumnoRepository alumnoRepository;
    private final TutorRepository tutorRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ConceptoCobroRepository conceptoCobroRepository;
    private final AlcanceDatosService alcance;

    public ResultadoAutocompletado alumnos(Long institucionId, String consulta) {
        alcance.validarAdministracionInstitucional(institucionId);
        return buscarAlumnos(institucionId, consulta);
    }

    public ResultadoAutocompletado alumnosParaInscripcion(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        return buscarAlumnos(institucionId, consulta);
    }

    private ResultadoAutocompletado buscarAlumnos(Long institucionId, String consulta) {
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<Alumno> resultado = alumnoRepository.buscarParaAutocompletado(
                institucionId, texto, PageRequest.of(0, MAXIMO_RESULTADOS));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(alumno -> new OpcionAutocompletado(alumno.getId(),
                        alumno.getMatricula() + " · " + nombre(alumno.getNombres(),
                                alumno.getPrimerApellido(), alumno.getSegundoApellido()),
                        detalle(alumno.getCurp(), alumno.getEmail())))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado tutores(Long institucionId, String consulta) {
        alcance.validarAdministracionInstitucional(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<Tutor> resultado = tutorRepository.buscarParaAutocompletado(
                institucionId, texto, PageRequest.of(0, MAXIMO_RESULTADOS));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(tutor -> new OpcionAutocompletado(tutor.getId(),
                        nombre(tutor.getNombres(), tutor.getPrimerApellido(),
                                tutor.getSegundoApellido()),
                        detalle(tutor.getTelefonoPrincipal(), tutor.getEmail())))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado usuarios(Long institucionId, String consulta, Long tutorId) {
        alcance.validarAdministracionInstitucional(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<Usuario> resultado = usuarioRepository.buscarParaAutocompletado(
                institucionId, texto, tutorId, PageRequest.of(0, MAXIMO_RESULTADOS));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(usuario -> new OpcionAutocompletado(usuario.getId(),
                        usuario.getUsername(), usuario.getEmail()))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado inscripciones(Long plantelId, String consulta) {
        alcance.validarPlantel(plantelId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<Inscripcion> resultado = inscripcionRepository.buscarParaAutocompletado(
                plantelId, texto, PageRequest.of(0, MAXIMO_RESULTADOS));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(inscripcion -> new OpcionAutocompletado(inscripcion.getId(),
                        inscripcion.getNumeroInscripcion() + " · "
                                + nombre(inscripcion.getAlumno().getNombres(),
                                inscripcion.getAlumno().getPrimerApellido(),
                                inscripcion.getAlumno().getSegundoApellido()),
                        inscripcion.getAlumno().getMatricula() + " · "
                                + inscripcion.getGrado().getNombre()))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado conceptosCobro(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<ConceptoCobro> resultado = conceptoCobroRepository.buscarParaAutocompletado(
                institucionId, texto, PageRequest.of(0, MAXIMO_RESULTADOS));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(concepto -> new OpcionAutocompletado(concepto.getId(),
                        concepto.getCodigo() + " · " + concepto.getNombre(),
                        concepto.getCategoria().name()))
                .toList(), resultado.hasNext());
    }

    private String normalizar(String consulta) {
        if (consulta == null) return null;
        String texto = consulta.trim().replaceAll("\\s+", " ");
        if (texto.length() < MINIMO_CARACTERES) return null;
        return texto.substring(0, Math.min(texto.length(), 100)).toLowerCase(Locale.ROOT);
    }

    private String nombre(String... partes) {
        return Stream.of(partes).filter(valor -> valor != null && !valor.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    private String detalle(String primero, String segundo) {
        String valor = Stream.of(primero, segundo)
                .filter(item -> item != null && !item.isBlank())
                .collect(java.util.stream.Collectors.joining(" · "));
        return valor.isBlank() ? "Sin datos adicionales" : valor;
    }
}
