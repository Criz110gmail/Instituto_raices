package escuela.finanzas.service;

import escuela.finanzas.repository.PagoRepository;
import escuela.finanzas.dto.response.CoberturaAnualPagoResponse;
import escuela.finanzas.entity.Pago;
import escuela.admin.dto.*;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class CoberturaAnualPagoService {
 private final PagoRepository pagos;
 private final AlcanceDatosService alcance;
 public List<CoberturaAnualPagoResponse> dePago(Long id){alcance.validarRecurso(ModuloCatalogo.PAGOS,id);return lote(List.of(id)).getOrDefault(id,List.of());}
 /** Uso interno: IDs de una página que ya pasó el alcance administrativo de Pagos. */
 public Map<Long,List<CoberturaAnualPagoResponse>> lote(Collection<Long> ids){
  if(ids.isEmpty())return Map.of();
  if(ids.size()>100)throw new IllegalArgumentException("La cobertura se consulta por páginas de máximo100 pagos");
  return pagos.coberturasAnuales(ids).stream().map(r->new CoberturaAnualPagoResponse(r.getPagoId(),r.getAlumnoId(),r.getAlumno(),r.getMatricula(),r.getConcepto(),r.getAnio(),r.getMeses(),r.getMesesPagados(),r.getMesesBonificados(),r.getBeneficio(),r.getAplicado(),r.getMoneda(),r.getEstado())).collect(Collectors.groupingBy(CoberturaAnualPagoResponse::pagoId));
 }
 public static Specification<Pago> filtro(FiltroCatalogo f){
  f.validarCobertura();
  return (root,q,cb)->f.cobertura().equals("TODOS")&&f.anioCobertura()==null?cb.conjunction():cb.isTrue(cb.function("pago_coincide_cobertura",Boolean.class,root.get("id"),f.anioCobertura()==null?cb.nullLiteral(Integer.class):cb.literal(f.anioCobertura()),cb.literal(f.cobertura())));
 }
}
