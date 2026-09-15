package escuela.cobranza.service.impl;

import escuela.academico.entity.PeriodoAcademico;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.cobranza.dto.request.CargoManualRequest;
import escuela.cobranza.dto.request.GeneracionCargosRequest;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.dto.response.GeneracionCargosResponse;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.CuotaAlumno;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.EnumSet;
import java.util.Locale;
import java.util.UUID;

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
                .orElseThrow(() -> new RecursoNoEncontradoException("el periodo académico",
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
    public CargoResponse obtener(Long id) {
        return mapper.respuesta(repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", id)));
    }

    @Override
    public CargoResponse cancelar(Long id, Long version, String motivo) {
        Cargo cargo = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", id));
        verificar(cargo, version, "Cargo");
        if (cargo.getEstadoRegistro() == EstadoRegistroCargo.CANCELADO) {
            throw new ReglaNegocioException("El cargo ya se encuentra cancelado");
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
        LocalDate fin = menor(fechaCorte, cuota.getFechaFin());
        if (fin.isBefore(cuota.getFechaInicio())) return new ResultadoGeneracion(0, 0);
        if (cuota.getFrecuencia() == FrecuenciaCuota.UNICA) {
            return insertar(cuota, cuota.getFechaInicio(), cuota.getFechaFin(),
                    cuota.getFechaVencimientoUnico(), "UNICA", cuota.getConceptoCobro().getNombre());
        }
        int generados = 0;
        int existentes = 0;
        YearMonth mes = YearMonth.from(cuota.getFechaInicio());
        YearMonth ultimoMes = YearMonth.from(fin);
        while (!mes.isAfter(ultimoMes)) {
            LocalDate inicioPeriodo = mayor(mes.atDay(1), cuota.getFechaInicio());
            LocalDate finPeriodo = menor(mes.atEndOfMonth(), cuota.getFechaFin());
            LocalDate vencimiento = mes.atDay(Math.min(cuota.getDiaVencimiento(), mes.lengthOfMonth()));
            vencimiento = mayor(inicioPeriodo, menor(finPeriodo, vencimiento));
            String etiqueta = mes.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "MX"))
                    + " " + mes.getYear();
            ResultadoGeneracion resultado = insertar(cuota, inicioPeriodo, finPeriodo,
                    vencimiento, mes.toString(), cuota.getConceptoCobro().getNombre() + " · " + etiqueta);
            generados += resultado.generados();
            existentes += resultado.existentes();
            mes = mes.plusMonths(1);
        }
        return new ResultadoGeneracion(generados, existentes);
    }

    private ResultadoGeneracion insertar(CuotaAlumno cuota, LocalDate inicio, LocalDate fin,
                                         LocalDate vencimiento, String periodoClave,
                                         String descripcion) {
        Long institucionId = cuota.getInscripcion().getAlumno().getInstitucion().getId();
        String clave = "AUTO:" + institucionId + ":" + cuota.getId() + ":" + periodoClave;
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
            throw new ReglaNegocioException("El periodo académico no corresponde a la inscripción");
        }
        if (request.periodoCobroInicio().isBefore(periodo.getFechaInicio())
                || request.periodoCobroFin().isAfter(periodo.getFechaFin())) {
            throw new ReglaNegocioException("El periodo de cobro debe quedar dentro del periodo académico");
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
}
