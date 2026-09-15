package escuela.cobranza.service.impl;

import escuela.cobranza.dto.request.AjusteCargoRequest;
import escuela.cobranza.dto.response.AjusteCargoResponse;
import escuela.cobranza.entity.*;
import escuela.cobranza.mapper.AjusteCargoMapper;
import escuela.cobranza.repository.*;
import escuela.cobranza.service.AjusteCargoService;
import escuela.common.exception.*;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.LocalDate;
import java.util.List;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class AjusteCargoServiceImpl implements AjusteCargoService {
    private final AjusteCargoRepository repository; private final CargoRepository cargoRepository;
    private final UsuarioRepository usuarioRepository; private final AjusteCargoMapper mapper;
    public AjusteCargoResponse crear(AjusteCargoRequest r){Cargo c=cargoRepository.findByIdForUpdate(r.cargoId()).orElseThrow(()->new RecursoNoEncontradoException("el cargo",r.cargoId())); validar(c,r);
        AjusteCargo a=new AjusteCargo();a.setCargo(c);a.setTipo(r.tipo());a.setEfecto(efecto(r));a.setMonto(r.monto().setScale(2,RoundingMode.UNNECESSARY));a.setMotivo(limpiar(r.motivo()));a.setFechaEfectiva(r.fechaEfectiva());a.setAutorizadoPor(actor());return mapper.respuesta(repository.saveAndFlush(a));}
    public AjusteCargoResponse reversar(Long id,Long version,String motivo){AjusteCargo original=repository.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("el ajuste",id));verificar(original,version,"Ajuste del cargo");
        if(original.getReversaDe()!=null) throw new ReglaNegocioException("Una reversa no puede volver a reversarse"); if(repository.existsByReversaDeId(id)) throw new ReglaNegocioException("Este ajuste ya fue reversado");
        String m=limpiar(motivo);if(m==null||m.length()>2000) throw new ReglaNegocioException("Indica un motivo de reversa de máximo 2000 caracteres");
        if(original.getCargo().getEstadoRegistro()==EstadoRegistroCargo.CANCELADO) throw new ReglaNegocioException("No se pueden modificar los ajustes de un cargo cancelado");
        AjusteCargo a=new AjusteCargo();a.setCargo(original.getCargo());a.setTipo(TipoAjusteCargo.CORRECCION);a.setEfecto(original.getEfecto()==EfectoAjusteCargo.AUMENTO?EfectoAjusteCargo.DISMINUCION:EfectoAjusteCargo.AUMENTO);a.setMonto(original.getMonto());a.setBaseCalculo(original.getBaseCalculo());a.setPorcentajeAplicado(original.getPorcentajeAplicado());a.setMotivo("Reversa: "+m);a.setAutorizadoPor(actor());a.setFechaEfectiva(LocalDate.now());a.setReversaDe(original);
        validarTotal(original.getCargo(),a.getEfecto(),a.getMonto());return mapper.respuesta(repository.saveAndFlush(a));}
    @Transactional(readOnly=true) public AjusteCargoResponse obtener(Long id){return mapper.respuesta(repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el ajuste",id)));}
    @Transactional(readOnly=true) public List<AjusteCargoResponse> listarPorCargo(Long cargoId){return repository.findAll((root,q,cb)->cb.equal(root.get("cargo").get("id"),cargoId),org.springframework.data.domain.Sort.by("id")).stream().map(mapper::respuesta).toList();}
    private void validar(Cargo c,AjusteCargoRequest r){if(c.getEstadoRegistro()==EstadoRegistroCargo.CANCELADO) throw new ReglaNegocioException("No se puede ajustar un cargo cancelado");if(r.tipo()==TipoAjusteCargo.BECA) throw new ReglaNegocioException("Las becas se aplican desde la asignación del alumno");
        if(r.tipo()==TipoAjusteCargo.DESCUENTO&&!c.getConceptoCobro().isPermiteDescuento()) throw new ReglaNegocioException("El concepto del cargo no permite descuentos");if(r.tipo()==TipoAjusteCargo.RECARGO&&!c.getConceptoCobro().isPermiteRecargo()) throw new ReglaNegocioException("El concepto del cargo no permite recargos");
        if(r.monto().scale()>2) throw new ReglaNegocioException("El monto debe tener máximo dos decimales");validarTotal(c,efecto(r),r.monto());}
    private EfectoAjusteCargo efecto(AjusteCargoRequest r){return r.tipo()==TipoAjusteCargo.DESCUENTO?EfectoAjusteCargo.DISMINUCION:r.tipo()==TipoAjusteCargo.RECARGO?EfectoAjusteCargo.AUMENTO:r.efecto();}
    private void validarTotal(Cargo c,EfectoAjusteCargo e,BigDecimal monto){BigDecimal total=total(c);BigDecimal nuevo=e==EfectoAjusteCargo.AUMENTO?total.add(monto):total.subtract(monto);if(nuevo.signum()<0) throw new ReglaNegocioException("El ajuste no puede dejar el total del cargo por debajo de cero");}
    private BigDecimal total(Cargo c){BigDecimal t=c.getImporteOriginal();for(AjusteCargo a:c.getAjustes())t=a.getEfecto()==EfectoAjusteCargo.AUMENTO?t.add(a.getMonto()):t.subtract(a.getMonto());return t;}
    private Usuario actor(){var a=SecurityContextHolder.getContext().getAuthentication();if(a!=null&&a.getPrincipal() instanceof UsuarioPrincipal p&&!p.accesoRecuperacion())return usuarioRepository.findById(p.usuarioId()).orElse(null);return null;}
}
