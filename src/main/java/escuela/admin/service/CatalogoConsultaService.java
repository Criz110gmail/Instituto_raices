package escuela.admin.service;

import escuela.academico.entity.*;
import escuela.academico.repository.*;
import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.AlumnoTutor;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.admin.dto.*;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.CuotaAlumno;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.entity.EstadoRegistroCargo;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.CargoRepository;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.cobranza.repository.TipoBecaRepository;
import escuela.cobranza.repository.BecaAlumnoRepository;
import escuela.cobranza.repository.AjusteCargoRepository;
import escuela.cobranza.repository.PoliticaRecargoRepository;
import escuela.finanzas.entity.CuentaFinanciera;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.entity.MotivoFinanciero;
import escuela.finanzas.repository.MotivoFinancieroRepository;
import escuela.finanzas.entity.Pago;
import escuela.finanzas.repository.PagoRepository;
import escuela.institucion.entity.*;
import escuela.institucion.repository.*;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import static escuela.cobranza.support.CalculoCargo.saldo;
import static escuela.cobranza.support.CalculoCargo.total;

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
    private final AlumnoTutorRepository alumnoTutorRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ConceptoCobroRepository conceptoCobroRepository;
    private final CuotaAlumnoRepository cuotaAlumnoRepository;
    private final CargoRepository cargoRepository;
    private final TipoBecaRepository tipoBecaRepository;
    private final BecaAlumnoRepository becaAlumnoRepository;
    private final AjusteCargoRepository ajusteCargoRepository;
    private final PoliticaRecargoRepository politicaRecargoRepository;
    private final MotivoFinancieroRepository motivoFinancieroRepository;
    private final CuentaFinancieraRepository cuentaFinancieraRepository;
    private final PagoRepository pagoRepository;
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
            case VINCULOS_TUTOR -> consultar(modulo, alumnoTutorRepository,
                    textoVinculo(f), activo(f), pagina,
                    e -> filaVinculo(e, e.getAlumno().getMatricula() + " · " + nombreAlumno(e.getAlumno()),
                            nombreTutor(e.getTutor()), parentesco(e), permisos(e), vigencia(e)));
            case INSCRIPCIONES -> consultar(modulo, inscripcionRepository,
                    textoInscripcion(f), estado(f, "estado"), pagina,
                    e -> filaEstadoInscripcion(e, e.getNumeroInscripcion(),
                            e.getAlumno().getMatricula() + " · " + nombreAlumno(e.getAlumno()),
                            e.getPlantel().getNombre(), e.getCicloEscolar().getNombre(),
                            e.getGrado().getNombre(), vigencia(e)));
            case CONCEPTOS_COBRO -> consultar(modulo, conceptoCobroRepository,
                    textoConcepto(f), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getCodigo(), e.getNombre(),
                            e.getInstitucion().getNombre(), etiqueta(e.getCategoria().name()),
                            reglas(e)));
            case CUOTAS_ALUMNO -> consultar(modulo, cuotaAlumnoRepository,
                    textoCuota(f), estado(f, "estado"), pagina,
                    e -> filaEstadoCuota(e,
                            e.getInscripcion().getAlumno().getMatricula() + " · "
                                    + nombreAlumno(e.getInscripcion().getAlumno()),
                            e.getConceptoCobro().getCodigo() + " · " + e.getConceptoCobro().getNombre(),
                            e.getImporteBase().toPlainString() + " " + e.getMoneda(),
                            etiqueta(e.getFrecuencia().name()), vencimiento(e),
                            e.isGeneracionAutomatica() ? "Automática" : "Manual"));
            case CARGOS -> consultar(modulo, cargoRepository, textoCargo(f),
                    estado(f, "estadoRegistro"), pagina, this::filaCargo);
            case TIPOS_BECA -> consultar(modulo, tipoBecaRepository,
                    texto(f, "codigo", "nombre", "descripcion"), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getCodigo(), e.getNombre(),
                            e.getInstitucion().getNombre(), valor(e.getDescripcion())));
            case BECAS_ALUMNO -> consultar(modulo, becaAlumnoRepository, textoBeca(f),
                    estado(f, "estado"), pagina, this::filaBeca);
            case AJUSTES_CARGO -> consultar(modulo, ajusteCargoRepository, textoAjuste(f),
                    estadoAjuste(f), pagina, this::filaAjuste);
            case POLITICAS_RECARGO -> consultar(modulo, politicaRecargoRepository,
                    textoPoliticaRecargo(f), activo(f), pagina, this::filaPoliticaRecargo);
            case MOTIVOS_FINANCIEROS -> consultar(modulo, motivoFinancieroRepository,
                    texto(f, "codigo", "nombre", "categoria"), activo(f), pagina,
                    e -> fila(e.getId(), e.isActivo(), e.getCodigo(), e.getNombre(),
                            e.getInstitucion().getNombre(), etiqueta(e.getNaturaleza().name()),
                            etiqueta(e.getCategoria())));
            case CUENTAS_FINANCIERAS -> consultar(modulo, cuentaFinancieraRepository,
                    textoCuentaFinanciera(f), activo(f), pagina, this::filaCuentaFinanciera);
            case PAGOS -> consultar(modulo, pagoRepository, textoPago(f),
                    estado(f, "estado"), pagina, this::filaPago);
            case MOVIMIENTOS_FINANCIEROS -> throw new IllegalArgumentException(
                    "Los movimientos usan su consulta especializada");
            case REPORTES_FINANCIEROS -> throw new IllegalArgumentException(
                    "Los reportes financieros usan su consulta especializada");
            case EVENTOS_ESCOLARES -> throw new IllegalArgumentException(
                    "Los eventos escolares usan su consulta especializada");
            case AVISOS -> throw new IllegalArgumentException(
                    "Los avisos escolares usan su consulta especializada");
            case AUDITORIA -> throw new IllegalArgumentException(
                    "La auditoría usa su consulta especializada");
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

    private Specification<CuentaFinanciera> textoCuentaFinanciera(FiltroCatalogo f) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            return cb.or(cb.like(cb.lower(root.get("codigo")), patron),
                    cb.like(cb.lower(root.get("nombre")), patron),
                    cb.like(cb.lower(root.get("bancoNombre")), patron),
                    cb.like(cb.lower(root.get("titular")), patron),
                    cb.like(cb.lower(root.get("plantel").get("nombre")), patron));
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

    private Specification<Pago> textoPago(FiltroCatalogo f) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            var tutor = root.get("tutor");
            return cb.or(cb.like(cb.lower(root.get("folio")), patron),
                    cb.like(cb.lower(root.get("nombrePagador")), patron),
                    cb.like(cb.lower(root.get("referencia")), patron),
                    cb.like(cb.lower(tutor.get("nombres")), patron),
                    cb.like(cb.lower(tutor.get("primerApellido")), patron),
                    cb.like(cb.lower(tutor.get("segundoApellido")), patron),
                    cb.like(cb.lower(root.get("plantelRegistro").get("nombre")), patron));
        };
    }

    private Specification<AlumnoTutor> textoVinculo(FiltroCatalogo f) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            var alumno = root.get("alumno");
            var tutor = root.get("tutor");
            return cb.or(cb.like(cb.lower(alumno.get("matricula")), patron),
                    cb.like(cb.lower(alumno.get("nombres")), patron),
                    cb.like(cb.lower(alumno.get("primerApellido")), patron),
                    cb.like(cb.lower(alumno.get("segundoApellido")), patron),
                    cb.like(cb.lower(tutor.get("nombres")), patron),
                    cb.like(cb.lower(tutor.get("primerApellido")), patron),
                    cb.like(cb.lower(tutor.get("segundoApellido")), patron),
                    cb.like(cb.lower(root.get("parentescoOtro")), patron));
        };
    }

    private Specification<Inscripcion> textoInscripcion(FiltroCatalogo f) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            var alumno = root.get("alumno");
            return cb.or(cb.like(cb.lower(root.get("numeroInscripcion")), patron),
                    cb.like(cb.lower(alumno.get("matricula")), patron),
                    cb.like(cb.lower(alumno.get("nombres")), patron),
                    cb.like(cb.lower(alumno.get("primerApellido")), patron),
                    cb.like(cb.lower(alumno.get("segundoApellido")), patron),
                    cb.like(cb.lower(root.get("plantel").get("nombre")), patron));
        };
    }

    private Specification<ConceptoCobro> textoConcepto(FiltroCatalogo f) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            return cb.or(cb.like(cb.lower(root.get("codigo")), patron),
                    cb.like(cb.lower(root.get("nombre")), patron),
                    cb.like(cb.lower(root.get("descripcion")), patron));
        };
    }

    private Specification<CuotaAlumno> textoCuota(FiltroCatalogo f) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            var inscripcion = root.get("inscripcion");
            var alumno = inscripcion.get("alumno");
            var concepto = root.get("conceptoCobro");
            return cb.or(cb.like(cb.lower(inscripcion.get("numeroInscripcion")), patron),
                    cb.like(cb.lower(alumno.get("matricula")), patron),
                    cb.like(cb.lower(alumno.get("nombres")), patron),
                    cb.like(cb.lower(alumno.get("primerApellido")), patron),
                    cb.like(cb.lower(alumno.get("segundoApellido")), patron),
                    cb.like(cb.lower(concepto.get("codigo")), patron),
                    cb.like(cb.lower(concepto.get("nombre")), patron));
        };
    }

    private Specification<Cargo> textoCargo(FiltroCatalogo f) {
        return (root, query, cb) -> {
            if (f.q().isBlank()) return cb.conjunction();
            String patron = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            var inscripcion = root.get("inscripcion");
            var alumno = inscripcion.get("alumno");
            var concepto = root.get("conceptoCobro");
            return cb.or(cb.like(cb.lower(root.get("descripcion")), patron),
                    cb.like(cb.lower(inscripcion.get("numeroInscripcion")), patron),
                    cb.like(cb.lower(alumno.get("matricula")), patron),
                    cb.like(cb.lower(alumno.get("nombres")), patron),
                    cb.like(cb.lower(alumno.get("primerApellido")), patron),
                    cb.like(cb.lower(alumno.get("segundoApellido")), patron),
                    cb.like(cb.lower(concepto.get("codigo")), patron),
                    cb.like(cb.lower(concepto.get("nombre")), patron));
        };
    }

    private Specification<BecaAlumno> textoBeca(FiltroCatalogo f) {
        return (root, query, cb) -> { if (f.q().isBlank()) return cb.conjunction();
            String p="%"+f.q().toLowerCase(Locale.ROOT)+"%"; var a=root.get("inscripcion").get("alumno");
            return cb.or(cb.like(cb.lower(a.get("matricula")),p),cb.like(cb.lower(a.get("nombres")),p),
                    cb.like(cb.lower(a.get("primerApellido")),p),cb.like(cb.lower(root.get("tipoBeca").get("nombre")),p),
                    cb.like(cb.lower(root.get("conceptoCobro").get("nombre")),p),cb.like(cb.lower(root.get("motivo")),p)); };
    }

    private Specification<AjusteCargo> textoAjuste(FiltroCatalogo f) {
        return (root, query, cb) -> { if (f.q().isBlank()) return cb.conjunction();
            String p="%"+f.q().toLowerCase(Locale.ROOT)+"%"; var cargo=root.get("cargo");var a=cargo.get("inscripcion").get("alumno");
            return cb.or(cb.like(cb.lower(a.get("matricula")),p),cb.like(cb.lower(a.get("nombres")),p),
                    cb.like(cb.lower(a.get("primerApellido")),p),cb.like(cb.lower(cargo.get("conceptoCobro").get("nombre")),p),
                    cb.like(cb.lower(root.get("motivo")),p)); };
    }

    private Specification<AjusteCargo> estadoAjuste(FiltroCatalogo f) {
        return (root, query, cb) -> f.estado().equals("TODOS") ? cb.conjunction()
                : cb.equal(root.get("tipo").as(String.class), f.estado());
    }
    private Specification<PoliticaRecargo> textoPoliticaRecargo(FiltroCatalogo f){return(root,q,cb)->{if(f.q().isBlank())return cb.conjunction();String p="%"+f.q().toLowerCase(Locale.ROOT)+"%";var c=root.get("conceptoCobro");return cb.or(cb.like(cb.lower(c.get("codigo")),p),cb.like(cb.lower(c.get("nombre")),p),cb.like(cb.lower(c.get("institucion").get("nombre")),p));};}

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

    private FilaCatalogo filaEstadoInscripcion(Inscripcion inscripcion, String... celdas) {
        String estado = inscripcion.getEstado().name();
        String tono = estado.equals("ACTIVA") ? "positivo"
                : estado.equals("PREINSCRITA") ? "aviso" : "neutro";
        return new FilaCatalogo(inscripcion.getId(), List.of(celdas), estado, tono);
    }

    private FilaCatalogo filaEstadoCuota(CuotaAlumno cuota, String... celdas) {
        String estado = cuota.getEstado().name();
        String tono = estado.equals("ACTIVA") ? "positivo"
                : estado.equals("SUSPENDIDA") ? "aviso" : "neutro";
        return new FilaCatalogo(cuota.getId(), List.of(celdas), estado, tono);
    }

    private FilaCatalogo filaCargo(Cargo cargo) {
        String estado;
        String tono;
        if (cargo.getEstadoRegistro() == EstadoRegistroCargo.CANCELADO) {
            estado = "Cancelado";
            tono = "neutro";
        } else if (saldo(cargo).signum() == 0) {
            estado = "Pagado";
            tono = "positivo";
        } else if (cargo.getFechaVencimiento().isBefore(LocalDate.now())) {
            estado = "Vencido";
            tono = "aviso";
        } else {
            estado = "Pendiente";
            tono = "positivo";
        }
        String periodo = FECHA.format(cargo.getPeriodoCobroInicio()) + " — "
                + FECHA.format(cargo.getPeriodoCobroFin());
        String importe = cargo.getImporteOriginal().toPlainString() + " " + cargo.getMoneda();
        String total = total(cargo).toPlainString() + " " + cargo.getMoneda();
        String saldoTexto = cargo.getEstadoRegistro() == EstadoRegistroCargo.CANCELADO
                ? "0.00 " + cargo.getMoneda() : saldo(cargo).toPlainString() + " " + cargo.getMoneda();
        return new FilaCatalogo(cargo.getId(), List.of(
                cargo.getInscripcion().getAlumno().getMatricula() + " · "
                        + nombreAlumno(cargo.getInscripcion().getAlumno()),
                cargo.getConceptoCobro().getCodigo() + " · " + cargo.getConceptoCobro().getNombre(),
                cargo.getDescripcion(), periodo, FECHA.format(cargo.getFechaVencimiento()),
                importe, saldoTexto), estado, tono);
    }

    private FilaCatalogo filaBeca(BecaAlumno b) {
        var a=b.getInscripcion().getAlumno(); String beneficio=b.getModalidad()==ModalidadBeca.PORCENTAJE
                ? b.getPorcentaje().stripTrailingZeros().toPlainString()+" %"
                : b.getMontoFijo().toPlainString()+" "+b.getMoneda();
        return new FilaCatalogo(b.getId(),List.of(a.getMatricula()+" · "+nombreAlumno(a),b.getTipoBeca().getNombre(),
                b.getConceptoCobro().getNombre(),beneficio,FECHA.format(b.getFechaInicio())+" — "+FECHA.format(b.getFechaFin())),
                b.getEstado().name(),b.getEstado()==EstadoBeca.ACTIVA?"positivo":b.getEstado()==EstadoBeca.SUSPENDIDA?"aviso":"neutro");
    }

    private FilaCatalogo filaAjuste(AjusteCargo a) {
        var alumno=a.getCargo().getInscripcion().getAlumno(); String estado=a.getReversa()!=null?"Reversado":a.getReversaDe()!=null?"Reversa":"Aplicado";
        return new FilaCatalogo(a.getId(),List.of(alumno.getMatricula()+" · "+nombreAlumno(alumno),
                a.getCargo().getConceptoCobro().getNombre(),etiqueta(a.getTipo().name()),etiqueta(a.getEfecto().name()),
                a.getMonto().toPlainString()+" "+a.getCargo().getMoneda(),FECHA.format(a.getFechaEfectiva()),a.getMotivo()),estado,a.getReversa()!=null?"neutro":"positivo");
    }
    private FilaCatalogo filaPoliticaRecargo(PoliticaRecargo p){String recargo=p.getModalidad()==ModalidadBeca.PORCENTAJE?p.getPorcentaje().stripTrailingZeros().toPlainString()+" %":p.getMontoFijo().toPlainString()+" "+p.getMoneda();String limite=switch(p.getTipoLimite()){case SIN_LIMITE->"Sin límite";case MONTO_FIJO->p.getValorLimite().setScale(2,java.math.RoundingMode.HALF_UP).toPlainString()+" "+p.getConceptoCobro().getInstitucion().getMonedaPredeterminada();case PORCENTAJE_ORIGINAL->p.getValorLimite().stripTrailingZeros().toPlainString()+" % del original";};return fila(p.getId(),p.isActivo(),p.getConceptoCobro().getCodigo()+" · "+p.getConceptoCobro().getNombre(),p.getConceptoCobro().getInstitucion().getNombre(),recargo,p.getDiasGracia()+" días",etiqueta(p.getPeriodicidad().name()),limite,p.isGeneracionAutomatica()?"Automática":"Manual");}

    private FilaCatalogo filaCuentaFinanciera(CuentaFinanciera cuenta) {
        String alcance = cuenta.getPlantel() == null ? "Institucional" : cuenta.getPlantel().getNombre();
        String identificador = cuenta.getClabe() != null
                ? enmascarar(cuenta.getClabe()) + " · CLABE"
                : cuenta.getNumeroCuenta() != null ? enmascarar(cuenta.getNumeroCuenta()) : "—";
        String institucionFinanciera = cuenta.getBancoNombre() == null ? "Caja" : cuenta.getBancoNombre();
        String tipo = switch (cuenta.getTipo()) {
            case CAJA -> "Caja";
            case BANCO -> "Cuenta bancaria";
            case INVERSION -> "Inversión";
        };
        return fila(cuenta.getId(), cuenta.isActivo(), cuenta.getCodigo(), cuenta.getNombre(), alcance,
                tipo, institucionFinanciera, identificador,
                cuenta.getSaldoInicial().toPlainString() + " " + cuenta.getMoneda(),
                FECHA.format(cuenta.getFechaSaldoInicial()));
    }

    private FilaCatalogo filaPago(Pago pago) {
        java.math.BigDecimal solicitado = pago.getSolicitudes().stream()
                .map(escuela.finanzas.entity.SolicitudAplicacionPago::getMontoSolicitado)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal aplicado = pago.getAplicaciones().stream()
                .map(a -> a.getOperacion() == escuela.finanzas.entity.OperacionAplicacionPago.APLICAR
                        ? a.getMonto() : a.getMonto().negate())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal devuelto = pago.getDevoluciones().stream()
                .filter(d -> d.getEstado() == escuela.finanzas.entity.EstadoDevolucionPago.EJECUTADA)
                .map(escuela.finanzas.entity.DevolucionPago::getMonto)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        String distribucion = pago.getEstado() == escuela.finanzas.entity.EstadoPago.VALIDADO
                ? aplicado.toPlainString() + " aplicado · "
                    + devuelto.toPlainString() + " devuelto · "
                    + pago.getMonto().subtract(aplicado).subtract(devuelto).toPlainString() + " disponible"
                : solicitado.signum() == 0 ? "Sin asignar"
                    : solicitado.toPlainString() + " " + pago.getMoneda() + " · "
                    + pago.getSolicitudes().size() + " cargo(s)";
        String estado = switch (pago.getEstado()) {
            case PENDIENTE_VALIDACION -> "Pendiente de validación";
            case VALIDADO -> "Validado";
            case RECHAZADO -> "Rechazado";
            case CANCELADO -> "Cancelado";
        };
        String tono = pago.getEstado() == escuela.finanzas.entity.EstadoPago.PENDIENTE_VALIDACION
                ? "aviso" : pago.getEstado() == escuela.finanzas.entity.EstadoPago.VALIDADO ? "positivo" : "neutro";
        String fecha = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", new Locale("es", "MX"))
                .withZone(java.time.ZoneId.of(pago.getInstitucion().getZonaHoraria())).format(pago.getFechaPago());
        return new FilaCatalogo(pago.getId(), List.of(pago.getFolio(), nombreTutor(pago.getTutor()),
                pago.getPlantelRegistro().getNombre(), fecha, etiqueta(pago.getMetodo().name()),
                pago.getMonto().toPlainString() + " " + pago.getMoneda(), distribucion,
                String.valueOf(pago.getComprobantes().size())), estado, tono);
    }

    private String enmascarar(String valor) {
        String limpio = valor == null ? "" : valor.replaceAll("\\s+", "");
        return "•••• " + limpio.substring(Math.max(0, limpio.length() - 4));
    }

    private FilaCatalogo filaVinculo(AlumnoTutor vinculo, String... celdas) {
        String estado;
        String tono;
        LocalDate hoy = LocalDate.now();
        if (!vinculo.isActivo()) {
            estado = "Revocado";
            tono = "neutro";
        } else if (vinculo.getFechaInicio().isAfter(hoy)) {
            estado = "Programado";
            tono = "aviso";
        } else if (vinculo.getFechaFin() != null && vinculo.getFechaFin().isBefore(hoy)) {
            estado = "Finalizado";
            tono = "neutro";
        } else {
            estado = "Vigente";
            tono = "positivo";
        }
        return new FilaCatalogo(vinculo.getId(), List.of(celdas), estado, tono);
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

    private String parentesco(AlumnoTutor vinculo) {
        if (vinculo.getParentesco() == escuela.alumno.entity.ParentescoTutor.OTRO) {
            return valor(vinculo.getParentescoOtro());
        }
        return switch (vinculo.getParentesco()) {
            case MADRE -> "Madre";
            case PADRE -> "Padre";
            case TUTOR_LEGAL -> "Tutor legal";
            case OTRO -> "Otro";
        };
    }

    private String permisos(AlumnoTutor vinculo) {
        java.util.ArrayList<String> valores = new java.util.ArrayList<>();
        if (vinculo.isContactoPrincipal()) valores.add("Principal");
        if (vinculo.isResponsableFinanciero()) valores.add("Responsable financiero");
        if (vinculo.isPuedeAutorizar()) valores.add("Autoriza");
        if (vinculo.isPuedeRecoger()) valores.add("Recoge");
        if (vinculo.isPuedeVerFinanzas()) valores.add("Ve finanzas");
        if (vinculo.isPuedeRecibirNotificaciones()) valores.add("Notificaciones");
        return valores.isEmpty() ? "Sin autorizaciones" : String.join(", ", valores);
    }

    private String vigencia(AlumnoTutor vinculo) {
        return FECHA.format(vinculo.getFechaInicio()) + " — "
                + (vinculo.getFechaFin() == null ? "Sin fecha de fin" : FECHA.format(vinculo.getFechaFin()));
    }

    private String vigencia(Inscripcion inscripcion) {
        return FECHA.format(inscripcion.getFechaInicio()) + " — "
                + (inscripcion.getFechaFin() == null ? "Vigente" : FECHA.format(inscripcion.getFechaFin()));
    }

    private String reglas(ConceptoCobro concepto) {
        java.util.ArrayList<String> reglas = new java.util.ArrayList<>();
        if (concepto.isPermiteBeca()) reglas.add("Beca");
        if (concepto.isPermiteDescuento()) reglas.add("Descuento");
        if (concepto.isPermiteRecargo()) reglas.add("Recargo");
        return reglas.isEmpty() ? "Sin ajustes" : String.join(", ", reglas);
    }

    private String vencimiento(CuotaAlumno cuota) {
        if (cuota.getFrecuencia() == escuela.cobranza.entity.FrecuenciaCuota.UNICA) {
            return FECHA.format(cuota.getFechaVencimientoUnico());
        }
        return "Día " + cuota.getDiaVencimiento() + " de cada mes";
    }

    private String etiqueta(String valor) {
        return java.util.Arrays.stream(valor.toLowerCase(Locale.ROOT).split("_"))
                .map(parte -> Character.toUpperCase(parte.charAt(0)) + parte.substring(1))
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
