package escuela.finanzas.service;

import escuela.finanzas.dto.request.TransferenciaCuentaRequest;
import escuela.finanzas.dto.response.TransferenciaCuentaResponse;

public interface TransferenciaCuentaService {
    TransferenciaCuentaResponse transferir(TransferenciaCuentaRequest request);
}
