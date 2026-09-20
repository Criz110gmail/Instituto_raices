package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.admin.repository.ReporteFinancieroRepository;
import escuela.alumno.repository.AlumnoRepository;
import escuela.common.exception.*;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteFinancieroConsultaService {
    private final ReporteFinancieroRepository repository;
    private final AlumnoRepository alumnoRepository;
    private final CuentaFinancieraRepository cuentaRepository;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    public FiltroEstadoCuentaAlumno normalizar(FiltroEstadoCuentaAlumno filtro) {
        InstitucionResponse institucion = institucion(filtro.institucionId());
        return filtro.normalizado(LocalDate.now(ZoneId.of(institucion.zonaHoraria())));
    }

    public FiltroReporteTesoreria normalizar(FiltroReporteTesoreria filtro) {
        InstitucionResponse institucion = institucion(filtro.institucionId());
        return filtro.normalizado(LocalDate.now(ZoneId.of(institucion.zonaHoraria())));
    }

    public ResultadoEstadoCuentaAlumno estadoCuenta(FiltroEstadoCuentaAlumno original) {
        FiltroEstadoCuentaAlumno filtro = normalizar(original);
        InstitucionResponse institucion = institucion(filtro.institucionId());
        validarPlantel(filtro.institucionId(), filtro.plantelId());
        if (filtro.alumnoId() == null) {
            return new ResultadoEstadoCuentaAlumno(null, null, null, Page.empty(),
                    ResumenEstadoCuenta.vacio(institucion.monedaPredeterminada()));
        }
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, filtro.alumnoId());
        var alumno = alumnoRepository.findById(filtro.alumnoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", filtro.alumnoId()));
        if (!alumno.getInstitucion().getId().equals(filtro.institucionId()))
            throw new ReglaNegocioException("El alumno no pertenece a la institución seleccionada");
        ZoneId zona = ZoneId.of(institucion.zonaHoraria());
        Instant corte = filtro.fechaCorte().plusDays(1).atStartOfDay(zona).toInstant();
        AlcanceReporteFinanciero seguridad = seguridad(filtro.institucionId());
        return new ResultadoEstadoCuentaAlumno(alumno.getId(), nombre(alumno.getNombres(),
                alumno.getPrimerApellido(), alumno.getSegundoApellido()), alumno.getMatricula(),
                repository.estadoCuenta(filtro, seguridad, corte),
                repository.resumenEstadoCuenta(filtro, seguridad, corte,
                        institucion.monedaPredeterminada()));
    }

    public ResultadoReporteTesoreria tesoreria(FiltroReporteTesoreria original) {
        FiltroReporteTesoreria filtro = normalizar(original);
        if (filtro.fechaDesde().isAfter(filtro.fechaHasta()))
            throw new ReglaNegocioException("La fecha inicial no puede ser posterior a la fecha final");
        InstitucionResponse institucion = institucion(filtro.institucionId());
        validarPlantel(filtro.institucionId(), filtro.plantelId());
        AlcanceReporteFinanciero seguridad = seguridad(filtro.institucionId());
        validarCuenta(filtro, seguridad);
        ZoneId zona = ZoneId.of(institucion.zonaHoraria());
        Instant desde = filtro.fechaDesde().atStartOfDay(zona).toInstant();
        Instant hasta = filtro.fechaHasta().plusDays(1).atStartOfDay(zona).toInstant();
        return new ResultadoReporteTesoreria(
                repository.tesoreria(filtro, seguridad, desde, hasta, institucion.zonaHoraria()),
                repository.resumenTesoreria(filtro, seguridad, desde, hasta,
                        institucion.monedaPredeterminada()));
    }

    private InstitucionResponse institucion(Long id) {
        if (id == null) throw new ReglaNegocioException("Selecciona una institución para consultar el reporte");
        alcance.validarInstitucion(id);
        return institucionService.obtener(id);
    }

    private void validarPlantel(Long institucionId, Long plantelId) {
        if (plantelId == null) return;
        alcance.validarPlantel(plantelId);
        if (!plantelService.obtener(plantelId).institucionId().equals(institucionId))
            throw new ReglaNegocioException("El plantel no pertenece a la institución seleccionada");
    }

    private void validarCuenta(FiltroReporteTesoreria filtro, AlcanceReporteFinanciero seguridad) {
        if (filtro.cuentaId() == null) return;
        var cuenta = cuentaRepository.findById(filtro.cuentaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuenta financiera", filtro.cuentaId()));
        if (!cuenta.getInstitucion().getId().equals(filtro.institucionId()))
            throw new ReglaNegocioException("La cuenta no pertenece a la institución seleccionada");
        if (!seguridad.institucional() && (cuenta.getPlantel() == null
                || !seguridad.plantelIds().contains(cuenta.getPlantel().getId())))
            throw new org.springframework.security.access.AccessDeniedException(
                    "La cuenta no está disponible para el alcance de la sesión");
    }

    private AlcanceReporteFinanciero seguridad(Long institucionId) {
        return new AlcanceReporteFinanciero(alcance.alcanceInstitucionalActual(institucionId),
                alcance.plantelesActuales(institucionId));
    }

    private String nombre(String... partes) {
        return Stream.of(partes).filter(v -> v != null && !v.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
