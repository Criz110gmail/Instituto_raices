package escuela.cobranza.dto.response;
import escuela.cobranza.entity.*;import escuela.common.dto.response.AuditoriaResponse;import java.math.BigDecimal;
public record PoliticaRecargoResponse(Long id,Long institucionId,String institucionNombre,Long conceptoCobroId,
 String conceptoCodigo,String conceptoNombre,ModalidadBeca modalidad,BigDecimal porcentaje,BigDecimal montoFijo,
 String moneda,int diasGracia,PeriodicidadRecargo periodicidad,TipoLimiteRecargo tipoLimite,
 BigDecimal valorLimite,boolean generacionAutomatica,boolean activo,AuditoriaResponse auditoria){}
