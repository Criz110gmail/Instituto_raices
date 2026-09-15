package escuela.admin.dto;

import escuela.finanzas.entity.ClaseMovimiento;
import escuela.finanzas.entity.DireccionMovimiento;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Arrays;

public record FiltroMovimientoFinanciero(
        Long institucionId,
        Long cuentaId,
        String cuentaTexto,
        Long plantelId,
        String direccion,
        String clase,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        int pagina,
        int tamanio
) {
    public FiltroMovimientoFinanciero normalizado() {
        String cuenta = cuentaTexto == null ? "" : cuentaTexto.trim().replaceAll("\\s+", " ");
        return new FiltroMovimientoFinanciero(institucionId, cuentaId, cuenta, plantelId,
                enumValido(direccion, DireccionMovimiento.values()),
                enumValido(clase, ClaseMovimiento.values()), fechaDesde, fechaHasta,
                Math.max(0, pagina), Arrays.asList(10, 25, 50, 100).contains(tamanio) ? tamanio : 25);
    }

    private static String enumValido(String valor, Enum<?>[] permitidos) {
        if (valor == null || valor.isBlank() || valor.equalsIgnoreCase("TODOS")) return "TODOS";
        return Arrays.stream(permitidos).map(Enum::name).anyMatch(nombre -> nombre.equalsIgnoreCase(valor))
                ? valor.toUpperCase() : "TODOS";
    }

    public FiltroMovimientoFinanciero conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroMovimientoFinanciero(institucionId, cuentaId, cuentaTexto, plantelId,
                direccion, clase, fechaDesde, fechaHasta, nuevaPagina, nuevoTamanio).normalizado();
    }
}
