package escuela.seguridad.service;

import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.GradoRepository;
import escuela.academico.repository.GrupoRepository;
import escuela.academico.repository.NivelEducativoRepository;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.cobranza.repository.CargoRepository;
import escuela.cobranza.repository.TipoBecaRepository;
import escuela.cobranza.repository.BecaAlumnoRepository;
import escuela.cobranza.repository.AjusteCargoRepository;
import escuela.cobranza.repository.PoliticaRecargoRepository;
import escuela.comunicacion.repository.EventoEscolarRepository;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.repository.MotivoFinancieroRepository;
import escuela.finanzas.repository.PagoRepository;
import escuela.finanzas.entity.CorteCaja;
import escuela.finanzas.entity.CuentaFinanciera;
import escuela.admin.dto.ModuloCatalogo;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.dto.response.PlantelNivelResponse;
import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.institucion.repository.InstitucionRepository;
import escuela.institucion.repository.PlantelNivelRepository;
import escuela.institucion.repository.PlantelRepository;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.seguridad.dto.response.RolResponse;
import escuela.seguridad.repository.RolRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.tutor.repository.TutorRepository;
import jakarta.persistence.criteria.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlcanceDatosService {

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
    private final EventoEscolarRepository eventoEscolarRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    public <T> Specification<T> especificacion(ModuloCatalogo modulo) {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return (root, query, cb) -> cb.conjunction();
        return (root, query, cb) -> {
            Path<?> institucion = switch (modulo) {
                case INSTITUCIONES -> root.get("id");
                case PLANTELES, NIVELES, CICLOS, ALUMNOS, TUTORES, CONCEPTOS_COBRO, TIPOS_BECA, MOTIVOS_FINANCIEROS, CUENTAS_FINANCIERAS, PAGOS, MOVIMIENTOS_FINANCIEROS, REPORTES_FINANCIEROS, EVENTOS_ESCOLARES, ROLES, USUARIOS -> root.get("institucion").get("id");
                case POLITICAS_RECARGO -> root.get("conceptoCobro").get("institucion").get("id");
                case VINCULOS_TUTOR, INSCRIPCIONES -> root.get("alumno").get("institucion").get("id");
                case CUOTAS_ALUMNO, CARGOS, BECAS_ALUMNO -> root.get("inscripcion").get("alumno").get("institucion").get("id");
                case AJUSTES_CARGO -> root.get("cargo").get("inscripcion").get("alumno").get("institucion").get("id");
                case OFERTA -> root.get("plantel").get("institucion").get("id");
                case GRADOS -> root.get("nivelEducativo").get("institucion").get("id");
                case PERIODOS -> root.get("cicloEscolar").get("institucion").get("id");
                case GRUPOS -> root.get("plantel").get("institucion").get("id");
            };
            var mismaInstitucion = cb.equal(institucion, principal.institucionId());
            if (principal.alcanceInstitucional()) return mismaInstitucion;
            if (modulo == ModuloCatalogo.VINCULOS_TUTOR && principal.plantelIds().isEmpty()) {
                var vigente = cb.and(cb.isTrue(root.get("activo")),
                        cb.lessThanOrEqualTo(root.<java.time.LocalDate>get("fechaInicio"), java.time.LocalDate.now()),
                        cb.or(cb.isNull(root.get("fechaFin")),
                                cb.greaterThanOrEqualTo(root.<java.time.LocalDate>get("fechaFin"), java.time.LocalDate.now())));
                return cb.and(mismaInstitucion, vigente,
                        cb.equal(root.get("tutor").get("usuario").get("id"), principal.usuarioId()));
            }
            if (principal.plantelIds().isEmpty()) return cb.disjunction();
            if (modulo == ModuloCatalogo.CUENTAS_FINANCIERAS) {
                return cb.and(mismaInstitucion, cb.or(cb.isNull(root.get("plantel")),
                        root.get("plantel").get("id").in(principal.plantelIds())));
            }
            if (modulo == ModuloCatalogo.MOVIMIENTOS_FINANCIEROS) {
                return cb.and(mismaInstitucion,
                        root.get("plantelOperacion").get("id").in(principal.plantelIds()));
            }
            if (modulo == ModuloCatalogo.USUARIOS) {
                return cb.and(mismaInstitucion, cb.equal(root.get("id"), principal.usuarioId()));
            }
            if (modulo == ModuloCatalogo.EVENTOS_ESCOLARES) {
                return cb.and(mismaInstitucion, cb.or(cb.isNull(root.get("plantel")),
                        root.get("plantel").get("id").in(principal.plantelIds())));
            }
            Path<Long> plantel = switch (modulo) {
                case PLANTELES -> root.get("id");
                case OFERTA, GRUPOS, INSCRIPCIONES -> root.get("plantel").get("id");
                case PAGOS -> root.get("plantelRegistro").get("id");
                case MOVIMIENTOS_FINANCIEROS -> root.get("plantelOperacion").get("id");
                case CUOTAS_ALUMNO, CARGOS, BECAS_ALUMNO -> root.get("inscripcion").get("plantel").get("id");
                case AJUSTES_CARGO -> root.get("cargo").get("inscripcion").get("plantel").get("id");
                default -> null;
            };
            if (plantel == null) return mismaInstitucion;
            return cb.and(mismaInstitucion, plantel.in(principal.plantelIds()));
        };
    }

    /** Alcance especializado: los cortes institucionales sólo son visibles con alcance institucional. */
    public Specification<CorteCaja> especificacionCortesCaja() {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return (root, query, cb) -> cb.disjunction();
        return (root, query, cb) -> {
            var mismaInstitucion = cb.equal(root.get("institucion").get("id"), principal.institucionId());
            if (principal.alcanceInstitucional()) return mismaInstitucion;
            if (principal.plantelIds().isEmpty()) return cb.disjunction();
            return cb.and(mismaInstitucion,
                    root.get("cuenta").get("plantel").get("id").in(principal.plantelIds()));
        };
    }

    public Specification<CuentaFinanciera> especificacionCuentasParaCorte() {
        return especificacionCuentasRestringidas();
    }

    public Specification<CuentaFinanciera> especificacionCuentasReporte() {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return (root, query, cb) -> cb.conjunction();
        return especificacionCuentasRestringidas();
    }

    private Specification<CuentaFinanciera> especificacionCuentasRestringidas() {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return (root, query, cb) -> cb.disjunction();
        return (root, query, cb) -> {
            var mismaInstitucion = cb.equal(root.get("institucion").get("id"), principal.institucionId());
            if (principal.alcanceInstitucional()) return mismaInstitucion;
            if (principal.plantelIds().isEmpty()) return cb.disjunction();
            return cb.and(mismaInstitucion, root.get("plantel").get("id").in(principal.plantelIds()));
        };
    }

    public void validarRecurso(ModuloCatalogo modulo, Long id) {
        switch (modulo) {
            case INSTITUCIONES -> validarInstitucion(id);
            case PLANTELES -> validarPlantel(id);
            case NIVELES -> validarInstitucion(nivelRepository.findById(id)
                    .orElseThrow(this::denegado).getInstitucion().getId());
            case OFERTA -> validarPlantel(ofertaRepository.findById(id)
                    .orElseThrow(this::denegado).getPlantel().getId());
            case GRADOS -> validarInstitucion(gradoRepository.findById(id)
                    .orElseThrow(this::denegado).getNivelEducativo().getInstitucion().getId());
            case CICLOS -> validarInstitucion(cicloRepository.findById(id)
                    .orElseThrow(this::denegado).getInstitucion().getId());
            case PERIODOS -> validarInstitucion(periodoRepository.findById(id)
                    .orElseThrow(this::denegado).getCicloEscolar().getInstitucion().getId());
            case GRUPOS -> validarPlantel(grupoRepository.findById(id)
                    .orElseThrow(this::denegado).getPlantel().getId());
            case ALUMNOS -> validarInstitucion(alumnoRepository.findById(id)
                    .orElseThrow(this::denegado).getInstitucion().getId());
            case TUTORES -> validarInstitucion(tutorRepository.findById(id)
                    .orElseThrow(this::denegado).getInstitucion().getId());
            case VINCULOS_TUTOR -> validarVinculoTutor(id);
            case INSCRIPCIONES -> validarPlantel(inscripcionRepository.findById(id)
                    .orElseThrow(this::denegado).getPlantel().getId());
            case CONCEPTOS_COBRO -> validarInstitucion(conceptoCobroRepository.findById(id)
                    .orElseThrow(this::denegado).getInstitucion().getId());
            case CUOTAS_ALUMNO -> validarPlantel(cuotaAlumnoRepository.findById(id)
                    .orElseThrow(this::denegado).getInscripcion().getPlantel().getId());
            case CARGOS -> validarPlantel(cargoRepository.findById(id)
                    .orElseThrow(this::denegado).getInscripcion().getPlantel().getId());
            case TIPOS_BECA -> validarInstitucion(tipoBecaRepository.findById(id)
                    .orElseThrow(this::denegado).getInstitucion().getId());
            case BECAS_ALUMNO -> validarPlantel(becaAlumnoRepository.findById(id)
                    .orElseThrow(this::denegado).getInscripcion().getPlantel().getId());
            case AJUSTES_CARGO -> validarPlantel(ajusteCargoRepository.findById(id)
                    .orElseThrow(this::denegado).getCargo().getInscripcion().getPlantel().getId());
            case POLITICAS_RECARGO -> validarInstitucion(politicaRecargoRepository.findById(id)
                    .orElseThrow(this::denegado).getConceptoCobro().getInstitucion().getId());
            case MOTIVOS_FINANCIEROS -> validarAdministracionInstitucional(motivoFinancieroRepository.findById(id)
                    .orElseThrow(this::denegado).getInstitucion().getId());
            case CUENTAS_FINANCIERAS -> {
                var cuenta = cuentaFinancieraRepository.findById(id).orElseThrow(this::denegado);
                if (cuenta.getPlantel() == null) validarAdministracionInstitucional(cuenta.getInstitucion().getId());
                else validarPlantel(cuenta.getPlantel().getId());
            }
            case PAGOS -> validarPlantel(pagoRepository.findById(id)
                    .orElseThrow(this::denegado).getPlantelRegistro().getId());
            case MOVIMIENTOS_FINANCIEROS -> throw denegado();
            case REPORTES_FINANCIEROS -> throw denegado();
            case EVENTOS_ESCOLARES -> {
                var evento = eventoEscolarRepository.findById(id).orElseThrow(this::denegado);
                if (evento.getPlantel() == null) validarInstitucion(evento.getInstitucion().getId());
                else validarPlantel(evento.getPlantel().getId());
            }
            case ROLES -> validarInstitucion(rolRepository.findById(id)
                    .orElseThrow(this::denegado).getInstitucion().getId());
            case USUARIOS -> validarInstitucion(usuarioRepository.findById(id)
                    .orElseThrow(this::denegado).getInstitucion().getId());
        }
    }

    public void validarInstitucion(Long institucionId) {
        UsuarioPrincipal principal = principal();
        if (!principal.accesoRecuperacion()
                && (!principal.institucionId().equals(institucionId)
                || (!principal.alcanceInstitucional() && principal.plantelIds().isEmpty()))) throw denegado();
    }

    public boolean alcanceInstitucionalActual(Long institucionId) {
        validarInstitucion(institucionId);
        UsuarioPrincipal principal = principal();
        return principal.accesoRecuperacion() || principal.alcanceInstitucional();
    }

    public java.util.Set<Long> plantelesActuales(Long institucionId) {
        validarInstitucion(institucionId);
        UsuarioPrincipal principal = principal();
        return principal.accesoRecuperacion() || principal.alcanceInstitucional()
                ? java.util.Set.of() : principal.plantelIds();
    }

    public void validarNuevaInstitucion() {
        if (!principal().accesoRecuperacion()) throw denegado();
    }

    public void validarNuevoPlantel(Long institucionId) {
        validarInstitucion(institucionId);
        UsuarioPrincipal principal = principal();
        if (!principal.accesoRecuperacion() && !principal.alcanceInstitucional()) throw denegado();
    }

    public void validarAdministracionInstitucional(Long institucionId) {
        validarInstitucion(institucionId);
        UsuarioPrincipal principal = principal();
        if (!principal.accesoRecuperacion() && !principal.alcanceInstitucional()) throw denegado();
    }

    public void validarPlantel(Long plantelId) {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return;
        var plantel = plantelRepository.findById(plantelId).orElseThrow(this::denegado);
        validarInstitucion(plantel.getInstitucion().getId());
        if (!principal.alcanceInstitucional() && !principal.plantelIds().contains(plantelId)) throw denegado();
    }

    public List<InstitucionResponse> filtrarInstituciones(List<InstitucionResponse> instituciones) {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return instituciones;
        if (!principal.alcanceInstitucional() && principal.plantelIds().isEmpty()) return List.of();
        return instituciones.stream().filter(i -> i.id().equals(principal.institucionId())).toList();
    }

    public List<PlantelResponse> filtrarPlanteles(List<PlantelResponse> planteles) {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return planteles;
        return planteles.stream()
                .filter(p -> p.institucionId().equals(principal.institucionId()))
                .filter(p -> principal.alcanceInstitucional() || principal.plantelIds().contains(p.id()))
                .toList();
    }

    public List<NivelEducativoResponse> filtrarNiveles(List<NivelEducativoResponse> niveles) {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return niveles;
        if (!principal.alcanceInstitucional() && principal.plantelIds().isEmpty()) return List.of();
        return niveles.stream().filter(n -> n.institucionId().equals(principal.institucionId())).toList();
    }

    public List<CicloEscolarResponse> filtrarCiclos(List<CicloEscolarResponse> ciclos) {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return ciclos;
        if (!principal.alcanceInstitucional() && principal.plantelIds().isEmpty()) return List.of();
        return ciclos.stream().filter(c -> c.institucionId().equals(principal.institucionId())).toList();
    }

    public List<PlantelNivelResponse> filtrarOfertas(List<PlantelNivelResponse> ofertas) {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion() || principal.alcanceInstitucional()) return ofertas.stream()
                .filter(o -> principal.accesoRecuperacion() || perteneceInstitucion(o.plantelId(), principal.institucionId()))
                .toList();
        return ofertas.stream().filter(o -> principal.plantelIds().contains(o.plantelId())).toList();
    }

    public List<RolResponse> filtrarRoles(List<RolResponse> roles) {
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return roles;
        if (!principal.alcanceInstitucional() && principal.plantelIds().isEmpty()) return List.of();
        return roles.stream().filter(r -> r.institucionId().equals(principal.institucionId())).toList();
    }

    private void validarVinculoTutor(Long id) {
        var vinculo = alumnoTutorRepository.findById(id).orElseThrow(this::denegado);
        UsuarioPrincipal principal = principal();
        if (principal.accesoRecuperacion()) return;
        if (!vinculo.getAlumno().getInstitucion().getId().equals(principal.institucionId())) {
            throw denegado();
        }
        if (principal.alcanceInstitucional() || !principal.plantelIds().isEmpty()) return;
        java.time.LocalDate hoy = java.time.LocalDate.now();
        if (!vinculo.isActivo() || vinculo.getFechaInicio().isAfter(hoy)
                || (vinculo.getFechaFin() != null && vinculo.getFechaFin().isBefore(hoy))) {
            throw denegado();
        }
        if (vinculo.getTutor().getUsuario() == null
                || !vinculo.getTutor().getUsuario().getId().equals(principal.usuarioId())) {
            throw denegado();
        }
    }

    private UsuarioPrincipal principal() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)) throw denegado();
        return principal;
    }

    private boolean perteneceInstitucion(Long plantelId, Long institucionId) {
        return plantelRepository.findById(plantelId)
                .map(p -> p.getInstitucion().getId().equals(institucionId)).orElse(false);
    }

    private AccessDeniedException denegado() {
        return new AccessDeniedException("No tienes acceso a los datos solicitados");
    }
}
