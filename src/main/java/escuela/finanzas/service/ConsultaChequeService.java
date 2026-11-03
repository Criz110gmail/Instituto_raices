package escuela.finanzas.service;

import escuela.finanzas.dto.FiltroCheque;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.data.domain.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Consulta bancaria de documentos. No concede acceso a expedientes ni cálculos salariales. */
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ConsultaChequeService {
 private final NamedParameterJdbcTemplate jdbc;
 private final ChequeraService chequeras;
 public record Cheque(Long folio,String chequera,LocalDate fecha,String beneficiario,BigDecimal importe,String moneda,Long pago,boolean cancelado,String motivo,String fechaCancelacion){}
 public record Totales(long emitidos,long cancelados,BigDecimal importeEmitido,BigDecimal importeCancelado){}
 private static final String DESDE=" FROM cheque_nomina ch LEFT JOIN cheque_nomina_cancelacion cc ON cc.cheque_id=ch.id WHERE ch.cuenta_id=:cuenta AND ch.institucion_id=:inst AND (:chequera IS NULL OR ch.chequera_id=:chequera) AND (:folio IS NULL OR ch.folio=:folio) AND (:estado='TODOS' OR (:estado='CANCELADO')=(cc.cheque_id IS NOT NULL)) AND (:desde IS NULL OR ch.fecha_emision>=:desde) AND (:hasta IS NULL OR ch.fecha_emision<=:hasta) AND strpos(lower(ch.beneficiario||' '||ch.chequera_nombre||' '||ch.folio||' NPI-'||ch.pago_id),lower(:q))>0";
 private MapSqlParameterSource parametros(Long cuenta,FiltroCheque f){
  var c=chequeras.cuenta(cuenta);
  if(f.chequeraId()!=null)chequeras.obtener(cuenta,f.chequeraId());
  if(f.chequeraId()==null&&!f.chequeraTexto().isBlank())throw new ReglaNegocioException("Selecciona la chequera de las sugerencias o limpia ese campo.");
  if(f.folio()!=null&&(f.folio()<1||f.folio()>999999999999L))throw new ReglaNegocioException("El folio debe estar entre 1 y 999999999999.");
  if(f.desde()!=null&&f.hasta()!=null&&f.desde().isAfter(f.hasta()))throw new ReglaNegocioException("La fecha inicial de emisión no puede ser posterior a la final.");
  return new MapSqlParameterSource("cuenta",c.id()).addValue("inst",c.institucion()).addValue("q",f.q()).addValue("estado",f.estado()).addValue("chequera",f.chequeraId(),java.sql.Types.BIGINT).addValue("folio",f.folio(),java.sql.Types.BIGINT).addValue("desde",f.desde(),java.sql.Types.DATE).addValue("hasta",f.hasta(),java.sql.Types.DATE);
 }
 public Page<Cheque> listar(Long cuenta,FiltroCheque original){var f=original.normalizado();var p=parametros(cuenta,f);var pg=PageRequest.of(f.pagina(),f.tamanio());long n=jdbc.queryForObject("SELECT count(*)"+DESDE,p,Long.class);p.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset());
  return new PageImpl<>(jdbc.query("SELECT ch.*,cc.cheque_id IS NOT NULL cancelado,cc.motivo motivo_cancelacion,to_char(cc.ocurrido_en AT TIME ZONE (SELECT zona_horaria FROM institucion WHERE id=:inst),'DD/MM/YYYY HH24:MI') fecha_cancelacion"+DESDE+" ORDER BY ch.fecha_emision DESC,ch.id DESC LIMIT :limite OFFSET :offset",p,(r,i)->new Cheque(r.getLong("folio"),r.getString("chequera_nombre"),r.getObject("fecha_emision",LocalDate.class),r.getString("beneficiario"),r.getBigDecimal("importe"),r.getString("moneda"),r.getLong("pago_id"),r.getBoolean("cancelado"),r.getString("motivo_cancelacion"),r.getString("fecha_cancelacion"))),pg,n);
 }
 public Totales totales(Long cuenta,FiltroCheque original){var f=original.normalizado();return jdbc.queryForObject("SELECT count(*) FILTER(WHERE cc.cheque_id IS NULL) emitidos,count(*) FILTER(WHERE cc.cheque_id IS NOT NULL) cancelados,coalesce(sum(ch.importe) FILTER(WHERE cc.cheque_id IS NULL),0) importe_emitido,coalesce(sum(ch.importe) FILTER(WHERE cc.cheque_id IS NOT NULL),0) importe_cancelado"+DESDE,parametros(cuenta,f),(r,i)->new Totales(r.getLong("emitidos"),r.getLong("cancelados"),r.getBigDecimal("importe_emitido"),r.getBigDecimal("importe_cancelado")));}
}
