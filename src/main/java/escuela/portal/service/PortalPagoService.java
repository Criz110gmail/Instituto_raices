package escuela.portal.service;

import escuela.admin.dto.*;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.service.PagoService;
import escuela.institucion.service.InstitucionService;
import escuela.portal.dto.*;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static escuela.cobranza.support.CalculoCargo.saldo;
import static escuela.common.support.FormatoMoneda.formatear;

@Service @RequiredArgsConstructor @Transactional
public class PortalPagoService {
    private final InstitucionService instituciones;
    private final TutorRepository tutores;
    private final CargoRepository cargos;
    private final CuentaFinancieraRepository cuentas;
    private final PagoService pagos;

    @Transactional(readOnly=true)
    public ResultadoAutocompletado buscarCargos(UsuarioPrincipal p,String q) {
        validar(p); String texto=q==null?"":q.trim().toLowerCase(Locale.ROOT); if(!texto.isEmpty()&&texto.length()<3)return ResultadoAutocompletado.vacio();
        Tutor t=tutor(p); int limite=texto.isEmpty()?10:20; var r=cargos.buscarParaPortal(p.institucionId(),t.getId(),texto,PageRequest.of(0,limite));
        var hoy=java.time.LocalDate.now(ZoneId.of(instituciones.obtener(p.institucionId()).zonaHoraria()));
        var opciones=r.getContent().stream().filter(c->saldo(c).signum()>0).map(c->new OpcionAutocompletado(c.getId(),
                c.getInscripcion().getAlumno().getMatricula()+" · "+c.getInscripcion().getAlumno().getNombres()+" "+c.getInscripcion().getAlumno().getPrimerApellido()
                        +" · "+c.getConceptoCobro().getNombre()+" · "+c.getDescripcion()+" · #"+c.getId(),
                detalle(c,hoy), saldo(c))).toList();
        return new ResultadoAutocompletado(opciones,r.hasNext());
    }

    @Transactional(readOnly=true)
    public ResultadoAutocompletado buscarCuentas(UsuarioPrincipal p,Long cargoId,String q) {
        validar(p); if(cargoId==null)return ResultadoAutocompletado.vacio();
        Tutor tutor=tutor(p);
        Cargo cargo=cargos.buscarVigenteParaPortal(cargoId,p.institucionId(),tutor.getId())
                .orElseThrow(()->new ReglaNegocioException("El cargo ya no está vigente o no pertenece a tu cuenta familiar"));
        Long plantelId=cargo.getInscripcion().getPlantel().getId();
        String texto=q==null?"":q.trim();
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
        // El mismo orden que la validación administrativa. Mantener los bloqueos hasta guardar
        // el reporte: dos envíos concurrentes no pueden reservar el mismo cargo.
        form.getSolicitudes().stream().map(PortalSolicitudPagoForm::getCargoId)
                .filter(Objects::nonNull).distinct().sorted().forEach(id -> {
                    cargos.findByIdForUpdate(id).orElseThrow(() -> new ReglaNegocioException(
                            "Uno de los cargos ya no está vigente o no pertenece a tu cuenta familiar"));
                });
        Set<Long> seleccionados = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        boolean saldoCambio = false;
        Long plantelId = null;
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
            if(cargos.tienePagoEnRevision(cargo.getId())) {
                throw new ReglaNegocioException("Este cargo ya tiene un pago en revisión. Espera la respuesta de administración antes de reportarlo otra vez.");
            }
            if (pendiente.signum() <= 0) {
                throw new ReglaNegocioException("Uno de los cargos seleccionados ya no tiene saldo pendiente");
            }
            saldoCambio |= solicitud.getMontoSolicitado()==null || solicitud.getMontoSolicitado().compareTo(pendiente)!=0;
            solicitud.setMontoSolicitado(pendiente);
            Long plantelCargo = cargo.getInscripcion().getPlantel().getId();
            if (plantelId == null) {
                plantelId = plantelCargo;
            } else if (!plantelId.equals(plantelCargo)) {
                throw new ReglaNegocioException(
                        "Los cargos pertenecen a planteles distintos. Reporta una transferencia separada por cada plantel");
            }
            total = total.add(pendiente);
        }
        form.setPlantelRegistroId(plantelId);
        form.setMonto(total);
        if(saldoCambio) throw new ReglaNegocioException("El saldo cambió desde que seleccionaste los cargos. Actualizamos el importe; revisa el total, confirma que coincide con tu transferencia y vuelve a adjuntar el comprobante antes de enviar.");
    }
    private String detalle(Cargo c,java.time.LocalDate hoy) {
        BigDecimal descuentos=BigDecimal.ZERO, aumentos=BigDecimal.ZERO;
        for(var a:c.getAjustes()) {
            if(a.getEfecto()==escuela.cobranza.entity.EfectoAjusteCargo.AUMENTO) aumentos=aumentos.add(a.getMonto());
            else descuentos=descuentos.add(a.getMonto());
        }
        return "Plantel "+c.getInscripcion().getPlantel().getNombre()+" · "
                +(c.getFechaVencimiento().isBefore(hoy)?"Vencido · transferencia habilitada · ":"")
                +"Fecha límite "+c.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                +" · Original "+formatear(c.getImporteOriginal())+" · Becas/descuentos −"+formatear(descuentos)
                +" · Recargos/ajustes aplicados +"+formatear(aumentos)
                +" · Abonos "+formatear(escuela.cobranza.support.CalculoCargo.aplicado(c))
                +" · Saldo "+formatear(saldo(c))+" "+c.getMoneda();
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
    private void validar(UsuarioPrincipal p){if(p==null||p.usuarioId()==null||p.institucionId()==null||p.accesoRecuperacion())throw new AccessDeniedException("El portal familiar requiere una cuenta activa");}
}
