package escuela.cobranza.service.impl;

import escuela.academico.entity.PeriodoAcademico;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.cobranza.dto.request.CargoManualRequest;
import escuela.cobranza.dto.request.GeneracionCargosRequest;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.dto.response.GeneracionCargosResponse;
import escuela.cobranza.dto.response.VistaPreviaCargoAutomaticoFila;
import escuela.cobranza.dto.response.VistaPreviaCargosAutomaticosResponse;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.CuotaAlumno;
import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.entity.EstadoRegistroCargo;
import escuela.cobranza.entity.FrecuenciaCuota;
import escuela.cobranza.mapper.CargoMapper;
import escuela.cobranza.repository.CargoRepository;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.cobranza.service.CargoService;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.EnumSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;
import static escuela.cobranza.support.CalculoCargo.aplicado;

@Service
@RequiredArgsConstructor
@Transactional
public class CargoServiceImpl implements CargoService {

    private static final int TAMANO_BLOQUE = 100;
    private static final EnumSet<EstadoInscripcion> INSCRIPCIONES_VIGENTES =
            EnumSet.of(EstadoInscripcion.PREINSCRITA, EstadoInscripcion.ACTIVA);

    private final CargoRepository repository;
    private final CuotaAlumnoRepository cuotaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ConceptoCobroRepository conceptoRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final CargoMapper mapper;
    private final AplicacionBecaCargoService aplicacionBecaService;

