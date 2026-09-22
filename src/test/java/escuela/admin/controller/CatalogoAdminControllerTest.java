package escuela.admin.controller;

import escuela.admin.dto.FiltroCatalogo;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.dto.ResultadoCatalogo;
import escuela.admin.service.CatalogoConsultaService;
import escuela.admin.service.ExcelCatalogoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.ExtendedModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoAdminControllerTest {

    @Mock private CatalogoConsultaService consultaService;
    @Mock private ExcelCatalogoService excelService;
    @InjectMocks private CatalogoAdminController controller;

    @Test
    void inicioRedirigeAlPrimerModuloPermitido() {
        var authentication = autenticacion("GRUPO_LEER", "USUARIO_ADMINISTRAR");

        assertThat(controller.admin(authentication))
                .isEqualTo("redirect:/admin/catalogos/grupos");
    }

    @Test
    void menuContieneUnicamenteModulosAutorizados() {
        var authentication = autenticacion("GRUPO_LEER", "USUARIO_ADMINISTRAR");
        var resultado = new ResultadoCatalogo(ModuloCatalogo.GRUPOS,
                ModuloCatalogo.GRUPOS.columnas(), Page.empty());
        when(consultaService.consultar(eq(ModuloCatalogo.GRUPOS), any(FiltroCatalogo.class)))
                .thenReturn(resultado);
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.catalogo("grupos", "", "TODOS", 0, 25,
                authentication, model);

        assertThat(vista).isEqualTo("admin/catalogo");
        assertThat(model.get("modulos")).isEqualTo(
                java.util.List.of(ModuloCatalogo.GRUPOS, ModuloCatalogo.USUARIOS));
    }

    @Test
    void controladorTambienRechazaModuloSinPermiso() {
        var authentication = autenticacion("GRUPO_LEER");

        assertThatThrownBy(() -> controller.catalogo("usuarios", "", "TODOS", 0,
                25, authentication, new ExtendedModelMap()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void inicioSinModulosPermitidosMuestraAccesoDenegado() {
        assertThat(controller.admin(autenticacion()))
                .isEqualTo("redirect:/acceso-denegado");
    }

    @Test
    void historialDeAjustesCambiaSoloElNombreVisible() {
        assertThat(ModuloCatalogo.AJUSTES_CARGO.titulo()).isEqualTo("Historial de ajustes");
        assertThat(ModuloCatalogo.AJUSTES_CARGO.slug()).isEqualTo("ajustes-cargo");
        assertThat(ModuloCatalogo.AJUSTES_CARGO.rutaListado())
                .isEqualTo("/admin/catalogos/ajustes-cargo");
        assertThat(ModuloCatalogo.AJUSTES_CARGO.visibleCon(java.util.Set.of("AJUSTE_CARGO_LEER")))
                .isTrue();
    }

    private TestingAuthenticationToken autenticacion(String... permisos) {
        return new TestingAuthenticationToken("usuario", "password", permisos);
    }
}
