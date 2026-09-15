package escuela.cobranza.mapper;
import escuela.cobranza.dto.request.PoliticaRecargoRequest;import escuela.cobranza.dto.response.PoliticaRecargoResponse;import escuela.cobranza.entity.*;import org.springframework.stereotype.Component;
import static escuela.common.mapper.AuditoriaMapper.desde;import static escuela.common.mapper.NormalizacionTexto.codigo;
@Component public class PoliticaRecargoMapper{
 public PoliticaRecargo nueva(PoliticaRecargoRequest r,ConceptoCobro c){PoliticaRecargo p=new PoliticaRecargo();p.setConceptoCobro(c);actualizar(p,r);return p;}
 public void actualizar(PoliticaRecargo p,PoliticaRecargoRequest r){p.setModalidad(r.modalidad());p.setPorcentaje(r.modalidad()==ModalidadBeca.PORCENTAJE?r.porcentaje():null);p.setMontoFijo(r.modalidad()==ModalidadBeca.MONTO_FIJO?r.montoFijo():null);p.setMoneda(r.modalidad()==ModalidadBeca.MONTO_FIJO?codigo(r.moneda()):null);p.setDiasGracia(r.diasGracia());p.setPeriodicidad(r.periodicidad());p.setTipoLimite(r.tipoLimite());p.setValorLimite(r.tipoLimite()==TipoLimiteRecargo.SIN_LIMITE?null:r.valorLimite());p.setGeneracionAutomatica(r.generacionAutomatica());p.setActivo(r.activo());}
 public PoliticaRecargoResponse respuesta(PoliticaRecargo p){var c=p.getConceptoCobro();var i=c.getInstitucion();return new PoliticaRecargoResponse(p.getId(),i.getId(),i.getNombre(),c.getId(),c.getCodigo(),c.getNombre(),p.getModalidad(),p.getPorcentaje(),p.getMontoFijo(),p.getMoneda(),p.getDiasGracia(),p.getPeriodicidad(),p.getTipoLimite(),p.getValorLimite(),p.isGeneracionAutomatica(),p.isActivo(),desde(p));}
}
