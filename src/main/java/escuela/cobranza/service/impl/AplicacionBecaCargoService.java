package escuela.cobranza.service.impl;

import escuela.cobranza.entity.*;
import escuela.cobranza.repository.*;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;

@Service @RequiredArgsConstructor
public class AplicacionBecaCargoService {
    private final BecaAlumnoRepository becaRepository; private final AjusteCargoRepository ajusteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void aplicar(Cargo cargo) {
        var becas=becaRepository.buscarAplicable(cargo.getInscripcion().getId(),cargo.getConceptoCobro().getId(),cargo.getPeriodoCobroInicio(),cargo.getPeriodoCobroFin());
        if(becas.isEmpty()) return;
        BecaAlumno beca=becas.getFirst();
        if(ajusteRepository.existsByCargoIdAndBecaAlumnoIdAndReversaDeIsNull(cargo.getId(),beca.getId())) return;
        BigDecimal monto=beca.getModalidad()==ModalidadBeca.PORCENTAJE
                ? cargo.getImporteOriginal().multiply(beca.getPorcentaje()).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP)
                : beca.getMontoFijo().min(cargo.getImporteOriginal()).setScale(2,RoundingMode.HALF_UP);
        if(monto.signum()==0) return;
        AjusteCargo a=new AjusteCargo(); a.setCargo(cargo); a.setTipo(TipoAjusteCargo.BECA);
        a.setEfecto(EfectoAjusteCargo.DISMINUCION); a.setMonto(monto); a.setBaseCalculo(cargo.getImporteOriginal());
        a.setPorcentajeAplicado(beca.getModalidad()==ModalidadBeca.PORCENTAJE?beca.getPorcentaje():null);
        a.setBecaAlumno(beca); a.setMotivo("Beca " + beca.getTipoBeca().getNombre() + " · " + beca.getMotivo());
        a.setAutorizadoPor(actor()); a.setFechaEfectiva(cargo.getFechaEmision()); ajusteRepository.saveAndFlush(a);
        cargo.getAjustes().add(a);
    }
    private Usuario actor(){var a=SecurityContextHolder.getContext().getAuthentication();if(a!=null&&a.getPrincipal() instanceof UsuarioPrincipal p&&!p.accesoRecuperacion()) return usuarioRepository.findById(p.usuarioId()).orElse(null);return null;}
}
