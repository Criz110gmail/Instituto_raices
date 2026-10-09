package escuela.finanzas.dto.request;
import java.util.List;
public record AplicarSaldoFavorRequest(Long version,String clave,String motivo,List<SolicitudAplicacionPagoRequest> cargos) { }
