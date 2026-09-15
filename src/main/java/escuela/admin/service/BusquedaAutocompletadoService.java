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
import escuela.finanzas.repository.CuentaFinancieraRepository;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.stream.Stream;
import static escuela.cobranza.support.CalculoCargo.saldo;

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
    private final TipoBecaRepository tipoBecaRepository;
    private final CargoRepository cargoRepository;
    private final CuentaFinancieraRepository cuentaFinancieraRepository;
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

    public ResultadoAutocompletado tutoresParaPago(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<Tutor> resultado = tutorRepository.buscarParaAutocompletado(
                institucionId, texto, PageRequest.of(0, MAXIMO_RESULTADOS));
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

    public ResultadoAutocompletado periodosCargo(Long inscripcionId, String consulta) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, inscripcionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<PeriodoAcademico> resultado = periodoAcademicoRepository.buscarParaCargo(
                inscripcionId, texto, PageRequest.of(0, MAXIMO_RESULTADOS));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(periodo -> new OpcionAutocompletado(periodo.getId(),
                        periodo.getCodigo() + " · " + periodo.getNombre(),
                        periodo.getFechaInicio() + " — " + periodo.getFechaFin()))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado tiposBeca(Long institucionId, String consulta) {
        alcance.validarInstitucion(institucionId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<TipoBeca> resultado = tipoBecaRepository.buscarParaAutocompletado(
                institucionId, texto, PageRequest.of(0, MAXIMO_RESULTADOS));
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
                PageRequest.of(0, MAXIMO_RESULTADOS));
        return new ResultadoAutocompletado(resultado.getContent().stream()
                .map(cuenta -> new OpcionAutocompletado(cuenta.getId(),
                        cuenta.getCodigo() + " · " + cuenta.getNombre(),
                        cuenta.getTipo().name() + " · " + identificadorCuenta(cuenta)))
                .toList(), resultado.hasNext());
    }

    public ResultadoAutocompletado cargosParaPago(Long institucionId, Long tutorId, String consulta) {
        alcance.validarInstitucion(institucionId);
        alcance.validarRecurso(ModuloCatalogo.TUTORES, tutorId);
        String texto = normalizar(consulta);
        if (texto == null) return ResultadoAutocompletado.vacio();
        Slice<Cargo> resultado = cargoRepository.buscarParaSolicitudPago(
                institucionId, tutorId, texto, PageRequest.of(0, MAXIMO_RESULTADOS));
        var permitidos = resultado.getContent().stream().filter(cargo -> {
            try {
                alcance.validarRecurso(ModuloCatalogo.CARGOS, cargo.getId());
                return true;
            } catch (AccessDeniedException excepcion) {
                return false;
            }
        }).map(cargo -> new OpcionAutocompletado(cargo.getId(),
                cargo.getInscripcion().getAlumno().getMatricula() + " · "
                        + nombre(cargo.getInscripcion().getAlumno().getNombres(),
                        cargo.getInscripcion().getAlumno().getPrimerApellido(),
                        cargo.getInscripcion().getAlumno().getSegundoApellido()) + " · "
                        + cargo.getConceptoCobro().getNombre(),
                cargo.getDescripcion() + " · Saldo actual " + saldo(cargo).toPlainString()
                        + " " + cargo.getMoneda())).toList();
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
