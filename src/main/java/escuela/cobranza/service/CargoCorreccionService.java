package escuela.cobranza.service;

import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.entity.*;
import escuela.cobranza.mapper.CargoMapper;
import escuela.cobranza.repository.*;
import escuela.cobranza.service.impl.AplicacionBecaCargoService;
import escuela.common.exception.*;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor
public class CargoCorreccionService {
    private final CargoRepository cargos;
    private final CuotaAlumnoRepository cuotas;
    private final CargoMapper mapper;
    private final AplicacionBecaCargoService becas;

    @Transactional(readOnly=true)
    public Enlaces enlaces(Long id) {
        var c=obtener(id);
        var siguiente=cargos.findByReemplazaCargoId(id).map(Cargo::getId).orElse(null);
        return new Enlaces(c.getReemplazaCargo()==null?null:c.getReemplazaCargo().getId(), siguiente,c.getMotivoReemplazo());
    }
    public record Enlaces(Long anteriorId, Long siguienteId, String motivo) { }

    @Transactional(readOnly=true)
    public LocalDate fechaSugerida(Long id) {
        var c=obtener(id); var q=c.getCuotaAlumno();
        return q!=null && q.getFrecuencia()==FrecuenciaCuota.UNICA ? q.getFechaVencimientoUnico() : c.getFechaVencimiento();
    }

    @Transactional
    public CargoResponse reemplazar(Long id, Long version, LocalDate vencimiento, String motivo) {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if (auth==null || !(auth.getPrincipal() instanceof UsuarioPrincipal p)
                || p.accesoRecuperacion() || p.usuarioId()==null
                || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("CARGO_ADMINISTRAR")))
            throw new ReglaNegocioException("La corrección requiere un usuario administrativo identificado con permiso de Adeudos de alumnos.");
        // Orden estable de bloqueos: cuota primero, igual que su edición.
        var referencia=obtener(id);
        if(!p.institucionId().equals(referencia.getInscripcion().getAlumno().getInstitucion().getId())
                || (!p.alcanceInstitucional() && !p.plantelIds().contains(referencia.getInscripcion().getPlantel().getId())))
            throw new ReglaNegocioException("El cargo no pertenece a tu alcance administrativo.");
        if(referencia.getCuotaAlumno()==null) throw new ReglaNegocioException("Esta corrección corresponde a cargos generados desde una cuota.");
        var cuota=cuotas.findByIdForUpdate(referencia.getCuotaAlumno().getId()).orElseThrow();
        var anterior=cargos.findByIdForUpdate(id).orElseThrow();
        verificar(anterior,version,"Cargo");
        if(cargos.findByReemplazaCargoId(id).isPresent()) throw new ReglaNegocioException("Este cargo ya tiene un reemplazo. Abre el cargo nuevo para continuar.");
        if(anterior.getEstadoRegistro()==EstadoRegistroCargo.CONVENIDO) throw new ReglaNegocioException("Un cargo de convenio no puede reemplazarse por esta vía.");
        if(!anterior.getAplicaciones().isEmpty()) throw new ReglaNegocioException("Este cargo tiene historial de pagos; no puede reemplazarse por esta vía.");
        if(cargos.tienePagoEnRevision(id)) throw new ReglaNegocioException("Hay un pago en revisión para este cargo. Resuélvelo antes de corregirlo.");
        if(anterior.getAjustes().stream().anyMatch(a -> a.getTipo()!=TipoAjusteCargo.BECA))
            throw new ReglaNegocioException("Este cargo tiene ajustes distintos de beca. Revisa su historial antes de corregirlo; no se trasladarán automáticamente.");
        if(cuota.getEstado()!=EstadoCuota.ACTIVA || !anterior.getConceptoCobro().isActivo()
                || (anterior.getInscripcion().getEstado()!=EstadoInscripcion.ACTIVA
                    && anterior.getInscripcion().getEstado()!=EstadoInscripcion.PREINSCRITA))
            throw new ReglaNegocioException("La corrección requiere cuota, concepto e inscripción vigentes.");
        var razon=limpiar(motivo);
        if(razon==null || razon.length()>2000) throw new ReglaNegocioException("Indica el motivo obligatorio de la corrección (máximo 2000 caracteres).");
        if(vencimiento==null || vencimiento.isBefore(anterior.getPeriodoCobroInicio()) || vencimiento.isAfter(anterior.getPeriodoCobroFin()))
            throw new ReglaNegocioException("La nueva fecha límite debe quedar dentro del periodo de cobro del cargo.");
        if(anterior.getEstadoRegistro()==EstadoRegistroCargo.EMITIDO && vencimiento.equals(anterior.getFechaVencimiento()))
            throw new ReglaNegocioException("La fecha no cambió. No es necesario reemplazar este cargo.");
        if(anterior.getEstadoRegistro()==EstadoRegistroCargo.EMITIDO) {
            anterior.setEstadoRegistro(EstadoRegistroCargo.CANCELADO); anterior.setCanceladoEn(Instant.now());
            anterior.setMotivoCancelacion("Corrección con reemplazo: " + razon.substring(0,Math.min(razon.length(),1970)));
        }
        Cargo nuevo=new Cargo(); nuevo.setInscripcion(anterior.getInscripcion()); nuevo.setConceptoCobro(anterior.getConceptoCobro());
        nuevo.setCuotaAlumno(cuota); nuevo.setClaveGeneracion("REEMPLAZO:"+id);
        nuevo.setDescripcion(anterior.getDescripcion()); nuevo.setPeriodoCobroInicio(anterior.getPeriodoCobroInicio());
        nuevo.setPeriodoCobroFin(anterior.getPeriodoCobroFin()); nuevo.setPeriodoAcademico(anterior.getPeriodoAcademico());
        // Conserva fecha de registro para no borrar antigüedad ni hacer inválido un vencimiento pasado.
        nuevo.setFechaEmision(anterior.getFechaEmision()); nuevo.setMotivoFechaRegistroDiferente(anterior.getMotivoFechaRegistroDiferente());
        nuevo.setFechaVencimiento(vencimiento); nuevo.setImporteOriginal(anterior.getImporteOriginal()); nuevo.setMoneda(anterior.getMoneda());
        nuevo.setReemplazaCargo(anterior); nuevo.setMotivoReemplazo(razon);
        cargos.saveAndFlush(nuevo); becas.aplicar(nuevo);
        return mapper.respuesta(nuevo);
    }
    private Cargo obtener(Long id) {return cargos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el cargo",id));}
}
