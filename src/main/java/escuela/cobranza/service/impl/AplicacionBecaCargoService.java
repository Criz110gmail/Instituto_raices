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
import java.time.LocalDate;

@Service @RequiredArgsConstructor
public class AplicacionBecaCargoService {
    private final BecaAlumnoRepository becaRepository; private final AjusteCargoRepository ajusteRepository;
    private final UsuarioRepository usuarioRepository;

    /** Consulta y cálculo compartidos por la vista previa y la emisión, sin escribir ajustes. */
    @Transactional(readOnly = true)
    public CalculoBeca previsualizar(Long inscripcionId, Long conceptoId, LocalDate inicio,
                                     LocalDate fin, BigDecimal importeOriginal) {
        var becas = becaRepository.buscarAplicable(inscripcionId, conceptoId, inicio, fin);
        if (becas.isEmpty()) return new CalculoBeca(null, BigDecimal.ZERO.setScale(2));
        BecaAlumno beca = becas.getFirst();
        BigDecimal monto = beca.getModalidad() == ModalidadBeca.PORCENTAJE
                ? importeOriginal.multiply(beca.getPorcentaje()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                : beca.getMontoFijo().min(importeOriginal).setScale(2, RoundingMode.HALF_UP);
        return new CalculoBeca(beca, monto);
    }

    public record CalculoBeca(BecaAlumno beca, BigDecimal monto) {
        public String descripcion() {
            if (beca == null) return "Sin beca aplicable";
            return beca.getTipoBeca().getNombre() + (beca.getModalidad() == ModalidadBeca.PORCENTAJE
                    ? " · " + beca.getPorcentaje().stripTrailingZeros().toPlainString() + "%"
                    : " · monto fijo");
        }
    }

    @Transactional
    public void aplicar(Cargo cargo) {
        var calculo = previsualizar(cargo.getInscripcion().getId(), cargo.getConceptoCobro().getId(),
                cargo.getPeriodoCobroInicio(), cargo.getPeriodoCobroFin(), cargo.getImporteOriginal());
        BecaAlumno beca=calculo.beca();
        if(beca == null) return;
        if(ajusteRepository.existsByCargoIdAndBecaAlumnoIdAndReversaDeIsNull(cargo.getId(),beca.getId())) return;
        BigDecimal monto=calculo.monto();
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
