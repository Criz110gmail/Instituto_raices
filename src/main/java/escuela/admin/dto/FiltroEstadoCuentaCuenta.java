package escuela.admin.dto;

import escuela.common.exception.ReglaNegocioException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;

public record FiltroEstadoCuentaCuenta(Long institucionId, Long cuentaId, String cuentaTexto,
                                      String periodo, int anio, Integer mes, int pagina, int tamanio) {
    public FiltroEstadoCuentaCuenta normalizado(LocalDate hoy) {
        String tipo = periodo == null ? "MENSUAL" : periodo.toUpperCase();
        if (!Set.of("MENSUAL", "ANUAL").contains(tipo))
            throw new ReglaNegocioException("Selecciona un periodo mensual o anual válido");
        int ejercicio = anio == 0 ? hoy.getYear() : anio;
        if (ejercicio < 2000 || ejercicio > hoy.getYear() + 1)
            throw new ReglaNegocioException("El año debe estar entre 2000 y el próximo año");
        int mesElegido = mes == null ? hoy.getMonthValue() : mes;
        if (tipo.equals("MENSUAL") && (mesElegido < 1 || mesElegido > 12))
            throw new ReglaNegocioException("Selecciona un mes válido");
        int tam = Set.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25;
        String texto = cuentaTexto == null ? "" : cuentaTexto.trim().replaceAll("\\s+", " ");
        return new FiltroEstadoCuentaCuenta(institucionId, cuentaId, texto, tipo, ejercicio,
                tipo.equals("ANUAL") ? null : mesElegido, Math.max(0, pagina), tam);
    }

    public LocalDate desde() {
        return periodo.equals("ANUAL") ? LocalDate.of(anio, 1, 1) : YearMonth.of(anio, mes).atDay(1);
    }

    public LocalDate hasta() {
        return periodo.equals("ANUAL") ? LocalDate.of(anio, 12, 31) : YearMonth.of(anio, mes).atEndOfMonth();
    }

    public FiltroEstadoCuentaCuenta conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroEstadoCuentaCuenta(institucionId, cuentaId, cuentaTexto, periodo, anio,
                mes, nuevaPagina, nuevoTamanio);
    }
}