    @Override
    public CargoResponse crearManual(CargoManualRequest request) {
        Inscripcion inscripcion = inscripcionRepository.findByIdForUpdate(request.inscripcionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la inscripción", request.inscripcionId()));
        ConceptoCobro concepto = conceptoRepository.findById(request.conceptoCobroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el concepto de cobro",
                        request.conceptoCobroId()));
        PeriodoAcademico periodo = request.periodoAcademicoId() == null ? null
                : periodoRepository.findById(request.periodoAcademicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el periodo de evaluación",
                        request.periodoAcademicoId()));
        validarManual(request, inscripcion, concepto, periodo);
        String clave = "MANUAL:" + inscripcion.getAlumno().getInstitucion().getId()
                + ":" + UUID.randomUUID();
        Cargo cargo = mapper.nuevoManual(request, inscripcion, concepto, periodo, clave);
        cargo.setImporteOriginal(request.importeOriginal().setScale(2, RoundingMode.UNNECESSARY));
        cargo = repository.saveAndFlush(cargo);
        aplicacionBecaService.aplicar(cargo);
        return mapper.respuesta(cargo);
    }

    @Override
    public GeneracionCargosResponse generar(GeneracionCargosRequest request) {
        int cuotasRevisadas = 0;
        int generados = 0;
        int existentes = 0;
        long ultimoId = 0L;
        while (true) {
            var bloque = cuotaRepository.buscarParaGeneracion(request.institucionId(),
                    request.plantelId(), request.fechaCorte(), ultimoId,
                    PageRequest.of(0, TAMANO_BLOQUE));
            if (bloque.isEmpty()) break;
            for (CuotaAlumno cuota : bloque.getContent()) {
                cuotasRevisadas++;
                ResultadoGeneracion resultado = generarCuota(cuota, request.fechaCorte());
                generados += resultado.generados();
                existentes += resultado.existentes();
                ultimoId = cuota.getId();
            }
            if (bloque.getNumberOfElements() < TAMANO_BLOQUE) break;
        }
        return new GeneracionCargosResponse(cuotasRevisadas, generados, existentes);
    }

    @Override
    @Transactional(readOnly = true)
    public VistaPreviaCargosAutomaticosResponse previsualizar(GeneracionCargosRequest request,
                                                               int numeroPagina, int tamanioPagina) {
        int pagina = Math.max(numeroPagina, 0);
        int tamanio = Math.min(Math.max(tamanioPagina, 10), 100);
        long desde = (long) pagina * tamanio;
        long cuotasConPendientes = 0;
        long pagosPorGenerar = 0;
        BigDecimal importeTotal = BigDecimal.ZERO;
        BigDecimal becaTotal = BigDecimal.ZERO;
        BigDecimal importeNetoTotal = BigDecimal.ZERO;
        List<VistaPreviaCargoAutomaticoFila> contenido = new ArrayList<>();
        long ultimoId = 0L;
        while (true) {
            var bloque = cuotaRepository.buscarParaGeneracion(request.institucionId(),
                    request.plantelId(), request.fechaCorte(), ultimoId,
                    PageRequest.of(0, TAMANO_BLOQUE));
            if (bloque.isEmpty()) break;
            for (CuotaAlumno cuota : bloque.getContent()) {
                ultimoId = cuota.getId();
                PlanCuota plan = planificar(cuota, request.fechaCorte());
                if (plan.pendientes().isEmpty()) continue;
                cuotasConPendientes++;
                for (PeriodoCargo periodo : plan.pendientes()) {
                    var fila = fila(cuota, periodo);
                    if (pagosPorGenerar >= desde && contenido.size() < tamanio)
                        contenido.add(fila);
                    pagosPorGenerar++;
                    importeTotal = importeTotal.add(fila.importe());
                    becaTotal = becaTotal.add(fila.montoBeca());
                    importeNetoTotal = importeNetoTotal.add(fila.importeNeto());
                }
            }
            if (bloque.getNumberOfElements() < TAMANO_BLOQUE) break;
        }
        return new VistaPreviaCargosAutomaticosResponse(
                new PageImpl<>(contenido, PageRequest.of(pagina, tamanio), pagosPorGenerar),
                cuotasConPendientes, pagosPorGenerar,
                importeTotal.setScale(2, RoundingMode.HALF_UP), becaTotal,
                importeNetoTotal);
    }

    @Override
    public CargoResponse generarCargoUnico(Long cuotaId) {
        CuotaAlumno cuota = cuotaRepository.findByIdForUpdate(cuotaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuota del alumno", cuotaId));
        if (cuota.getFrecuencia() != FrecuenciaCuota.UNICA) {
            throw new ReglaNegocioException("La generación inmediata sólo está disponible para cobros únicos");
        }
        if (cuota.getEstado() != EstadoCuota.ACTIVA || !cuota.getConceptoCobro().isActivo()
                || !INSCRIPCIONES_VIGENTES.contains(cuota.getInscripcion().getEstado())) {
            throw new ReglaNegocioException("El cargo requiere una cuota, concepto e inscripción vigentes");
        }
        insertar(cuota, cuota.getFechaInicio(), cuota.getFechaFin(),
                cuota.getFechaVencimientoUnico(), "UNICA", cuota.getConceptoCobro().getNombre());
        return mapper.respuesta(repository.findByClaveGeneracion(clave(cuota, "UNICA"))
                .orElseThrow(() -> new ReglaNegocioException("No fue posible localizar el cargo generado")));
    }

    @Override
    @Transactional(readOnly = true)
    public CargoResponse obtener(Long id) {
        return mapper.respuesta(repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", id)));
    }

    @Override
    public CargoResponse cancelar(Long id, Long version, String motivo) {
        Cargo cargo = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", id));
        verificar(cargo, version, "Cargo");
        if (cargo.getEstadoRegistro() != EstadoRegistroCargo.EMITIDO) {
            throw new ReglaNegocioException(cargo.getEstadoRegistro() == EstadoRegistroCargo.CONVENIDO
                    ? "El cargo fue sustituido por un convenio y no puede cancelarse por separado"
                    : "El cargo ya se encuentra cancelado");
        }
        if (aplicado(cargo).signum() > 0) {
            throw new ReglaNegocioException("No se puede cancelar un cargo con pagos aplicados; primero deben reversarse sus aplicaciones");
        }
        String motivoLimpio = limpiar(motivo);
        if (motivoLimpio == null || motivoLimpio.length() > 2000) {
            throw new ReglaNegocioException("Indica un motivo de cancelación de máximo 2000 caracteres");
        }
        cargo.setEstadoRegistro(EstadoRegistroCargo.CANCELADO);
        cargo.setCanceladoEn(Instant.now());
        cargo.setMotivoCancelacion(motivoLimpio);
        return mapper.respuesta(repository.saveAndFlush(cargo));
    }

    private ResultadoGeneracion generarCuota(CuotaAlumno cuota, LocalDate fechaCorte) {
        PlanCuota plan = planificar(cuota, fechaCorte);
        int generados = 0;
        int existentes = plan.existentes();
        for (PeriodoCargo periodo : plan.pendientes()) {
            ResultadoGeneracion resultado = insertar(cuota, periodo.inicio(), periodo.fin(),
                    periodo.vencimiento(), periodo.clavePeriodo(), periodo.descripcion());
            generados += resultado.generados();
            existentes += resultado.existentes();
        }
        return new ResultadoGeneracion(generados, existentes);
    }

    private PlanCuota planificar(CuotaAlumno cuota, LocalDate fechaCorte) {
        LocalDate fin = menor(fechaCorte, cuota.getFechaFin());
        if (fin.isBefore(cuota.getFechaInicio())) return new PlanCuota(List.of(), 0);
        List<PeriodoCargo> candidatos = new ArrayList<>();
        if (cuota.getFrecuencia() == FrecuenciaCuota.UNICA) {
            candidatos.add(new PeriodoCargo(cuota.getFechaInicio(), cuota.getFechaFin(),
                    cuota.getFechaVencimientoUnico(), "UNICA", cuota.getConceptoCobro().getNombre()));
        } else {
            YearMonth mes = YearMonth.from(cuota.getFechaInicio());
            YearMonth ultimoMes = YearMonth.from(fin);
            while (!mes.isAfter(ultimoMes)) {
                LocalDate inicioPeriodo = mayor(mes.atDay(1), cuota.getFechaInicio());
                LocalDate finPeriodo = menor(mes.atEndOfMonth(), cuota.getFechaFin());
                LocalDate vencimiento = mes.atDay(Math.min(cuota.getDiaVencimiento(), mes.lengthOfMonth()));
                vencimiento = mayor(inicioPeriodo, menor(finPeriodo, vencimiento));
                String etiqueta = mes.getMonth().getDisplayName(TextStyle.FULL,
                        new Locale("es", "MX")) + " " + mes.getYear();
                candidatos.add(new PeriodoCargo(inicioPeriodo, finPeriodo, vencimiento,
                        mes.toString(), cuota.getConceptoCobro().getNombre() + " · " + etiqueta));
                mes = mes.plusMonths(1);
            }
        }
        Set<String> existentes = new HashSet<>(repository.clavesGeneradasPorCuota(cuota.getId()));
        List<PeriodoCargo> pendientes = candidatos.stream()
                .filter(periodo -> !existentes.contains(clave(cuota, periodo.clavePeriodo())))
                .toList();
        return new PlanCuota(pendientes, candidatos.size() - pendientes.size());
    }

    private VistaPreviaCargoAutomaticoFila fila(CuotaAlumno cuota, PeriodoCargo periodo) {
        var alumno = cuota.getInscripcion().getAlumno();
        String nombre = Stream.of(alumno.getNombres(), alumno.getPrimerApellido(),
                        alumno.getSegundoApellido()).filter(v -> v != null && !v.isBlank())
                .collect(Collectors.joining(" "));
        String frecuencia = cuota.getFrecuencia() == FrecuenciaCuota.UNICA
                ? "Cobro único" : "Mensual";
        BigDecimal importe = cuota.getImporteBase().setScale(2, RoundingMode.HALF_UP);
        var beca = aplicacionBecaService.previsualizar(cuota.getInscripcion().getId(),
                cuota.getConceptoCobro().getId(), periodo.inicio(), periodo.fin(), importe);
        return new VistaPreviaCargoAutomaticoFila(cuota.getId(), alumno.getMatricula(), nombre,
                cuota.getInscripcion().getPlantel().getNombre(), cuota.getConceptoCobro().getNombre(),
                frecuencia, periodo.descripcion(), periodo.vencimiento(),
                importe, cuota.getMoneda(), beca.monto(), beca.descripcion(),
                importe.subtract(beca.monto()));
    }

    private ResultadoGeneracion insertar(CuotaAlumno cuota, LocalDate inicio, LocalDate fin,
                                         LocalDate vencimiento, String periodoClave,
                                         String descripcion) {
        String clave = clave(cuota, periodoClave);
        int insertados = repository.insertarAutomaticoSiAusente(cuota.getInscripcion().getId(),
                cuota.getConceptoCobro().getId(), cuota.getId(), clave, descripcion,
                inicio, fin, LocalDate.now(), vencimiento,
                cuota.getImporteBase().setScale(2, RoundingMode.UNNECESSARY),
                codigo(cuota.getMoneda()), actorActual());
        if (insertados == 1) {
            repository.findByClaveGeneracion(clave).ifPresent(aplicacionBecaService::aplicar);
        }
        return insertados == 1 ? new ResultadoGeneracion(1, 0) : new ResultadoGeneracion(0, 1);
    }

    private String clave(CuotaAlumno cuota, String periodoClave) {
        Long institucionId = cuota.getInscripcion().getAlumno().getInstitucion().getId();
        return "AUTO:" + institucionId + ":" + cuota.getId() + ":" + periodoClave;
    }

    private void validarManual(CargoManualRequest request, Inscripcion inscripcion,
                               ConceptoCobro concepto, PeriodoAcademico periodo) {
        Long institucionId = inscripcion.getAlumno().getInstitucion().getId();
        if (!institucionId.equals(concepto.getInstitucion().getId())) {
            throw new ReglaNegocioException("La inscripción y el concepto deben pertenecer a la misma institución");
        }
        if (!INSCRIPCIONES_VIGENTES.contains(inscripcion.getEstado()) || !concepto.isActivo()) {
            throw new ReglaNegocioException("El cargo requiere una inscripción y un concepto vigentes");
        }
        if (request.periodoCobroFin().isBefore(request.periodoCobroInicio())) {
            throw new ReglaNegocioException("El fin del periodo de cobro no puede ser anterior al inicio");
        }
        if (request.periodoCobroInicio().isBefore(inscripcion.getFechaInicio())
                || request.periodoCobroFin().isAfter(inscripcion.getCicloEscolar().getFechaFin())
                || inscripcion.getFechaFin() != null
                && request.periodoCobroFin().isAfter(inscripcion.getFechaFin())) {
            throw new ReglaNegocioException("El periodo de cobro debe quedar dentro de la inscripción");
        }
        if (request.fechaVencimiento().isBefore(request.fechaEmision())) {
            throw new ReglaNegocioException("El vencimiento no puede ser anterior a la emisión");
        }
        LocalDate hoyInstitucion = LocalDate.now(ZoneId.of(
                inscripcion.getAlumno().getInstitucion().getZonaHoraria()));
        if (request.fechaEmision().isAfter(hoyInstitucion)) {
            throw new ReglaNegocioException("La fecha de registro del cargo no puede estar en el futuro");
        }
        if (request.fechaEmision().isBefore(hoyInstitucion)
                && (request.motivoFechaRegistroDiferente() == null
                || request.motivoFechaRegistroDiferente().isBlank())) {
            throw new ReglaNegocioException(
                    "Explica por qué el cargo se registra con una fecha diferente a la actual");
        }
        if (request.importeOriginal().signum() < 0 || request.importeOriginal().scale() > 2) {
            throw new ReglaNegocioException("El importe debe ser positivo o cero y tener máximo dos decimales");
        }
        String moneda = codigo(request.moneda());
        if (!moneda.equals(inscripcion.getAlumno().getInstitucion().getMonedaPredeterminada())) {
            throw new ReglaNegocioException("La moneda debe coincidir con la moneda de la institución");
        }
        if (periodo != null) validarPeriodo(periodo, inscripcion, request);
    }

    private void validarPeriodo(PeriodoAcademico periodo, Inscripcion inscripcion,
                                CargoManualRequest request) {
        if (!periodo.getCicloEscolar().getId().equals(inscripcion.getCicloEscolar().getId())
                || !periodo.getNivelEducativo().getId()
                .equals(inscripcion.getGrado().getNivelEducativo().getId())) {
            throw new ReglaNegocioException("El periodo de evaluación no corresponde a la inscripción");
        }
        if (request.periodoCobroInicio().isBefore(periodo.getFechaInicio())
                || request.periodoCobroFin().isAfter(periodo.getFechaFin())) {
            throw new ReglaNegocioException("El periodo de cobro debe quedar dentro del periodo de evaluación");
        }
    }

    private Long actorActual() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        return autenticacion != null && autenticacion.getPrincipal() instanceof UsuarioPrincipal principal
                ? principal.usuarioId() : null;
    }

    private LocalDate menor(LocalDate uno, LocalDate dos) {
        return uno.isBefore(dos) ? uno : dos;
    }

    private LocalDate mayor(LocalDate uno, LocalDate dos) {
        return uno.isAfter(dos) ? uno : dos;
    }

    private record ResultadoGeneracion(int generados, int existentes) {
    }

    private record PeriodoCargo(LocalDate inicio, LocalDate fin, LocalDate vencimiento,
                                String clavePeriodo, String descripcion) { }

    private record PlanCuota(List<PeriodoCargo> pendientes, int existentes) { }
}
