package escuela.finanzas.service;

import escuela.admin.dto.*;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.CargoRepository;
import escuela.finanzas.dto.request.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.PagoMapper;
import escuela.finanzas.repository.*;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.*;
import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.common.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.*;
import java.math.*;
import java.time.*;
import java.util.*;
import static escuela.cobranza.support.CalculoCargo.saldo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class SaldoFavorService {
    private final PagoRepository pagos;
    private final AplicacionPagoRepository aplicaciones;
    private final CargoRepository cargos;
    private final AlumnoTutorRepository vinculos;
    private final UsuarioRepository usuarios;
    private final PagoMapper mapper;
    private final AlcanceDatosService alcance;
    private final RegistroAuditoriaService auditoria;

    public record DatoAplicacion(Long id,String alumno,String concepto,BigDecimal monto,String moneda,
        String fecha,String operacion,String motivo,String autorizador,boolean reversible) { }
    public record DatosValidacion(BigDecimal reportado,String motivo,String zona) { }

    @Transactional(readOnly=true)
    public DatosValidacion datosValidacion(Long id) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS,id);
        var p=pagos.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el pago",id));
        return new DatosValidacion(p.getMontoReportado()==null?p.getMonto():p.getMontoReportado(),p.getMotivoCambioMonto(),p.getInstitucion().getZonaHoraria());
    }
    @Transactional(readOnly=true)
    public Page<DatoAplicacion> historial(Long id,String operacion,int pagina,int tamanio) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS,id);
        String filtro=operacion==null?"":operacion;
        if(!Set.of("","APLICAR","REVERTIR").contains(filtro)) throw new ReglaNegocioException("Selecciona una operación válida");
        return aplicaciones.historialSaldo(id,filtro,PageRequest.of(Math.max(0,pagina),Math.min(100,Math.max(1,tamanio)))).map(a->new DatoAplicacion(a.getId(),
          a.getCargo().getInscripcion().getAlumno().getNombres()+" · "+a.getCargo().getInscripcion().getAlumno().getMatricula(),
          a.getCargo().getDescripcion(),a.getMonto(),a.getPago().getMoneda(),java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of(a.getPago().getInstitucion().getZonaHoraria())).format(a.getFechaAplicacion()),a.getOperacion().name(),
          a.getMotivoSaldoFavor(),a.getAutorizadoPor().getUsername(),a.getOperacion()==OperacionAplicacionPago.APLICAR&&a.getReversa()==null));
    }
    @Transactional(readOnly=true)
    public ResultadoAutocompletado buscar(Long id,String texto) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS,id);
        var p=pagos.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el pago",id));
        if(p.getEstado()!=EstadoPago.VALIDADO) return ResultadoAutocompletado.vacio();
        String q=texto==null?"":texto.trim(); if(!q.isEmpty()&&q.length()<3) return ResultadoAutocompletado.vacio();
        var filas=cargos.buscarParaSaldoFavor(p.getInstitucion().getId(),p.getTutor().getId(),p.getPlantelRegistro().getId(),p.getMoneda(),q,
            PageRequest.of(0,q.isEmpty()?10:20));
        var opciones=filas.getContent().stream().filter(c->{
            try{alcance.validarRecurso(ModuloCatalogo.CARGOS,c.getId());return true;}catch(AccessDeniedException ex){return false;}
        }).map(c->new OpcionAutocompletado(c.getId(),c.getInscripcion().getAlumno().getMatricula()+" · "+c.getInscripcion().getAlumno().getNombres()+" · "+c.getDescripcion(),
            "Saldo pendiente · "+p.getMoneda(),saldo(c))).toList();
        return new ResultadoAutocompletado(opciones,filas.hasNext());
    }
    public void aplicar(Long id,AplicarSaldoFavorRequest r) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS,id);
        var p=pagos.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("el pago",id));
        var actor=actor(p,"PAGO_VALIDAR");
        if(p.getEstado()!=EstadoPago.VALIDADO) throw new ReglaNegocioException("Sólo se puede aplicar saldo de un pago validado");
        String motivo=motivo(r.motivo());
        try{UUID.fromString(r.clave());}catch(Exception ex){throw new ReglaNegocioException("La confirmación de saldo no es válida; recarga la pantalla");}
        if(r.cargos()==null||r.cargos().isEmpty()||r.cargos().size()>20) throw new ReglaNegocioException("Selecciona de uno a veinte cargos");
        var ids=new HashSet<Long>(); BigDecimal total=BigDecimal.ZERO;
        boolean reintento=true; boolean algunoExistente=false;
        for(var s:r.cargos()) {
            if(s.cargoId()==null||!ids.add(s.cargoId()))throw new ReglaNegocioException("Selecciona cada cargo una sola vez");
            dinero(s.montoSolicitado()); total=total.add(s.montoSolicitado());
            var anterior=aplicaciones.findByPagoIdAndClaveSaldoFavor(id,r.clave()+":"+s.cargoId());
            if(anterior.isEmpty())reintento=false;
            else { algunoExistente=true; if(anterior.get().getMonto().compareTo(s.montoSolicitado())!=0||!Objects.equals(anterior.get().getMotivoSaldoFavor(),motivo))
                throw new ReglaNegocioException("La confirmación ya fue usada con otros datos");
            }
        }
        if(reintento)return;
        if(algunoExistente)throw new ReglaNegocioException("La confirmación ya fue usada; recarga la pantalla antes de cambiar la distribución");
        verificar(p,r.version(),"Pago");
        if(total.compareTo(mapper.respuesta(p).montoDisponible())>0) throw new ReglaNegocioException("La distribución supera el saldo a favor disponible; actualiza la pantalla");
        var orden=r.cargos().stream().sorted(Comparator.comparing(SolicitudAplicacionPagoRequest::cargoId)).toList();
        for(var s:orden) {
            alcance.validarRecurso(ModuloCatalogo.CARGOS,s.cargoId());
            var c=cargos.findByIdForUpdate(s.cargoId()).orElseThrow(()->new RecursoNoEncontradoException("el cargo",s.cargoId()));
            validarCargo(p,c);
            p.getAplicaciones().size(); c.getAplicaciones().size();
            if(s.montoSolicitado().compareTo(saldo(c))>0)throw new ReglaNegocioException("El importe supera el saldo actual de "+c.getDescripcion());
            var a=new AplicacionPago();a.setPago(p);a.setCargo(c);a.setMonto(s.montoSolicitado().setScale(2));
            a.setOperacion(OperacionAplicacionPago.APLICAR);a.setFechaAplicacion(Instant.now());a.setSaldoFavor(true);
            a.setMotivoSaldoFavor(motivo);a.setAutorizadoPor(actor);a.setClaveSaldoFavor(r.clave()+":"+s.cargoId());
            aplicaciones.saveAndFlush(a);p.getAplicaciones().add(a);c.getAplicaciones().add(a);
        }
        p.setActualizadoEn(Instant.now());pagos.saveAndFlush(p);
        auditoria.registrar(p.getInstitucion().getId(),AccionAuditoria.SALDO_FAVOR_APLICADO,"PAGO",id,motivo,
          Map.of("folio",p.getFolio(),"importe",total,"cargos",ids,"sinMovimientoBancario",true));
    }
    public void revertir(Long id,Long aplicacionId,Long version,String motivo) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS,id);
        var p=pagos.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("el pago",id));
        var actor=actor(p,"PAGO_CANCELAR");String razon=motivo(motivo);
        var a=aplicaciones.findById(aplicacionId).orElseThrow(()->new RecursoNoEncontradoException("la aplicación",aplicacionId));
        if(!a.getPago().getId().equals(id)||!a.isSaldoFavor()||a.getOperacion()!=OperacionAplicacionPago.APLICAR)
            throw new ReglaNegocioException("Selecciona una aplicación administrativa de este pago");
        if(p.getEstado()!=EstadoPago.VALIDADO)throw new ReglaNegocioException("El pago ya no está validado");
        if(a.getReversa()!=null)return;
        verificar(p,version,"Pago");alcance.validarRecurso(ModuloCatalogo.CARGOS,a.getCargo().getId());
        var c=cargos.findByIdForUpdate(a.getCargo().getId()).orElseThrow();
        p.getAplicaciones().size(); c.getAplicaciones().size();
        if(c.getEstadoRegistro()!=EstadoRegistroCargo.EMITIDO)
            throw new ReglaNegocioException("El cargo está cancelado o incluido en un convenio; resuelve ese proceso antes de revertir");
        var rev=new AplicacionPago();rev.setPago(p);rev.setCargo(c);rev.setMonto(a.getMonto());rev.setOperacion(OperacionAplicacionPago.REVERTIR);
        rev.setFechaAplicacion(Instant.now());rev.setReversaDe(a);rev.setMotivo(razon);rev.setSaldoFavor(true);
        rev.setMotivoSaldoFavor(razon);rev.setAutorizadoPor(actor);aplicaciones.saveAndFlush(rev);a.setReversa(rev);
        p.getAplicaciones().add(rev);c.getAplicaciones().add(rev);p.setActualizadoEn(Instant.now());pagos.saveAndFlush(p);
        auditoria.registrar(p.getInstitucion().getId(),AccionAuditoria.SALDO_FAVOR_REVERTIDO,"APLICACION_PAGO",a.getId(),razon,
            Map.of("pagoId",id,"cargoId",c.getId(),"importe",a.getMonto(),"sinMovimientoBancario",true));
    }
    private void validarCargo(Pago p,Cargo c) {
        var i=c.getInscripcion();
        if(!p.getTutor().isActivo()||!i.getAlumno().isActivo()||c.getEstadoRegistro()!=EstadoRegistroCargo.EMITIDO||!i.getAlumno().getInstitucion().getId().equals(p.getInstitucion().getId())
            ||!i.getPlantel().getId().equals(p.getPlantelRegistro().getId())||!c.getMoneda().equals(p.getMoneda()))
            throw new ReglaNegocioException("El cargo debe estar emitido y pertenecer a la misma institución, plantel y moneda del pago");
        var hoy=LocalDate.now(ZoneId.of(p.getInstitucion().getZonaHoraria()));
        if(!vinculos.tieneResponsabilidadFinancieraVigente(i.getAlumno().getId(),p.getTutor().getId(),hoy))
            throw new ReglaNegocioException("El tutor no es responsable financiero vigente de ese alumno");
        if(cargos.tienePagoEnRevision(c.getId()))throw new ReglaNegocioException("El cargo tiene un pago en revisión; resuélvelo antes de aplicar saldo a favor");
    }
    private escuela.seguridad.entity.Usuario actor(Pago p,String permiso) {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null||!(auth.getPrincipal() instanceof UsuarioPrincipal u)||u.accesoRecuperacion()||u.usuarioId()==null
          ||auth.getAuthorities().stream().noneMatch(a->a.getAuthority().equals(permiso)))
            throw new AccessDeniedException("Se requiere un administrador identificado con permiso para esta operación");
        var actor=usuarios.findById(u.usuarioId()).orElseThrow(()->new AccessDeniedException("La cuenta ya no existe"));
        if(!actor.getInstitucion().getId().equals(p.getInstitucion().getId()))throw new AccessDeniedException("Pago fuera de la institución");
        return actor;
    }
    private String motivo(String valor) {String m=limpiar(valor);if(m==null||m.length()>2000)throw new ReglaNegocioException("Captura un motivo de máximo 2000 caracteres");return m;}
    private void dinero(BigDecimal monto) {if(monto==null||monto.signum()<=0||monto.scale()>2||monto.precision()-monto.scale()>17)throw new ReglaNegocioException("El importe debe ser positivo y tener hasta dos decimales");}
}
