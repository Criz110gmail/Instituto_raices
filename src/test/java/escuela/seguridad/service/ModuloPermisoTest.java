package escuela.seguridad.service;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ModuloPermisoTest {
    @Test
    void expandeCualquierAccionAlModuloCompleto() {
        assertThat(ModuloPermiso.expandir(Set.of("PAGO_LEER")))
                .contains("PAGO_LEER", "PAGO_REGISTRAR", "PAGO_VALIDAR",
                        "PAGO_DEVOLVER", "PAGO_CANCELAR")
                .doesNotContain("ALUMNO_ADMINISTRAR");
    }

    @Test
    void conservaUnPermisoDesconocidoParaNoRomperExtensiones() {
        assertThat(ModuloPermiso.expandir(Set.of("PERMISO_FUTURO")))
                .containsExactly("PERMISO_FUTURO");
    }
}
