package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;

class GuiasCobranzaContenidoTest {
    private String contenido() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V57__guias_becas_recargos_pagos_parciales.sql"));
    }

    @Test void becaYRecargoExplicanBaseVencimientoYProcesoReal() throws Exception {
        String sql=contenido();
        assertThat(sql).contains("beca-recargo-liquidacion", "$1,000.00 − $200.00 = $800.00",
                "$800.00 × 10% = $80.00", "$800.00 + recargo $80.00 = $880.00",
                "Asignar beca", "antes de emitir", "Tipo de límite Sin límite",
                "Políticas de recargo pulsa Generar recargos", "/admin/politicas-recargo/generar",
                "Confirmar y generar recargos", "TIPO_BECA_LEER", "BECA_ALUMNO_LEER",
                "POLITICA_RECARGO_LEER", "AJUSTE_CARGO_LEER", "el selector de Reportar transferencia del portal lo excluye")
                .doesNotContain("/admin/recargos/generar");
    }

    @Test void pagosParcialesDistinguenDeudaDineroSinAsignarYTresIngresos() throws Exception {
        String sql=contenido();
        assertThat(sql).contains("pagos-parciales-liquidacion", "Registrar pago parcial",
                "Pagos recibidos → Nuevo registro", "$700.00", "$200.00", "Caja Bc",
                "Caja Bc + $800.00; banco Bb + $200.00", "tres pagos y tres ingresos",
                "Dinero pendiente de asignar $0.00 no significa que el alumno deba cero",
                "No edites el primer pago", "En revisión", "PDF en una pestaña nueva");
        var pasos=Pattern.compile("(?m)^\\([1-8],").matcher(sql);
        assertThat(pasos.results().count()).isEqualTo(15);
        var inserts=Pattern.compile("(?i)INSERT INTO ([a-z_]+)").matcher(sql);
        assertThat(inserts.results().map(m->m.group(1)).toList())
                .allSatisfy(tabla->assertThat(tabla).startsWith("guia_proceso"));
        assertThat(sql).doesNotContain("INSERT INTO cargo", "INSERT INTO pago", "UPDATE cargo", "UPDATE pago", "INSERT INTO rol_permiso");
    }
}
