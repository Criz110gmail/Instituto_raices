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
import escuela.cobranza.service.SeleccionGeneracionService;
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
    private final SeleccionGeneracionService selecciones;

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
        long esperados=request.seleccionId()==null?0:selecciones.confirmar(request.seleccionId(),"CARGOS",request.institucionId(),request.plantelId(),request.fechaCorte(),request.excluidos(),request.incluidos());
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
                ResultadoGeneracion resultado = generarCuota(cuota, request);
                generados += resultado.generados();
                existentes += resultado.existentes();
                ultimoId = cuota.getId();
            }
            if (bloque.getNumberOfElements() < TAMANO_BLOQUE) break;
        }
        if(request.seleccionId()!=null){selecciones.comprobarCantidad(esperados,(long)generados+existentes);selecciones.consumida(request.seleccionId());}
        return new GeneracionCargosResponse(cuotasRevisadas, generados, existentes);
    }

    @Override
    @Transactional
    public VistaPreviaCargosAutomaticosResponse previsualizar(GeneracionCargosRequest request,
                                                               int numeroPagina, int tamanioPagina) {
        var seleccion=selecciones.preparar(request.seleccionId(),"CARGOS",request.institucionId(),request.plantelId(),request.fechaCorte());
        var captura=new ArrayList<SeleccionGeneracionService.Item>();
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
                var permitidas=seleccion!=null && !seleccion.nueva()?selecciones.items(seleccion.id(),plan.pendientes().stream().map(p->clave(cuota,p.clavePeriodo())).toList()):java.util.Map.<String,SeleccionGeneracionService.Item>of();
                boolean cuotaIncluida=false;
                for (PeriodoCargo periodo : plan.pendientes()) {
                    var fila = fila(cuota, periodo);
                    if(seleccion!=null) {
                        if(seleccion.nueva()) {
                            captura.add(new SeleccionGeneracionService.Item(fila.claveSeleccion(),cuota.getVersion(),fila.importeNeto(),fila.fechaVencimiento()));
                            if(captura.size()==100){selecciones.guardar(seleccion.id(),captura);captura.clear();}
                        } else {
                            var esperado=permitidas.get(fila.claveSeleccion());if(esperado==null)continue;
                            selecciones.comprobar(esperado,cuota.getVersion(),fila.importeNeto(),fila.fechaVencimiento());
                        }
                    }
                    cuotaIncluida=true;
                    if (pagosPorGenerar >= desde && contenido.size() < tamanio)
                        contenido.add(fila);
                    pagosPorGenerar++;
                    importeTotal = importeTotal.add(fila.importe());
                    becaTotal = becaTotal.add(fila.montoBeca());
                    importeNetoTotal = importeNetoTotal.add(fila.importeNeto());
                }
                if(cuotaIncluida)cuotasConPendientes++;
            }
            if (bloque.getNumberOfElements() < TAMANO_BLOQUE) break;
        }
        if(seleccion!=null && seleccion.nueva()){selecciones.guardar(seleccion.id(),captura);selecciones.completar(seleccion.id());}
        else if(seleccion!=null)selecciones.comprobarCantidad(selecciones.cantidad(seleccion.id()),pagosPorGenerar);
        return new VistaPreviaCargosAutomaticosResponse(
                new PageImpl<>(contenido, PageRequest.of(pagina, tamanio), pagosPorGenerar),
                cuotasConPendientes, pagosPorGenerar,
                importeTotal.setScale(2, RoundingMode.HALF_UP), becaTotal,
                importeNetoTotal,seleccion==null?null:seleccion.id());
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
        var cargo=repository.findByClaveGeneracion(clave(cuota, "UNICA"))
                .orElseThrow(() -> new ReglaNegocioException("No fue posible localizar el cargo generado"));
        var siguiente=repository.findByReemplazaCargoId(cargo.getId());
        while(siguiente.isPresent()) {cargo=siguiente.get();siguiente=repository.findByReemplazaCargoId(cargo.getId());}
        if(cargo.getEstadoRegistro()!=EstadoRegistroCargo.EMITIDO)
            throw new ReglaNegocioException("Esta cuota ya generó un cargo cancelado o convenido. Abre su detalle; no se regenera automáticamente.");
        return mapper.respuesta(cargo);
    }

    @Override
    @Transactional(readOnly = true)
    public CargoResponse obtener(Long id) {
        return mapper.respuesta(repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", id)));
    }

    @Override @Transactional(readOnly=true)
    public Long reemplazoDe(Long id) {return repository.findByReemplazaCargoId(id).map(Cargo::getId).orElse(null);}

    @Override @Transactional(readOnly=true)
    public org.springframework.data.domain.Page<CuotaExcluida> diagnosticar(GeneracionCargosRequest request,int pagina) {
        int numero=Math.max(pagina,0), tamanio=25; long desde=(long)numero*tamanio, total=0, ultimo=0;
        List<CuotaExcluida> filas=new ArrayList<>();
        while(true) {
            var bloque=cuotaRepository.buscarParaDiagnostico(request.institucionId(),request.plantelId(),ultimo,PageRequest.of(0,TAMANO_BLOQUE));
            if(bloque.isEmpty()) break;
            for(var q:bloque) {
                ultimo=q.getId(); String motivo=null;
                if(q.getEstado()!=EstadoCuota.ACTIVA) motivo="Cuota "+q.getEstado().name().toLowerCase();
                else if(!q.isGeneracionAutomatica()) motivo="Generación automática deshabilitada";
                else if(!q.getConceptoCobro().isActivo()) motivo="Concepto inactivo";
                else if(!INSCRIPCIONES_VIGENTES.contains(q.getInscripcion().getEstado())) motivo="Inscripción no vigente";
                else if(q.getFechaInicio().isAfter(request.fechaCorte())) motivo="La cuota inicia después del corte";
                else if(planificar(q,request.fechaCorte()).pendientes().isEmpty())
                    motivo=q.getFrecuencia()==FrecuenciaCuota.UNICA
                        ? "Cuota única ya generada. Cancelar su cargo no la regenera; abre el cargo para corregir y reemplazar."
                        : "Todos los periodos de este corte ya fueron generados, incluidos los cancelados. Corregir la cuota no modifica esos cargos.";
                if(motivo!=null) {
                    if(total>=desde && filas.size()<tamanio) {
                        var a=q.getInscripcion().getAlumno();
                        filas.add(new CuotaExcluida(q.getId(),a.getMatricula()+" · "+Stream.of(a.getNombres(),a.getPrimerApellido(),a.getSegundoApellido()).filter(v->v!=null).collect(Collectors.joining(" ")),
                            q.getConceptoCobro().getNombre(),motivo,cuotaRepository.ultimoCargo(q.getId())));
                    }
                    total++;
                }
            }
            if(bloque.getNumberOfElements()<TAMANO_BLOQUE) break;
        }
        return new PageImpl<>(filas,PageRequest.of(numero,tamanio),total);
    }

    @Override
    public CargoResponse cancelar(Long id, Long version, String motivo) {
        Cargo cargo = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", id));
        if(repository.tieneBeneficioAnticipadoAplicado(id))throw new ReglaNegocioException("Este cargo pertenece a un acuerdo anticipado aplicado; revisa o cancela el pago asociado, no el cargo por separado");
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

    private ResultadoGeneracion generarCuota(CuotaAlumno cuota, GeneracionCargosRequest request) {
        PlanCuota plan = planificar(cuota, request.fechaCorte());
        var permitidas=request.seleccionId()==null?java.util.Map.<String,SeleccionGeneracionService.Item>of():selecciones.items(request.seleccionId(),plan.pendientes().stream().map(p->clave(cuota,p.clavePeriodo())).toList());
        int generados = 0;
        int existentes = request.seleccionId()==null?plan.existentes():0;
        for (PeriodoCargo periodo : plan.pendientes()) {
            String clave=clave(cuota,periodo.clavePeriodo());
            if(!SeleccionGeneracionService.solicitada(clave,request.excluidos(),request.incluidos()))continue;
            if(request.seleccionId()!=null){var esperado=permitidas.get(clave);if(esperado==null)continue;var vista=fila(cuota,periodo);selecciones.comprobar(esperado,cuota.getVersion(),vista.importeNeto(),vista.fechaVencimiento());}
            ResultadoGeneracion resultado = insertar(cuota, periodo.inicio(), periodo.fin(),
                    periodo.vencimiento(), periodo.clavePeriodo(), periodo.descripcion());
            if(request.seleccionId()!=null && resultado.generados()>0) {
                var emitido=repository.findByClaveGeneracion(clave).orElseThrow(()->new ReglaNegocioException("No se pudo comprobar el cargo emitido; vuelve a visualizar."));
                selecciones.comprobar(permitidas.get(clave),cuota.getVersion(),escuela.cobranza.support.CalculoCargo.total(emitido),emitido.getFechaVencimiento());
            }
            generados += resultado.generados();
            existentes += resultado.existentes();
        }
        return new ResultadoGeneracion(generados, existentes);
    }

    private PlanCuota planificar(CuotaAlumno cuota, LocalDate fechaCorte) {
        var candidatos = candidatos(cuota, fechaCorte);
        Set<String> existentes = new HashSet<>(repository.clavesGeneradasPorCuota(cuota.getId()));
        var pendientes = candidatos.stream().filter(p -> !existentes.contains(clave(cuota,p.clavePeriodo()))).toList();
        return new PlanCuota(pendientes,candidatos.size()-pendientes.size());
    }

    private List<PeriodoCargo> candidatos(CuotaAlumno cuota, LocalDate fechaCorte) {
        LocalDate fin = menor(fechaCorte, cuota.getFechaFin());
        if (fin.isBefore(cuota.getFechaInicio())) return List.of();
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
        return candidatos;
    }

    /** Operación individual: no recorre cuotas de otros alumnos ni cambia saldos. */
    @Transactional(readOnly=true)
    public List<escuela.cobranza.dto.FilaAnticipoInscripcion> anticipables(Long inscripcionId, LocalDate desde, LocalDate hasta) {
        autorizarAnticipo(inscripcionId);
        if(desde==null||hasta==null||hasta.isBefore(desde)||java.time.temporal.ChronoUnit.MONTHS.between(YearMonth.from(desde),YearMonth.from(hasta))>23)
            throw new ReglaNegocioException("Selecciona un rango válido de hasta 24 meses");
        var ins=inscripcionRepository.findById(inscripcionId).orElseThrow();
        if(!INSCRIPCIONES_VIGENTES.contains(ins.getEstado())||!ins.getAlumno().isActivo())throw new ReglaNegocioException("La inscripción y el alumno deben estar vigentes");
        var filas=new ArrayList<escuela.cobranza.dto.FilaAnticipoInscripcion>();
        var registrados=repository.findAllByInscripcionIdOrderByFechaVencimientoDescIdDesc(inscripcionId);
        for(var q:cuotaRepository.findAllByInscripcionIdOrderByCreadoEnDescIdDesc(inscripcionId)) {
            if(q.getEstado()!=EstadoCuota.ACTIVA||!q.getConceptoCobro().isActivo())continue;
            for(var p:candidatos(q,hasta)) {
                if(p.fin().isBefore(desde))continue;
                var cargo=repository.findByClaveGeneracion(clave(q,p.clavePeriodo()));
                if(cargo.isEmpty()) {
                    var coincidentes=registrados.stream().filter(c->c.getConceptoCobro().getId().equals(q.getConceptoCobro().getId())&&c.getPeriodoCobroInicio().equals(p.inicio())&&c.getPeriodoCobroFin().equals(p.fin())).toList();
                    if(coincidentes.size()>1)throw new ReglaNegocioException("Hay varios cargos para el mismo concepto y periodo. Revisa los cargos antes de preparar el anticipo");
                    if(!coincidentes.isEmpty())cargo=java.util.Optional.of(coincidentes.getFirst());
                }
                while(cargo.isPresent()) {var siguiente=repository.findByReemplazaCargoId(cargo.get().getId());if(siguiente.isEmpty())break;cargo=siguiente;}
                var f=fila(q,p);var inicio=p.inicio();var fin=p.fin();var vencimiento=p.vencimiento();var moneda=f.moneda();var concepto=f.concepto();String estado="Por generar";Long cargoId=null;Long version=null;BigDecimal importe=f.importeNeto();boolean disponible=importe.signum()>0;
                if(cargo.isPresent()) {
                    var c=cargo.get();var v=mapper.respuesta(c);cargoId=c.getId();version=c.getVersion();importe=v.saldoPendiente();inicio=v.periodoCobroInicio();fin=v.periodoCobroFin();vencimiento=v.fechaVencimiento();moneda=v.moneda();concepto=v.conceptoNombre();
                    estado=c.getEstadoRegistro()!=EstadoRegistroCargo.EMITIDO?"Cancelado o sustituido":importe.signum()<=0?"Liquidado":repository.tienePagoEnRevision(c.getId())?"Pago en revisión":"Existente";
                    disponible=estado.equals("Existente")&&importe.signum()>0;
                } else if(!disponible)estado="Sin importe por cobrar";
                filas.add(new escuela.cobranza.dto.FilaAnticipoInscripcion(f.claveSeleccion(),q.getId(),q.getVersion(),cargoId,version,concepto,inicio,fin,vencimiento,importe,moneda,estado,disponible));
                if(filas.size()>500)throw new ReglaNegocioException("El rango contiene más de 500 cargos; reduce los meses consultados");
            }
        }
        return filas.stream().sorted(java.util.Comparator.comparing(escuela.cobranza.dto.FilaAnticipoInscripcion::inicio).thenComparing(escuela.cobranza.dto.FilaAnticipoInscripcion::clave)).toList();
    }

    public List<escuela.cobranza.dto.response.CargoResponse> confirmarAnticipo(Long inscripcionId,LocalDate desde,LocalDate hasta,List<escuela.cobranza.dto.FilaAnticipoInscripcion> esperadas,Set<String> elegidas) {
        autorizarAnticipo(inscripcionId);
        if(elegidas==null||elegidas.isEmpty()||elegidas.size()>100)throw new ReglaNegocioException("Selecciona entre uno y cien cargos");
        inscripcionRepository.findByIdForUpdate(inscripcionId).orElseThrow();
        cuotaRepository.bloquearPorInscripcion(inscripcionId);
        repository.bloquearPorInscripcion(inscripcionId);
        var actuales=anticipables(inscripcionId,desde,hasta);
        var seleccionadas=new ArrayList<escuela.cobranza.dto.FilaAnticipoInscripcion>();
        for(String clave:elegidas.stream().sorted().toList()) {
            var anterior=esperadas.stream().filter(f->f.clave().equals(clave)).findFirst().orElseThrow(()->new ReglaNegocioException("La selección no pertenece a esta vista previa"));
            var actual=actuales.stream().filter(f->f.clave().equals(clave)).findFirst().orElseThrow(()->new ReglaNegocioException("La configuración cambió; vuelve a consultar"));
            if(!actual.equals(anterior)||!actual.disponible())throw new ReglaNegocioException("Cambió un cargo, su beca o configuración. Vuelve a consultar antes de confirmar");
            seleccionadas.add(actual);
        }
        if(seleccionadas.stream().map(escuela.cobranza.dto.FilaAnticipoInscripcion::moneda).distinct().count()!=1)throw new ReglaNegocioException("Un pago sólo puede contener cargos de la misma moneda");
        var resultado=new ArrayList<escuela.cobranza.dto.response.CargoResponse>();
        for(var f:seleccionadas) {
            if(f.cargoId()!=null){repository.findByIdForUpdate(f.cargoId());var v=obtener(f.cargoId());if(v.saldoPendiente().compareTo(f.importe())!=0||repository.tienePagoEnRevision(f.cargoId()))throw new ReglaNegocioException("El cargo cambió o ya tiene un pago en revisión");resultado.add(v);continue;}
            var q=cuotaRepository.findById(f.cuotaId()).orElseThrow();
            var p=candidatos(q,hasta).stream().filter(x->clave(q,x.clavePeriodo()).equals(f.clave())).findFirst().orElseThrow();
            insertar(q,p.inicio(),p.fin(),p.vencimiento(),p.clavePeriodo(),p.descripcion());
            var c=repository.findByClaveGeneracion(f.clave()).orElseThrow();var v=mapper.respuesta(c);
            if(v.estadoRegistro()!=EstadoRegistroCargo.EMITIDO||v.saldoPendiente().compareTo(f.importe())!=0||repository.tienePagoEnRevision(c.getId()))throw new ReglaNegocioException("El cargo generado cambió; vuelve a consultar");
            resultado.add(v);
        }
        return List.copyOf(resultado);
    }

    private void autorizarAnticipo(Long id) {
        var a=SecurityContextHolder.getContext().getAuthentication();
        if(a==null||!a.isAuthenticated()||Stream.of("INSCRIPCION_ADMINISTRAR","CARGO_ADMINISTRAR","PAGO_REGISTRAR").anyMatch(p->a.getAuthorities().stream().noneMatch(x->x.getAuthority().equals(p))))throw new org.springframework.security.access.AccessDeniedException("Se requieren permisos de inscripción, cargos y registro de pagos");
        var i=inscripcionRepository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("la inscripción",id));
        if(!(a.getPrincipal() instanceof UsuarioPrincipal u)||u.usuarioId()==null||u.accesoRecuperacion()||!i.getAlumno().getInstitucion().getId().equals(u.institucionId())||!u.alcanceInstitucional()&&!u.plantelIds().contains(i.getPlantel().getId()))throw new org.springframework.security.access.AccessDeniedException("La inscripción está fuera de tu alcance");
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
                importe.subtract(beca.monto()), clave(cuota,periodo.clavePeriodo()));
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
