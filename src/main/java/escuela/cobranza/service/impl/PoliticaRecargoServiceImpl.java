package escuela.cobranza.service.impl;

import escuela.cobranza.dto.request.*;
import escuela.cobranza.dto.response.*;
import escuela.cobranza.entity.*;
import escuela.cobranza.mapper.PoliticaRecargoMapper;
import escuela.cobranza.repository.*;
import escuela.cobranza.service.PoliticaRecargoService;
import escuela.cobranza.service.SeleccionGeneracionService;
import escuela.common.exception.*;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.*;

import static escuela.cobranza.support.CalculoCargo.saldo;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class PoliticaRecargoServiceImpl implements PoliticaRecargoService {
    private static final int BLOQUE = 100;
    private final PoliticaRecargoRepository repository;
    private final ConceptoCobroRepository conceptoRepository;
    private final CargoRepository cargoRepository;
    private final AjusteCargoRepository ajusteRepository;
    private final PoliticaRecargoMapper mapper;
    private final SeleccionGeneracionService selecciones;

    public PoliticaRecargoResponse crear(PoliticaRecargoRequest request) {
        ConceptoCobro concepto = conceptoRepository.findById(request.conceptoCobroId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "el concepto de cobro", request.conceptoCobroId()));
        validar(request, concepto, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nueva(normalizar(request), concepto)));
    }

    public PoliticaRecargoResponse actualizar(Long id, PoliticaRecargoRequest request) {
        PoliticaRecargo politica = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la política de recargo", id));
        verificar(politica, request.version(), "Política de recargo");
        if (!politica.getConceptoCobro().getId().equals(request.conceptoCobroId()))
            throw new ReglaNegocioException("No se puede cambiar el concepto de una política histórica");
        validar(request, politica.getConceptoCobro(), id);
        mapper.actualizar(politica, normalizar(request));
        return mapper.respuesta(repository.saveAndFlush(politica));
    }

    @Transactional(readOnly = true)
    public PoliticaRecargoResponse obtener(Long id) {
        return mapper.respuesta(repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la política de recargo", id)));
    }

    public void desactivar(Long id, Long version) {
        PoliticaRecargo politica = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la política de recargo", id));
        verificar(politica, version, "Política de recargo");
        politica.setActivo(false);
        politica.setGeneracionAutomatica(false);
    }

    @Transactional
    public VistaPreviaRecargosResponse previsualizar(GeneracionRecargosRequest request,
                                                      int numeroPagina, int tamanioPagina) {
        var seleccion=selecciones.preparar(request.seleccionId(),"RECARGOS",request.institucionId(),request.plantelId(),request.fechaCorte());
        var captura=new ArrayList<SeleccionGeneracionService.Item>();
        int pagina = Math.max(numeroPagina, 0);
        int tamanio = Math.min(Math.max(tamanioPagina, 10), 100);
        long desde = (long) pagina * tamanio;
        long aplicables = 0;
        long recargos = 0;
        BigDecimal saldoActual = BigDecimal.ZERO;
        BigDecimal totalRecargos = BigDecimal.ZERO;
        List<VistaPreviaRecargoFila> contenido = new ArrayList<>();

        long ultimoId = 0;
        while (true) {
            var bloque = cargoRepository.buscarParaRecargo(request.institucionId(),
                    request.plantelId(), request.fechaCorte(), ultimoId, PageRequest.of(0, BLOQUE));
            if (bloque.isEmpty()) break;
            var permitidas=seleccion!=null && !seleccion.nueva()?selecciones.items(seleccion.id(),bloque.getContent().stream().map(c->"RECARGO:"+c.getId()).toList()):Map.<String,SeleccionGeneracionService.Item>of();
            for (Cargo cargo : bloque) {
                ultimoId = cargo.getId();
                PlanRecargo plan = planificar(cargo, request.fechaCorte());
                if (plan.periodos().isEmpty()) continue;
                BigDecimal recargoCargo = plan.periodos().stream().map(PeriodoRecargo::monto)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                BigDecimal saldoCargo = saldo(cargo);
                if(seleccion!=null) {
                    String clave="RECARGO:"+cargo.getId();
                    if(seleccion.nueva()) {captura.add(new SeleccionGeneracionService.Item(clave,cargo.getVersion(),recargoCargo,cargo.getFechaVencimiento()));if(captura.size()==100){selecciones.guardar(seleccion.id(),captura);captura.clear();}}
                    else {var esperado=permitidas.get(clave);if(esperado==null)continue;selecciones.comprobar(esperado,cargo.getVersion(),recargoCargo,cargo.getFechaVencimiento());}
                }
                if (aplicables >= desde && contenido.size() < tamanio)
                    contenido.add(fila(cargo, plan, saldoCargo, recargoCargo, request.fechaCorte()));
                aplicables++;
                recargos += plan.periodos().size();
                saldoActual = saldoActual.add(saldoCargo);
                totalRecargos = totalRecargos.add(recargoCargo);
            }
            if (bloque.getNumberOfElements() < BLOQUE) break;
        }

        BigDecimal saldoNormalizado = saldoActual.setScale(2, RoundingMode.HALF_UP);
        BigDecimal recargosNormalizados = totalRecargos.setScale(2, RoundingMode.HALF_UP);
        if(seleccion!=null && seleccion.nueva()){selecciones.guardar(seleccion.id(),captura);selecciones.completar(seleccion.id());}
        else if(seleccion!=null)selecciones.comprobarCantidad(selecciones.cantidad(seleccion.id()),aplicables);
        return new VistaPreviaRecargosResponse(
                new PageImpl<>(contenido, PageRequest.of(pagina, tamanio), aplicables),
                aplicables, recargos, saldoNormalizado, recargosNormalizados,
                saldoNormalizado.add(recargosNormalizados).setScale(2, RoundingMode.HALF_UP),seleccion==null?null:seleccion.id());
    }

    public GeneracionRecargosResponse generar(GeneracionRecargosRequest request) {
        long esperados=request.seleccionId()==null?0:selecciones.confirmar(request.seleccionId(),"RECARGOS",request.institucionId(),request.plantelId(),request.fechaCorte(),request.excluidos(),request.incluidos());
        int revisados = 0, generados = 0, existentes = 0, sinImporte = 0;
        long ultimoId = 0;
        while (true) {
            var bloque = cargoRepository.buscarParaRecargo(request.institucionId(),
                    request.plantelId(), request.fechaCorte(), ultimoId, PageRequest.of(0, BLOQUE));
            if (bloque.isEmpty()) break;
            var permitidas=request.seleccionId()==null?Map.<String,SeleccionGeneracionService.Item>of():selecciones.items(request.seleccionId(),bloque.getContent().stream().map(c->"RECARGO:"+c.getId()).toList());
            for (Cargo cargo : bloque) {
                ultimoId = cargo.getId();
                String clave="RECARGO:"+cargo.getId();if(!SeleccionGeneracionService.solicitada(clave,request.excluidos(),request.incluidos()))continue;
                if(request.seleccionId()!=null){var esperado=permitidas.get(clave);if(esperado==null)continue;var plan=planificar(cargo,request.fechaCorte());if(plan.periodos().isEmpty())continue;var monto=plan.periodos().stream().map(PeriodoRecargo::monto).reduce(BigDecimal.ZERO,BigDecimal::add);selecciones.comprobar(esperado,cargo.getVersion(),monto,cargo.getFechaVencimiento());}
                revisados++;
                Resultado resultado = generarCargo(cargo, request.fechaCorte());
                generados += resultado.generados();
                existentes += resultado.existentes();
                sinImporte += resultado.sinImporte();
            }
            if (bloque.getNumberOfElements() < BLOQUE) break;
        }
        if(request.seleccionId()!=null){selecciones.comprobarCantidad(esperados,revisados);selecciones.consumida(request.seleccionId());}
        return new GeneracionRecargosResponse(revisados, generados, existentes, sinImporte);
    }

    private Resultado generarCargo(Cargo cargo, LocalDate corte) {
        if (saldo(cargo).signum() <= 0) return new Resultado(0, 0, 1);
        PoliticaRecargo politica = politica(cargo);
        if (politica == null) return new Resultado(0, 0, 0);
        PlanRecargo plan = planificar(cargo, politica, corte);
        int generados = 0;
        int existentes = plan.existentes();
        for (PeriodoRecargo periodo : plan.periodos()) {
            int insertado = ajusteRepository.insertarRecargoSiAusente(cargo.getId(), periodo.monto(),
                    plan.base(), politica.getModalidad() == ModalidadBeca.PORCENTAJE
                            ? politica.getPorcentaje() : null,
                    "Recargo automático · " + politica.getConceptoCobro().getNombre()
                            + " · periodo " + (periodo.numero() + 1), actorActual(), periodo.fecha(),
                    politica.getId(), periodo.clave());
            if (insertado == 1) generados++;
            else existentes++;
        }
        return new Resultado(generados, existentes, plan.base().signum() <= 0 ? 1 : 0);
    }

    private PlanRecargo planificar(Cargo cargo, LocalDate corte) {
        if (saldo(cargo).signum() <= 0) return PlanRecargo.vacio();
        PoliticaRecargo politica = politica(cargo);
        return politica == null ? PlanRecargo.vacio() : planificar(cargo, politica, corte);
    }

    private PlanRecargo planificar(Cargo cargo, PoliticaRecargo politica, LocalDate corte) {
        LocalDate primera = cargo.getFechaVencimiento().plusDays((long) politica.getDiasGracia() + 1);
        if (corte.isBefore(primera)) return PlanRecargo.vacio();
        BigDecimal base = baseSinRecargos(cargo);
        if (base.signum() <= 0) return new PlanRecargo(politica, base, List.of(), 0);
        int cantidadPeriodos = politica.getPeriodicidad() == PeriodicidadRecargo.UNICA
                ? 1 : periodosVencidos(primera, corte);
        BigDecimal acumulado = ajusteRepository.totalRecargosAutomaticos(cargo.getId(), politica.getId());
        if (acumulado == null) acumulado = BigDecimal.ZERO;
        BigDecimal limite = limite(politica, cargo.getImporteOriginal());
        Set<String> clavesExistentes = new HashSet<>(ajusteRepository.clavesDeRecargosAutomaticos(
                cargo.getId(), politica.getId()));
        List<PeriodoRecargo> nuevos = new ArrayList<>();
        int existentes = 0;
        for (int numero = 0; numero < cantidadPeriodos; numero++) {
            BigDecimal disponible = limite == null ? null : limite.subtract(acumulado);
            if (disponible != null && disponible.signum() <= 0) break;
            BigDecimal monto = monto(politica, base);
            if (disponible != null) monto = monto.min(disponible);
            if (monto.signum() <= 0) break;
            String clave = "RECARGO:" + politica.getId() + ":" + cargo.getId() + ":" + numero;
            if (clavesExistentes.contains(clave)) {
                existentes++;
                continue;
            }
            nuevos.add(new PeriodoRecargo(numero, fechaPeriodo(primera, numero), monto, clave));
            acumulado = acumulado.add(monto);
        }
        return new PlanRecargo(politica, base, nuevos, existentes);
    }

    private PoliticaRecargo politica(Cargo cargo) {
        return repository.findByConceptoCobroIdAndActivoTrueAndGeneracionAutomaticaTrue(
                cargo.getConceptoCobro().getId()).orElse(null);
    }

    private VistaPreviaRecargoFila fila(Cargo cargo, PlanRecargo plan, BigDecimal saldoActual,
                                         BigDecimal recargo, LocalDate corte) {
        var alumno = cargo.getInscripcion().getAlumno();
        String nombre = Stream.of(alumno.getNombres(), alumno.getPrimerApellido(),
                        alumno.getSegundoApellido()).filter(v -> v != null && !v.isBlank())
                .collect(Collectors.joining(" "));
        PoliticaRecargo politica = plan.politica();
        String detalle = politica.getModalidad() == ModalidadBeca.PORCENTAJE
                ? politica.getPorcentaje().stripTrailingZeros().toPlainString() + " %"
                : politica.getMoneda() + " " + politica.getMontoFijo().setScale(2);
        detalle += politica.getPeriodicidad() == PeriodicidadRecargo.UNICA
                ? " · una vez" : " · mensual";
        return new VistaPreviaRecargoFila(cargo.getId(), alumno.getMatricula(), nombre,
                cargo.getInscripcion().getPlantel().getNombre(), cargo.getConceptoCobro().getNombre(),
                cargo.getFechaVencimiento(), Math.max(0, ChronoUnit.DAYS.between(
                        cargo.getFechaVencimiento(), corte)), detalle, plan.periodos().size(),
                saldoActual, recargo, saldoActual.add(recargo).setScale(2, RoundingMode.HALF_UP));
    }

    private int periodosVencidos(LocalDate primera, LocalDate corte) {
        int meses = (int) ChronoUnit.MONTHS.between(YearMonth.from(primera), YearMonth.from(corte));
        int total = meses + 1;
        if (fechaPeriodo(primera, meses).isAfter(corte)) total--;
        return Math.max(0, total);
    }

    private LocalDate fechaPeriodo(LocalDate primera, int numero) {
        YearMonth mes = YearMonth.from(primera).plusMonths(numero);
        return mes.atDay(Math.min(primera.getDayOfMonth(), mes.lengthOfMonth()));
    }

    private BigDecimal baseSinRecargos(Cargo cargo) {
        BigDecimal base = cargo.getImporteOriginal();
        for (AjusteCargo ajuste : cargo.getAjustes()) {
            boolean esRecargo = ajuste.getTipo() == TipoAjusteCargo.RECARGO
                    || ajuste.getReversaDe() != null
                    && ajuste.getReversaDe().getTipo() == TipoAjusteCargo.RECARGO;
            if (esRecargo) continue;
            base = ajuste.getEfecto() == EfectoAjusteCargo.AUMENTO
                    ? base.add(ajuste.getMonto()) : base.subtract(ajuste.getMonto());
        }
        return base.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal monto(PoliticaRecargo politica, BigDecimal base) {
        return (politica.getModalidad() == ModalidadBeca.PORCENTAJE
                ? base.multiply(politica.getPorcentaje()).divide(new BigDecimal("100"),
                2, RoundingMode.HALF_UP) : politica.getMontoFijo()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal limite(PoliticaRecargo politica, BigDecimal original) {
        return switch (politica.getTipoLimite()) {
            case SIN_LIMITE -> null;
            case MONTO_FIJO -> politica.getValorLimite().setScale(2, RoundingMode.HALF_UP);
            case PORCENTAJE_ORIGINAL -> original.multiply(politica.getValorLimite())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        };
    }

    private void validar(PoliticaRecargoRequest request, ConceptoCobro concepto, Long id) {
        if (!concepto.isActivo() || !concepto.isPermiteRecargo())
            throw new ReglaNegocioException("El concepto debe estar activo y permitir recargos");
        if (repository.existsByConceptoCobroIdAndIdNot(concepto.getId(), id))
            throw new RecursoDuplicadoException("El concepto ya tiene una política de recargo");
        if (request.modalidad() == ModalidadBeca.PORCENTAJE) {
            if (request.porcentaje() == null || request.porcentaje().signum() <= 0
                    || request.porcentaje().compareTo(new BigDecimal("100")) > 0
                    || request.porcentaje().scale() > 4 || request.montoFijo() != null)
                throw new ReglaNegocioException("Indica un porcentaje mayor a 0 y máximo 100");
        } else {
            if (request.montoFijo() == null || request.montoFijo().signum() <= 0
                    || request.montoFijo().scale() > 2 || request.porcentaje() != null)
                throw new ReglaNegocioException("Indica un monto fijo positivo con máximo dos decimales");
            if (!codigo(request.moneda()).equals(concepto.getInstitucion().getMonedaPredeterminada()))
                throw new ReglaNegocioException("La moneda debe coincidir con la institución");
        }
        if (request.tipoLimite() != TipoLimiteRecargo.SIN_LIMITE
                && (request.valorLimite() == null || request.valorLimite().signum() <= 0))
            throw new ReglaNegocioException("La política seleccionada requiere un límite positivo");
        if (request.tipoLimite() == TipoLimiteRecargo.PORCENTAJE_ORIGINAL
                && request.valorLimite().compareTo(new BigDecimal("1000")) > 0)
            throw new ReglaNegocioException("El límite porcentual no puede superar 1000 %");
    }

    private PoliticaRecargoRequest normalizar(PoliticaRecargoRequest request) {
        return new PoliticaRecargoRequest(request.conceptoCobroId(), request.modalidad(),
                request.modalidad() == ModalidadBeca.PORCENTAJE ? request.porcentaje() : null,
                request.modalidad() == ModalidadBeca.MONTO_FIJO
                        ? request.montoFijo().setScale(2, RoundingMode.UNNECESSARY) : null,
                request.modalidad() == ModalidadBeca.MONTO_FIJO ? codigo(request.moneda()) : null,
                request.diasGracia(), request.periodicidad(), request.tipoLimite(),
                request.tipoLimite() == TipoLimiteRecargo.SIN_LIMITE ? null : request.valorLimite(),
                request.generacionAutomatica(), request.activo(), request.version());
    }

    private Long actorActual() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        return autenticacion != null && autenticacion.getPrincipal() instanceof UsuarioPrincipal principal
                ? principal.usuarioId() : null;
    }

    private record PeriodoRecargo(int numero, LocalDate fecha, BigDecimal monto, String clave) { }
    private record PlanRecargo(PoliticaRecargo politica, BigDecimal base,
                               List<PeriodoRecargo> periodos, int existentes) {
        private static PlanRecargo vacio() {
            return new PlanRecargo(null, BigDecimal.ZERO, List.of(), 0);
        }
    }
    private record Resultado(int generados, int existentes, int sinImporte) { }
}
