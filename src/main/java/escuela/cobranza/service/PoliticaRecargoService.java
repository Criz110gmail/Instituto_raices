package escuela.cobranza.service;
import escuela.cobranza.dto.request.*;import escuela.cobranza.dto.response.*;
public interface PoliticaRecargoService{PoliticaRecargoResponse crear(PoliticaRecargoRequest r);PoliticaRecargoResponse actualizar(Long id,PoliticaRecargoRequest r);PoliticaRecargoResponse obtener(Long id);void desactivar(Long id,Long version);GeneracionRecargosResponse generar(GeneracionRecargosRequest r);}
