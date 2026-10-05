package escuela.admin.controller;

import escuela.admin.dto.AjusteCargoForm;
import escuela.cobranza.service.AjusteCargoService;
import escuela.cobranza.service.CargoService;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.repository.SolicitudAplicacionPagoRepository;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AjusteCargoAdminControllerTest {
    private final AjusteCargoService ajustes = mock(AjusteCargoService.class);
    private final AjusteCargoAdminController controller = new AjusteCargoAdminController(
            ajustes, mock(CargoService.class), mock(AlcanceDatosService.class),
            mock(SolicitudAplicacionPagoRepository.class));

    @AfterEach void limpiar() { SecurityContextHolder.clearContext(); }

    @Test void conservaMensajeYPermisosAlRechazarUnDescuento() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "", List.of(
                        new SimpleGrantedAuthority("PAGO_REGISTRAR"))));
        when(ajustes.crear(any())).thenThrow(new ReglaNegocioException(
                "El concepto del cargo no permite descuentos"));
        var form = new AjusteCargoForm();
        var model = new ExtendedModelMap();
        String vista = controller.crear(15L, form,
                new BeanPropertyBindingResult(form, "ajusteForm"), model,
                new RedirectAttributesModelMap());
        assertThat(vista).isEqualTo("admin/cargo-detalle");
        assertThat(model.get("errorAjuste")).isEqualTo("El concepto del cargo no permite descuentos");
        assertThat(model.get("puedeRegistrarPago")).isEqualTo(true);
        assertThat(model.get("ajusteForm")).isSameAs(form);
    }

    @Test void erroresDeCapturaReconstruyenPermisosSinGuardar() {
        var form = new AjusteCargoForm();
        var errores = new BeanPropertyBindingResult(form, "ajusteForm");
        errores.rejectValue("monto", "required", "Indica el monto");
        var model = new ExtendedModelMap();
        assertThat(controller.crear(15L, form, errores, model,
                new RedirectAttributesModelMap())).isEqualTo("admin/cargo-detalle");
        assertThat(model.get("puedeRegistrarPago")).isEqualTo(false);
        verify(ajustes, never()).crear(any());
    }
}
