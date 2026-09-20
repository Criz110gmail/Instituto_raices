package escuela.admin.dto;

import escuela.finanzas.entity.EstadoCorteCaja;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Arrays;

public record FiltroCorteCaja(Long institucionId, Long cuentaId, String cuentaTexto,
                              String estado,
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                              int pagina, int tamanio) {
    public FiltroCorteCaja normalizado() {
        String cuenta = cuentaTexto == null ? "" : cuentaTexto.trim().replaceAll("\\s+", " ");
        String solicitado = estado == null ? "TODOS" : estado.toUpperCase();
        String estadoNormalizado = solicitado.equals("TODOS") || Arrays.stream(EstadoCorteCaja.values())
                .anyMatch(valor -> valor.name().equals(solicitado)) ? solicitado : "TODOS";
        return new FiltroCorteCaja(institucionId, cuentaId, cuenta, estadoNormalizado,
                fechaDesde, fechaHasta, Math.max(0, pagina),
                Arrays.asList(10, 25, 50, 100).contains(tamanio) ? tamanio : 25);
    }

    public FiltroCorteCaja conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroCorteCaja(institucionId, cuentaId, cuentaTexto, estado,
                fechaDesde, fechaHasta, nuevaPagina, nuevoTamanio).normalizado();
    }
}
