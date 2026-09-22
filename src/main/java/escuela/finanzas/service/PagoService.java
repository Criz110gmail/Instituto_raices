package escuela.finanzas.service;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.finanzas.dto.request.PagoRequest;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.seguridad.service.UsuarioPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PagoService {
    PagoResponse registrar(PagoRequest request, List<MultipartFile> comprobantes);
    PagoResponse registrarDesdePortal(PagoRequest request, List<MultipartFile> comprobantes,
                                      UsuarioPrincipal principal);
    PagoResponse obtener(Long id);
    ArchivoDescarga descargarComprobante(Long pagoId, Long comprobanteId);
}
