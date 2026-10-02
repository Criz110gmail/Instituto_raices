package escuela.portal.service;

import escuela.admin.dto.*;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.CuentaFinanciera;
import escuela.finanzas.entity.TipoCuentaFinanciera;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.service.PagoService;
import escuela.institucion.service.InstitucionService;
import escuela.portal.dto.*;
import escuela.portal.repository.PortalTutorRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static escuela.cobranza.support.CalculoCargo.saldo;

@Service @RequiredArgsConstructor @Transactional
public class PortalPagoService {
    private final InstitucionService instituciones;
    private final PortalTutorRepository portal;
    private final TutorRepository tutores;
    private final CargoRepository cargos;
    private final CuentaFinancieraRepository cuentas;
    private final PagoService pagos;

    @Transactional(readOnly=true)
    public List<PortalPlantelPago> planteles(UsuarioPrincipal p) { validar(p); return portal.plantelesPago(p.usuarioId(),p.institucionId(),LocalDate.now(zona(p))); }

    @Transactional(readOnly=true)
    public ResultadoAutocompletado buscarCargos(UsuarioPrincipal p,String q) {
        validar(p); String texto=q==null?"":q.trim().toLowerCase(Locale.ROOT); if(!texto.isEmpty()&&texto.length()<3)return ResultadoAutocompletado.vacio();
        Tutor t=tutor(p); int limite=texto.isEmpty()?10:20; var r=cargos.buscarParaPortal(p.institucionId(),t.getId(),texto,PageRequest.of(0,limite));
        var opciones=r.getContent().stream().filter(c->saldo(c).signum()>0).map(c->new OpcionAutocompletado(c.getId(),
                c.getInscripcion().getAlumno().getMatricula()+" · "+c.getInscripcion().getAlumno().getNombres()+" "+c.getInscripcion().getAlumno().getPrimerApellido()
                        +" · "+c.getConceptoCobro().getNombre()+" · "+c.getDescripcion()+" · #"+c.getId(),
                "Vence "+c.getFechaVencimiento()+" · saldo "+saldo(c)+" "+c.getMoneda(), saldo(c))).toList();
        return new ResultadoAutocompletado(opciones,r.hasNext());
    }

    @Transactional(readOnly=true)
    public ResultadoAutocompletado buscarCuentas(UsuarioPrincipal p,Long plantelId,String q) {
        validar(p); if(plantelId==null)return ResultadoAutocompletado.vacio(); String texto=q==null?"":q.trim();
        var r=cuentas.buscarParaPago(p.institucionId(),plantelId,false,texto,PageRequest.of(0,20));
        return new ResultadoAutocompletado(r.getContent().stream().map(c->new OpcionAutocompletado(c.getId(),c.getNombre(),
                (c.getBancoNombre()==null?"":c.getBancoNombre()+" · ")+c.getMoneda())).toList(),r.hasNext());
    }

    public PagoResponse reportar(UsuarioPrincipal p, PortalPagoForm form, List<MultipartFile> files) {
        validar(p); Tutor t=tutor(p); var i=instituciones.obtener(p.institucionId()); ZoneId z=ZoneId.of(i.zonaHoraria());
        if(form.getSolicitudes()==null||form.getSolicitudes().isEmpty())throw new ReglaNegocioException("Selecciona al menos un cargo para aplicar la transferencia");
        normalizarImportes(p, t, form);
        if(files==null||files.stream().noneMatch(f->f!=null&&!f.isEmpty()))throw new ReglaNegocioException("Adjunta el comprobante de la transferencia");
        return pagos.registrarDesdePortal(form.request(i.id(),t.getId(),i.monedaPredeterminada(),z),files,p);
    }

    private void normalizarImportes(UsuarioPrincipal p, Tutor tutor, PortalPagoForm form) {
        Set<Long> seleccionados = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        for (PortalSolicitudPagoForm solicitud : form.getSolicitudes()) {
            if (solicitud.getCargoId() == null) {
                throw new ReglaNegocioException("Selecciona un cargo vigente de la lista");
            }
            if (!seleccionados.add(solicitud.getCargoId())) {
                throw new ReglaNegocioException("Un cargo sólo puede seleccionarse una vez");
            }
            Cargo cargo = cargos.buscarVigenteParaPortal(solicitud.getCargoId(), p.institucionId(), tutor.getId())
                    .orElseThrow(() -> new ReglaNegocioException(
                            "Uno de los cargos ya no está vigente o no pertenece a tu cuenta familiar"));
            BigDecimal pendiente = saldo(cargo);
            if (pendiente.signum() <= 0) {
                throw new ReglaNegocioException("Uno de los cargos seleccionados ya no tiene saldo pendiente");
            }
            solicitud.setMontoSolicitado(pendiente);
            total = total.add(pendiente);
        }
        form.setMonto(total);
    }
    @Transactional(readOnly=true)
    public PagoResponse comprobante(UsuarioPrincipal p, Long pagoId) {
        validar(p);
        Tutor tutor = tutor(p);
        PagoResponse pago = pagos.obtener(pagoId);
        if (!p.institucionId().equals(pago.institucionId()) || !tutor.getId().equals(pago.tutorId())) {
            throw new AccessDeniedException("El pago no pertenece a la cuenta familiar");
        }
        if (pago.estado() != escuela.finanzas.entity.EstadoPago.VALIDADO) {
            throw new ReglaNegocioException("El comprobante oficial sólo está disponible para pagos validados");
        }
        return pago;
    }
    private Tutor tutor(UsuarioPrincipal p){return tutores.findByUsuarioIdAndInstitucionIdAndActivoTrue(p.usuarioId(),p.institucionId()).orElseThrow(()->new AccessDeniedException("La cuenta no está vinculada a un tutor activo"));}
    private ZoneId zona(UsuarioPrincipal p){return ZoneId.of(instituciones.obtener(p.institucionId()).zonaHoraria());}
    private void validar(UsuarioPrincipal p){if(p==null||p.usuarioId()==null||p.institucionId()==null||p.accesoRecuperacion())throw new AccessDeniedException("El portal familiar requiere una cuenta activa");}
}
