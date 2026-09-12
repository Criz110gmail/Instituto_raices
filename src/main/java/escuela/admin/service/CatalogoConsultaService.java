package escuela.admin.service;

import escuela.academico.entity.*;
import escuela.academico.repository.*;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoRepository;
import escuela.admin.dto.*;
import escuela.institucion.entity.*;
import escuela.institucion.repository.*;
import escuela.seguridad.repository.RolRepository;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoConsultaService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("es", "MX"));
    private final InstitucionRepository institucionRepository;
    private final PlantelRepository plantelRepository;
    private final NivelEducativoRepository nivelRepository;
    private final PlantelNivelRepository ofertaRepository;
    private final GradoRepository gradoRepository;
    private final CicloEscolarRepository cicloRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final GrupoRepository grupoRepository;
    private final AlumnoRepository alumnoRepository;
    private final TutorRepository tutorRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlcanceDatosService alcanceDatosService;

    public ResultadoCatalogo consultar(ModuloCatalogo modulo, FiltroCatalogo filtroOriginal) {
        FiltroCatalogo f = filtroOriginal.normalizado();
        Pageable pagina = PageRequest.of(f.pagina(), f.tamanio(), Sort.by("id").descending());
        Page<FilaCatalogo> resultado = switch (modulo) {
            case INSTITUCIONES -> consultar(modulo, institucionRepository, texto(f, "codigo", "nombre"), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getCodigo(), e.getNombre(), e.getZonaHoraria(), e.getMonedaPredeterminada()));
            case PLANTELES -> consultar(modulo, plantelRepository, texto(f, "codigo", "nombre", "ciudad"), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getCodigo(), e.getNombre(), e.getInstitucion().getNombre(), valor(e.getCiudad())));
            case NIVELES -> consultar(modulo, nivelRepository, texto(f, "codigo", "nombre"), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getCodigo(), e.getNombre(), e.getInstitucion().getNombre(), String.valueOf(e.getOrden())));
            case OFERTA -> consultar(modulo, ofertaRepository, textoRelacionesOferta(f), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getPlantel().getNombre(), e.getNivelEducativo().getNombre(), valor(e.getClaveCentroTrabajo())));
            case GRADOS -> consultar(modulo, gradoRepository, texto(f, "codigo", "nombre"), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getCodigo(), e.getNombre(), e.getNivelEducativo().getNombre(), String.valueOf(e.getOrden())));
            case CICLOS -> consultar(modulo, cicloRepository, texto(f, "codigo", "nombre"), estado(f, "estado"), pagina,
                    e -> filaEstado(e.getId(), e.getEstado().name(), e.getCodigo(), e.getNombre(), FECHA.format(e.getFechaInicio()), FECHA.format(e.getFechaFin()), e.isPredeterminado() ? "Sí" : "No"));
            case PERIODOS -> consultar(modulo, periodoRepository, texto(f, "codigo", "nombre"), estado(f, "estado"), pagina,
                    e -> filaEstado(e.getId(), e.getEstado().name(), e.getCodigo(), e.getNombre(), e.getNivelEducativo().getNombre(), e.getTipo().name(), FECHA.format(e.getFechaInicio()) + " — " + FECHA.format(e.getFechaFin())));
            case GRUPOS -> consultar(modulo, grupoRepository, texto(f, "nombre", "codigo", "aula"), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getNombre(), valor(e.getCodigo()), e.getPlantel().getNombre(), e.getGrado().getNombre(), e.getTurno().name(), e.getCapacidad() == null ? "Sin límite" : e.getCapacidad().toString()));
            case ALUMNOS -> consultar(modulo, alumnoRepository,
                    texto(f, "matricula", "nombres", "primerApellido", "segundoApellido", "curp", "email"),
                    activo(f), pagina, e -> fila(e.getId(), e.isActivo(), e.getMatricula(),
                            nombreAlumno(e), valor(e.getCurp()), FECHA.format(e.getFechaNacimiento()),
                            FECHA.format(e.getFechaIngreso())));
            case TUTORES -> consultar(modulo, tutorRepository,
                    texto(f, "nombres", "primerApellido", "segundoApellido", "telefonoPrincipal", "email"),
                    activo(f), pagina, e -> fila(e.getId(), e.isActivo(), nombreTutor(e),
                            e.getTelefonoPrincipal(), valor(e.getEmail()),
                            e.getUsuario() == null ? "Sin cuenta" : e.getUsuario().getUsername(),
                            e.getInstitucion().getNombre()));
            case ROLES -> consultar(modulo, rolRepository, texto(f, "codigo", "nombre", "descripcion"), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getCodigo(), e.getNombre(), e.getInstitucion().getNombre(), valor(e.getDescripcion())));
            case USUARIOS -> consultar(modulo, usuarioRepository, textoUsuario(f), estado(f, "estado"), pagina,
                    e -> filaEstadoUsuario(e, e.getUsername(), e.getEmail(), e.getInstitucion().getNombre(),
                            e.getPasswordHash() == null ? "Pendiente" : "Configurada"));
        };
        return new ResultadoCatalogo(modulo, modulo.columnas(), resultado);
    }

    private <T> Page<FilaCatalogo> consultar(ModuloCatalogo modulo, JpaSpecificationExecutor<T> repo, Specification<T> texto,
                                              Specification<T> estado, Pageable pagina,
                                              Function<T, FilaCatalogo> mapper) {
        return repo.findAll(Specification.where(texto).and(estado)
                .and(alcanceDatosService.especificacion(modulo)), pagina).map(mapper);
    }

    private <T> Specification<T> texto(FiltroCatalogo f, String... campos) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            return cb.or(java.util.Arrays.stream(campos)
                    .map(c -> cb.like(cb.lower(root.get(c).as(String.class)), patron)).toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    private Specification<PlantelNivel> textoRelacionesOferta(FiltroCatalogo f) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            return cb.or(cb.like(cb.lower(root.get("plantel").get("nombre")), patron),
                    cb.like(cb.lower(root.get("nivelEducativo").get("nombre")), patron),
                    cb.like(cb.lower(root.get("claveCentroTrabajo")), patron));
        };
    }

    private Specification<Usuario> textoUsuario(FiltroCatalogo f) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            return cb.or(cb.like(cb.lower(root.get("username")), patron),
                    cb.like(cb.lower(root.get("email")), patron),
                    cb.like(cb.lower(root.get("institucion").get("nombre")), patron));
        };
    }

    private <T> Specification<T> activo(FiltroCatalogo f) {
        return (root, query, cb) -> switch (f.estado()) {
            case "ACTIVO" -> cb.isTrue(root.get("activo"));
            case "INACTIVO" -> cb.isFalse(root.get("activo"));
            default -> cb.conjunction();
        };
    }

    private <T> Specification<T> estado(FiltroCatalogo f, String campo) {
        return (root, query, cb) -> f.estado().equals("TODOS") ? cb.conjunction()
                : cb.equal(root.get(campo).as(String.class), f.estado());
    }

    private FilaCatalogo fila(Long id, boolean activo, String... celdas) {
        return new FilaCatalogo(id, List.of(celdas), activo ? "Activo" : "Inactivo", activo ? "positivo" : "neutro");
    }

    private FilaCatalogo filaEstado(Long id, String estado, String... celdas) {
        return new FilaCatalogo(id, List.of(celdas), estado, estado.equals("ABIERTO") ? "positivo" : estado.equals("CERRADO") ? "neutro" : "aviso");
    }

    private FilaCatalogo filaEstadoUsuario(Usuario usuario, String... celdas) {
        String estado = usuario.getEstado().name();
        String tono = estado.equals("ACTIVO") ? "positivo"
                : estado.equals("INVITADO") ? "aviso" : "neutro";
        return new FilaCatalogo(usuario.getId(), List.of(celdas), estado, tono);
    }

    private String valor(String valor) { return valor == null || valor.isBlank() ? "—" : valor; }

    private String nombreAlumno(Alumno alumno) {
        return java.util.stream.Stream.of(alumno.getNombres(), alumno.getPrimerApellido(),
                        alumno.getSegundoApellido())
                .filter(valor -> valor != null && !valor.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    private String nombreTutor(Tutor tutor) {
        return java.util.stream.Stream.of(tutor.getNombres(), tutor.getPrimerApellido(),
                        tutor.getSegundoApellido())
                .filter(valor -> valor != null && !valor.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
