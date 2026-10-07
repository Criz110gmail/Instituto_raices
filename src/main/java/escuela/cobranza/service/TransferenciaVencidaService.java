package escuela.cobranza.service;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.CargoRepository;
import escuela.common.exception.*;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import static escuela.cobranza.support.CalculoCargo.saldo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor
public class TransferenciaVencidaService {
    private final CargoRepository cargos;
    private final RegistroAuditoriaService auditoria;

    public record Configuracion(boolean plantelPermite, boolean excepcionAutorizada) { }

    @Transactional(readOnly=true)
    public Configuracion consultar(Long id) {
        var cargo=cargos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el cargo",id));
        return new Configuracion(cargo.getInscripcion().getPlantel().isPermitirTransferenciasVencidas(),
                cargo.isTransferenciaVencidaAutorizada());
    }

    @Transactional
    public void configurar(Long id, Long version, boolean permitir, String motivo) {
        var cargo=cargos.findByIdForUpdate(id).orElseThrow(() -> new RecursoNoEncontradoException("el cargo",id));
        exigirActor(cargo); verificar(cargo,version,"Cargo");
        if(cargo.getEstadoRegistro()!=EstadoRegistroCargo.EMITIDO || saldo(cargo).signum()<=0)
            throw new ReglaNegocioException("Sólo se autoriza una transferencia para un cargo emitido con saldo pendiente.");
        String razon=motivo==null?"":motivo.trim();
        if(razon.isEmpty() || razon.length()>2000)
            throw new ReglaNegocioException("Indica el motivo obligatorio (máximo 2000 caracteres).");
        if(cargo.isTransferenciaVencidaAutorizada()==permitir) return;
        boolean anterior=cargo.isTransferenciaVencidaAutorizada();
        cargo.setTransferenciaVencidaAutorizada(permitir);
        cargos.saveAndFlush(cargo);
        auditoria.registrar(cargo.getInscripcion().getAlumno().getInstitucion().getId(),
                AccionAuditoria.TRANSFERENCIA_VENCIDA_AUTORIZADA,"CARGO",id,razon,
                Map.of("autorizacionAnterior",anterior,"autorizacionNueva",permitir));
    }

    private void exigirActor(Cargo cargo) {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null || !(auth.getPrincipal() instanceof UsuarioPrincipal p)
                || p.accesoRecuperacion() || p.usuarioId()==null
                || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("CARGO_ADMINISTRAR")))
            throw new ReglaNegocioException("La autorización requiere un administrador identificado con permiso de Adeudos de alumnos.");
        if(!cargo.getInscripcion().getAlumno().getInstitucion().getId().equals(p.institucionId())
                || (!p.alcanceInstitucional() && !p.plantelIds().contains(cargo.getInscripcion().getPlantel().getId())))
            throw new org.springframework.security.access.AccessDeniedException("El cargo no pertenece a tu alcance.");
    }
}
