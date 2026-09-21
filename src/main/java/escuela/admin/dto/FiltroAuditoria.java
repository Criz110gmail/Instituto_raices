package escuela.admin.dto;

import escuela.auditoria.entity.AccionAuditoria;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.*;

public record FiltroAuditoria(Long institucionId, String accion, String tipoEntidad,
                              String entidadId, String actor, String correlacion,
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                              int pagina, int tamanio) {
    public FiltroAuditoria normalizado() {
        return new FiltroAuditoria(institucionId, accion(accion), texto(tipoEntidad, 80).toUpperCase(Locale.ROOT),
                texto(entidadId, 100), texto(actor, 120), texto(correlacion, 64), fechaDesde, fechaHasta,
                Math.max(0, pagina), List.of(10, 25, 50, 100).contains(tamanio) ? tamanio : 25);
    }

    public FiltroAuditoria conPagina(int nuevaPagina, int nuevoTamanio) {
        return new FiltroAuditoria(institucionId, accion, tipoEntidad, entidadId, actor,
                correlacion, fechaDesde, fechaHasta, nuevaPagina, nuevoTamanio).normalizado();
    }

    private static String accion(String valor) {
        if (valor == null || valor.isBlank() || valor.equalsIgnoreCase("TODAS")) return "TODAS";
        return Arrays.stream(AccionAuditoria.values()).map(Enum::name)
                .anyMatch(nombre -> nombre.equalsIgnoreCase(valor)) ? valor.toUpperCase(Locale.ROOT) : "TODAS";
    }

    private static String texto(String valor, int maximo) {
        if (valor == null) return "";
        String limpio = valor.trim().replaceAll("\\s+", " ");
        return limpio.length() > maximo ? limpio.substring(0, maximo) : limpio;
    }
}
