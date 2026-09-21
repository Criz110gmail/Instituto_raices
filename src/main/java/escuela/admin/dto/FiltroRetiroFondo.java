package escuela.admin.dto;

import escuela.common.exception.ReglaNegocioException;

import java.time.LocalDate;
import java.util.Set;

public record FiltroRetiroFondo(Long institucionId, Long cuentaId, String cuentaTexto,
                                Long plantelId, LocalDate fechaDesde, LocalDate fechaHasta,
                                String beneficiario, int pagina, int tamanio) {
    public FiltroRetiroFondo normalizado() {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta))
            throw new ReglaNegocioException("La fecha inicial no puede ser posterior a la final");
        int tam = Set.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25;
        String texto = cuentaTexto == null ? "" : cuentaTexto.trim();
        String persona = beneficiario == null ? "" : beneficiario.trim();
        if (persona.length() > 180) throw new ReglaNegocioException("El filtro de destinatario es demasiado largo");
        return new FiltroRetiroFondo(institucionId, cuentaId, texto, plantelId,
                fechaDesde, fechaHasta, persona, Math.max(0, pagina), tam);
    }

    public FiltroRetiroFondo conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroRetiroFondo(institucionId, cuentaId, cuentaTexto, plantelId,
                fechaDesde, fechaHasta, beneficiario, nuevaPagina, nuevoTamanio);
    }
}
