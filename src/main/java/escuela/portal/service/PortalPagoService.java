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
import java.time.*;
import java.util.*;

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
        validar(p); String texto=q==null?"":q.trim().toLowerCase(Locale.ROOT); if(texto.length()<3)return ResultadoAutocompletado.vacio();
        Tutor t=tutor(p); var r=cargos.buscarParaPortal(p.institucionId(),t.getId(),texto,PageRequest.of(0,20));
        return new ResultadoAutocompletado(r.getContent().stream().map(c->new OpcionAutocompletado(c.getId(),
                c.getInscripcion().getAlumno().getMatricula()+" · "+c.getInscripcion().getAlumno().getNombres()+" "+c.getInscripcion().getAlumno().getPrimerApellido(),
                c.getDescripcion()+" · vence "+c.getFechaVencimiento()+" · "+c.getImporteOriginal()+" "+c.getMoneda())).toList(),r.hasNext());
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
        var total=form.getSolicitudes().stream().map(PortalSolicitudPagoForm::getMontoSolicitado).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add);
        if(form.getMonto()==null||total.compareTo(form.getMonto())!=0)throw new ReglaNegocioException("La distribución de cargos debe coincidir exactamente con el monto de la transferencia");
        if(files==null||files.stream().noneMatch(f->f!=null&&!f.isEmpty()))throw new ReglaNegocioException("Adjunta el comprobante de la transferencia");
        return pagos.registrarDesdePortal(form.request(i.id(),t.getId(),i.monedaPredeterminada(),z),files,p);
    }
    private Tutor tutor(UsuarioPrincipal p){return tutores.findByUsuarioIdAndInstitucionIdAndActivoTrue(p.usuarioId(),p.institucionId()).orElseThrow(()->new AccessDeniedException("La cuenta no está vinculada a un tutor activo"));}
    private ZoneId zona(UsuarioPrincipal p){return ZoneId.of(instituciones.obtener(p.institucionId()).zonaHoraria());}
    private void validar(UsuarioPrincipal p){if(p==null||p.usuarioId()==null||p.institucionId()==null||p.accesoRecuperacion())throw new AccessDeniedException("El portal familiar requiere una cuenta activa");}
}
