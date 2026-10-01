package escuela.admin.service;

import escuela.admin.dto.OpcionAutocompletado;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoRepository;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.entity.AjusteCargo;
import escuela.cobranza.entity.EfectoAjusteCargo;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.TipoBecaRepository;
import escuela.cobranza.repository.CargoRepository;
import escuela.cobranza.entity.TipoBeca;
import escuela.academico.entity.PeriodoAcademico;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.academico.repository.GradoRepository;
import escuela.academico.repository.GrupoRepository;
import escuela.academico.repository.MateriaGradoRepository;
import escuela.docente.entity.Maestro;
import escuela.docente.repository.MaestroRepository;
import escuela.academico.entity.Grupo;
import escuela.admin.dto.ModuloCatalogo;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import escuela.finanzas.entity.CuentaFinanciera;
import escuela.finanzas.entity.MetodoPago;
import escuela.finanzas.entity.TipoCuentaFinanciera;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.stream.Stream;
import static escuela.cobranza.support.CalculoCargo.saldo;
import static escuela.common.support.FormatoMoneda.formatear;

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
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    private final GradoRepository gradoRepository;
    private final GrupoRepository grupoRepository;
    private final MaestroRepository maestroRepository;
    private final MateriaGradoRepository materiaGradoRepository;
    private final TipoBecaRepository tipoBecaRepository;
    private final CargoRepository cargoRepository;
    private final CuentaFinancieraRepository cuentaFinancieraRepository;
    private final AlcanceDatosService alcance;

    public ResultadoAutocompletado maestrosPlaneacion(String consulta) {
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        String patron = "%" + texto + "%";
        Specification<Maestro> busqueda = (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("numeroEmpleado")), patron),
                cb.like(cb.lower(root.get("nombres")), patron),
                cb.like(cb.lower(root.get("primerApellido")), patron),
                cb.like(cb.lower(root.get("segundoApellido")), patron));
        int limite = tamano(consulta);
        var resultado = maestroRepository.findAll(Specification.where(busqueda)
                        .and(alcance.especificacion(ModuloCatalogo.MAESTROS)),
                PageRequest.of(0, limite + 1));
        boolean hayMas = resultado.getNumberOfElements() > limite;
        return new ResultadoAutocompletado(resultado.getContent().stream().limit(limite)
                .map(maestro -> new OpcionAutocompletado(maestro.getId(),
                        maestro.getNumeroEmpleado() + " · " + nombre(maestro.getNombres(),
                                maestro.getPrimerApellido(), maestro.getSegundoApellido()),
                        maestro.getInstitucion().getNombre() + (maestro.isActivo() ? "" : " · Inactivo")))
                .toList(), hayMas);
    }

    public ResultadoAutocompletado gruposPlaneacion(String consulta) {
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        String patron = "%" + texto + "%";
        Specification<Grupo> busqueda = (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("nombre")), patron),
                cb.like(cb.lower(root.get("codigo")), patron),
                cb.like(cb.lower(root.get("plantel").get("nombre")), patron),
                cb.like(cb.lower(root.get("grado").get("nombre")), patron));
        int limite = tamano(consulta);
        var resultado = grupoRepository.findAll(Specification.where(busqueda)
                        .and(alcance.especificacion(ModuloCatalogo.GRUPOS)),
                PageRequest.of(0, limite + 1));
        boolean hayMas = resultado.getNumberOfElements() > limite;
        return new ResultadoAutocompletado(resultado.getContent().stream().limit(limite)
                .map(grupo -> new OpcionAutocompletado(grupo.getId(),
                        grupo.getNombre() + " · " + grupo.getGrado().getNombre(),
                        grupo.getPlantel().getNombre() + " · " + grupo.getCicloEscolar().getNombre()))
                .toList(), hayMas);
    }

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
                institucionId, texto, PageRequest.of(0, tamano(consulta)));
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
                institucionId, texto, PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(tutor -> new OpcionAutocompletado(tutor.getId(),
                        nombre(tutor.getNombres(), tutor.getPrimerApellido(),
                                tutor.getSegundoApellido()),
                        detalle(tutor.getTelefonoPrincipal(), tutor.getEmail())))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado tutoresParaPago(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<Tutor> resultado = tutorRepository.buscarParaAutocompletado(
                institucionId, texto, PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(tutor -> new OpcionAutocompletado(tutor.getId(),
                        nombre(tutor.getNombres(), tutor.getPrimerApellido(), tutor.getSegundoApellido()),
                        detalle(tutor.getTelefonoPrincipal(), tutor.getEmail())))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado usuarios(Long institucionId, String consulta, Long tutorId) {
        alcance.validarAdministracionInstitucional(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<Usuario> resultado = usuarioRepository.buscarParaAutocompletado(
                institucionId, texto, tutorId, PageRequest.of(0, tamano(consulta)));
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
                plantelId, texto, PageRequest.of(0, tamano(consulta)));
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
                institucionId, texto, PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(concepto -> new OpcionAutocompletado(concepto.getId(),
                        concepto.getCodigo() + " · " + concepto.getNombre(),
                        concepto.getCategoria().name()))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado periodosCargo(Long inscripcionId, String consulta) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, inscripcionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<PeriodoAcademico> resultado = periodoAcademicoRepository.buscarParaCargo(
                inscripcionId, texto, PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(periodo -> new OpcionAutocompletado(periodo.getId(),
                        periodo.getCodigo() + " · " + periodo.getNombre(),
                        periodo.getFechaInicio() + " — " + periodo.getFechaFin()))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado gradosMateria(Long institucionId, String consulta) {
        alcance.validarAdministracionInstitucional(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        var resultado = gradoRepository.buscarParaMateria(
                institucionId, texto, PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(grado -> new OpcionAutocompletado(grado.getId(),
                        grado.getNivelEducativo().getNombre() + " · " + grado.getCodigo()
                                + " · " + grado.getNombre(),
                        "Disponible para configurar la materia"))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado gruposCalificacion(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        String patron = "%" + texto + "%";
        Specification<Grupo> busqueda = (root, query, cb) -> cb.and(
                cb.isTrue(root.get("activo")),
                cb.equal(root.get("plantel").get("institucion").get("id"), institucionId),
                cb.or(cb.like(cb.lower(root.get("nombre")), patron),
                        cb.like(cb.lower(root.get("codigo")), patron),
                        cb.like(cb.lower(root.get("plantel").get("nombre")), patron),
                        cb.like(cb.lower(root.get("grado").get("nombre")), patron)));
        var resultado = grupoRepository.findAll(Specification.where(busqueda)
                        .and(alcance.especificacion(ModuloCatalogo.GRUPOS)),
                PageRequest.of(0, tamano(consulta) + 1));
        boolean hayMas = resultado.getNumberOfElements() > MAXIMO_RESULTADOS;
        return new ResultadoAutocompletado(resultado.getContent().stream().limit(MAXIMO_RESULTADOS)
                .map(grupo -> new OpcionAutocompletado(grupo.getId(),
                        grupo.getNombre() + " · " + grupo.getGrado().getNombre(),
                        grupo.getPlantel().getNombre() + " · " + grupo.getCicloEscolar().getNombre()))
                .toList(), hayMas);
    }

    public ResultadoAutocompletado gruposBoleta(Long cicloId, String consulta) {
        alcance.validarRecurso(ModuloCatalogo.CICLOS, cicloId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        String patron = "%" + texto + "%";
        Specification<Grupo> busqueda = (root, query, cb) -> cb.and(
                cb.equal(root.get("cicloEscolar").get("id"), cicloId),
                cb.or(cb.like(cb.lower(root.get("nombre")), patron),
                        cb.like(cb.lower(root.get("codigo")), patron),
                        cb.like(cb.lower(root.get("plantel").get("nombre")), patron),
                        cb.like(cb.lower(root.get("grado").get("nombre")), patron)));
        var resultado = grupoRepository.findAll(Specification.where(busqueda)
                        .and(alcance.especificacion(ModuloCatalogo.GRUPOS)),
                PageRequest.of(0, tamano(consulta) + 1));
        boolean hayMas = resultado.getNumberOfElements() > MAXIMO_RESULTADOS;
        return new ResultadoAutocompletado(resultado.getContent().stream().limit(MAXIMO_RESULTADOS)
                .map(grupo -> new OpcionAutocompletado(grupo.getId(),
                        grupo.getNombre() + " · " + grupo.getGrado().getNombre(),
                        grupo.getPlantel().getNombre() + " · " + grupo.getCicloEscolar().getNombre()))
                .toList(), hayMas);
    }

    public ResultadoAutocompletado periodosCalificacion(Long grupoId, String consulta) {
        alcance.validarRecurso(ModuloCatalogo.GRUPOS, grupoId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        var resultado = periodoAcademicoRepository.buscarParaCalificaciones(
                grupoId, texto, PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(periodo -> new OpcionAutocompletado(periodo.getId(),
                        periodo.getCodigo() + " · " + periodo.getNombre(),
                        periodo.getFechaInicio() + " — " + periodo.getFechaFin()))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado materiasCalificacion(Long grupoId, String consulta) {
        alcance.validarRecurso(ModuloCatalogo.GRUPOS, grupoId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        var resultado = materiaGradoRepository.buscarParaCalificaciones(
                grupoId, texto, PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(plan -> new OpcionAutocompletado(plan.getId(),
                        plan.getMateria().getCodigo() + " · " + plan.getMateria().getNombre(),
                        plan.getTipoEvaluacion().getEtiqueta()))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado materiasMaestro(Long grupoId, String consulta) {
        alcance.validarRecurso(ModuloCatalogo.GRUPOS, grupoId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        var resultado = materiaGradoRepository.buscarParaCalificaciones(
                grupoId, texto, PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(plan -> new OpcionAutocompletado(plan.getMateria().getId(),
                        plan.getMateria().getCodigo() + " · " + plan.getMateria().getNombre(),
                        plan.getGrado().getNombre() + " · " + plan.getTipoEvaluacion().getEtiqueta()))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado tiposBeca(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<TipoBeca> resultado = tipoBecaRepository.buscarParaAutocompletado(
                institucionId, texto, PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(tipo -> new OpcionAutocompletado(tipo.getId(),
                        tipo.getCodigo() + " · " + tipo.getNombre(),
                        detalle(tipo.getDescripcion(), null))).toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado cuentasParaPago(Long institucionId, Long plantelId,
                                                   MetodoPago metodo, String consulta) {
        alcance.validarInstitucion(institucionId);
        alcance.validarPlantel(plantelId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<CuentaFinanciera> resultado = cuentaFinancieraRepository.buscarParaPago(
                institucionId, plantelId, metodo == MetodoPago.EFECTIVO, texto,
                PageRequest.of(0, tamano(consulta)));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(cuenta -> new OpcionAutocompletado(cuenta.getId(),
                        cuenta.getCodigo() + " · " + cuenta.getNombre(),
                        cuenta.getTipo().name() + " · " + identificadorCuenta(cuenta)))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado cuentasParaMovimientos(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        String patron = "%" + texto + "%";
        Specification<CuentaFinanciera> busqueda = (root, query, cb) -> cb.and(
                cb.equal(root.get("institucion").get("id"), institucionId),
                cb.or(cb.like(cb.lower(root.get("codigo")), patron),
                        cb.like(cb.lower(root.get("nombre")), patron),
                        cb.like(cb.lower(root.get("bancoNombre")), patron)));
        var resultado = cuentaFinancieraRepository.findAll(Specification.where(busqueda)
                        .and(alcance.especificacion(ModuloCatalogo.CUENTAS_FINANCIERAS)),
                PageRequest.of(0, tamano(consulta) + 1));
        boolean hayMas = resultado.getNumberOfElements() > MAXIMO_RESULTADOS;
        return new ResultadoAutocompletado(resultado.getContent().stream().limit(MAXIMO_RESULTADOS)
                .map(cuenta -> new OpcionAutocompletado(cuenta.getId(),
                        cuenta.getCodigo() + " · " + cuenta.getNombre(),
                        cuenta.getTipo().name() + " · " + identificadorCuenta(cuenta)
                                + (cuenta.isActivo() ? "" : " · Inactiva")))
                .toList(), hayMas);
    }

    public ResultadoAutocompletado cuentasParaCortes(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        String patron = "%" + texto + "%";
        Specification<CuentaFinanciera> busqueda = (root, query, cb) -> cb.and(
                cb.equal(root.get("institucion").get("id"), institucionId),
                cb.equal(root.get("tipo"), TipoCuentaFinanciera.CAJA),
                cb.isTrue(root.get("activo")),
                cb.or(cb.like(cb.lower(root.get("codigo")), patron),
                        cb.like(cb.lower(root.get("nombre")), patron)));
        var resultado = cuentaFinancieraRepository.findAll(Specification.where(busqueda)
                        .and(alcance.especificacionCuentasParaCorte()),
                PageRequest.of(0, tamano(consulta) + 1));
        boolean hayMas = resultado.getNumberOfElements() > MAXIMO_RESULTADOS;
        return new ResultadoAutocompletado(resultado.getContent().stream().limit(MAXIMO_RESULTADOS)
                .map(cuenta -> new OpcionAutocompletado(cuenta.getId(),
                        cuenta.getCodigo() + " · " + cuenta.getNombre(),
                        "CAJA · " + identificadorCuenta(cuenta)))
                .toList(), hayMas);
    }

    public ResultadoAutocompletado cuentasParaReportes(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        String patron = "%" + texto + "%";
        Specification<CuentaFinanciera> busqueda = (root, query, cb) -> cb.and(
                cb.equal(root.get("institucion").get("id"), institucionId),
                cb.or(cb.like(cb.lower(root.get("codigo")), patron),
                        cb.like(cb.lower(root.get("nombre")), patron),
                        cb.like(cb.lower(root.get("bancoNombre")), patron)));
        var resultado = cuentaFinancieraRepository.findAll(Specification.where(busqueda)
                        .and(alcance.especificacionCuentasReporte()),
                PageRequest.of(0, tamano(consulta) + 1));
        boolean hayMas = resultado.getNumberOfElements() > MAXIMO_RESULTADOS;
        return new ResultadoAutocompletado(resultado.getContent().stream().limit(MAXIMO_RESULTADOS)
                .map(cuenta -> new OpcionAutocompletado(cuenta.getId(),
                        cuenta.getCodigo() + " · " + cuenta.getNombre(),
                        cuenta.getTipo().name() + " · " + identificadorCuenta(cuenta)
                                + (cuenta.isActivo() ? "" : " · Inactiva")))
                .toList(), hayMas);
    }

    public ResultadoAutocompletado cargosParaPago(Long institucionId, Long tutorId, String consulta) {
        alcance.validarInstitucion(institucionId);
        alcance.validarRecurso(ModuloCatalogo.TUTORES, tutorId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<Cargo> resultado = cargoRepository.buscarParaSolicitudPago(
                institucionId, tutorId, texto, PageRequest.of(0, tamano(consulta)));
        var permitidos = resultado.getContent().stream().filter(cargo -> {
            try {
                alcance.validarRecurso(ModuloCatalogo.CARGOS, cargo.getId());
                return saldo(cargo).signum() > 0;
            } catch (AccessDeniedException excepcion) {
                return false;
            }
        }).map(cargo -> new OpcionAutocompletado(cargo.getId(),
                cargo.getInscripcion().getAlumno().getMatricula() + " · "
                        + nombre(cargo.getInscripcion().getAlumno().getNombres(),
                        cargo.getInscripcion().getAlumno().getPrimerApellido(),
                        cargo.getInscripcion().getAlumno().getSegundoApellido()) + " · "
                        + cargo.getConceptoCobro().getNombre(),
                cargo.getDescripcion() + " · Saldo actual " + formatear(saldo(cargo)),
                saldo(cargo))).toList();
        return new ResultadoAutocompletado(permitidos, resultado.hasNext());
    }

    private String identificadorCuenta(CuentaFinanciera cuenta) {
        String valor = cuenta.getClabe() != null ? cuenta.getClabe() : cuenta.getNumeroCuenta();
        if (valor == null) return cuenta.getPlantel() == null ? "Institucional" : cuenta.getPlantel().getNombre();
        return "•••• " + valor.substring(Math.max(0, valor.length() - 4));
    }

    private String normalizar(String consulta) {
        if (consulta == null) return null;
        String texto = consulta.trim().replaceAll("\\s+", " ");
        if (texto.isBlank() || "__INICIALES__".equalsIgnoreCase(texto)) return "";
        if (texto.length() < MINIMO_CARACTERES) return null;
        return texto.substring(0, Math.min(texto.length(), 100)).toLowerCase(Locale.ROOT);
    }

    private int tamano(String consulta) {
        return consulta == null || consulta.isBlank() || "__INICIALES__".equalsIgnoreCase(consulta.trim()) ? 10 : MAXIMO_RESULTADOS;
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
