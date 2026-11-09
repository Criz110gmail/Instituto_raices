package escuela.cobranza.service;

import escuela.cobranza.dto.request.CargoManualRequest;
import escuela.cobranza.dto.request.GeneracionCargosRequest;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.dto.response.GeneracionCargosResponse;
import escuela.cobranza.dto.response.VistaPreviaCargosAutomaticosResponse;

public interface CargoService {
    java.util.List<escuela.cobranza.dto.FilaAnticipoInscripcion> anticipables(Long id,java.time.LocalDate desde,java.time.LocalDate hasta);
    java.util.List<CargoResponse> confirmarAnticipo(Long id,java.time.LocalDate desde,java.time.LocalDate hasta,java.util.List<escuela.cobranza.dto.FilaAnticipoInscripcion> esperadas,java.util.Set<String> elegidas);
    CargoResponse crearManual(CargoManualRequest request);
    GeneracionCargosResponse generar(GeneracionCargosRequest request);
    VistaPreviaCargosAutomaticosResponse previsualizar(GeneracionCargosRequest request,
                                                        int pagina, int tamanio);
    CargoResponse generarCargoUnico(Long cuotaId);
    CargoResponse obtener(Long id);
    CargoResponse cancelar(Long id, Long version, String motivo);
    Long reemplazoDe(Long id);
    org.springframework.data.domain.Page<CuotaExcluida> diagnosticar(GeneracionCargosRequest request,int pagina);
    record CuotaExcluida(Long cuotaId,String alumno,String concepto,String motivo,Long cargoId) { }
}
