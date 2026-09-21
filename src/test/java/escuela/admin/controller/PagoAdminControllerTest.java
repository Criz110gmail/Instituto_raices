package escuela.admin.controller;

import escuela.admin.dto.PagoForm;
import escuela.finanzas.dto.request.PagoRequest;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.MetodoPago;
import escuela.finanzas.service.PagoService;
import escuela.finanzas.service.ValidacionPagoService;
import escuela.finanzas.service.CancelacionPagoService;
import escuela.finanzas.dto.request.CancelacionPagoRequest;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PagoAdminControllerTest {
    @Mock private PagoService service;
    @Mock private ValidacionPagoService validacionService;
    @Mock private CancelacionPagoService cancelacionService;
    @Mock private InstitucionService institucionService;
    @Mock private PlantelService plantelService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private PagoAdminController controller;

    @BeforeEach
    void preparar() {
        lenient().when(institucionService.listar()).thenReturn(List.of());
        lenient().when(plantelService.listar()).thenReturn(List.of());
        lenient().when(alcance.filtrarInstituciones(anyList())).thenAnswer(i -> i.getArgument(0));
        lenient().when(alcance.filtrarPlanteles(anyList())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void preparaFormularioConFolioYMetodos() {
        ExtendedModelMap model = new ExtendedModelMap();
        String vista = controller.nuevo(model);
        PagoForm form = (PagoForm) model.get("form");
        assertThat(vista).isEqualTo("admin/pago-form");
        assertThat(form.getFolio()).startsWith("PAG-");
        assertThat((MetodoPago[]) model.get("metodos")).containsExactly(
                MetodoPago.EFECTIVO, MetodoPago.TRANSFERENCIA);
    }

    @Test
    void registraPendienteYRedirigeAlDetalle() {
        PagoForm form = formulario();
        when(institucionService.obtener(1L)).thenReturn(institucion());
        PagoResponse respuesta = mock(PagoResponse.class);
        when(respuesta.id()).thenReturn(50L);
        when(respuesta.folio()).thenReturn("PAG-001");
        when(service.registrar(any(PagoRequest.class), anyList())).thenReturn(respuesta);

        String vista = controller.registrar(form, new BeanPropertyBindingResult(form, "form"),
                List.of(), new ExtendedModelMap(), new RedirectAttributesModelMap());

        ArgumentCaptor<PagoRequest> captor = ArgumentCaptor.forClass(PagoRequest.class);
        verify(service).registrar(captor.capture(), anyList());
        assertThat(captor.getValue().fechaPago().toString()).contains("16:00:00Z");
        assertThat(vista).isEqualTo("redirect:/admin/pagos/50/editar");
    }

    @Test
    void conservaFormularioInvalidoSinRegistrar() {
        PagoForm form = formulario();
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        errores.rejectValue("monto", "pago.monto", "El monto es inválido");

        String vista = controller.registrar(form, errores, List.of(),
                new ExtendedModelMap(), new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/pago-form");
        verify(service, never()).registrar(any(), anyList());
    }

    @Test
    void validaPagoYRedirigeAlExpediente() {
        PagoResponse respuesta = mock(PagoResponse.class);
        when(respuesta.folio()).thenReturn("PAG-001");
        when(validacionService.validar(eq(50L), any())).thenReturn(respuesta);

        String vista = controller.validar(50L, 8L, 2L, mock(Authentication.class),
                new ExtendedModelMap(), new RedirectAttributesModelMap());

        verify(validacionService).validar(eq(50L), argThat(r -> r.cuentaDestinoId().equals(8L)
                && r.version().equals(2L)));
        assertThat(vista).isEqualTo("redirect:/admin/pagos/50/editar");
    }

    @Test
    void rechazaPagoConMotivoYRedirige() {
        PagoResponse respuesta = mock(PagoResponse.class);
        when(respuesta.folio()).thenReturn("PAG-001");
        when(validacionService.rechazar(eq(50L), any())).thenReturn(respuesta);

        String vista = controller.rechazar(50L, "Duplicado", 3L, mock(Authentication.class),
                new ExtendedModelMap(), new RedirectAttributesModelMap());

        verify(validacionService).rechazar(eq(50L), argThat(r -> r.motivo().equals("Duplicado")
                && r.version().equals(3L)));
        assertThat(vista).isEqualTo("redirect:/admin/pagos/50/editar");
    }

    @Test
    void cancelaPagoConMotivoYRedirigeAlExpediente() {
        PagoResponse respuesta = mock(PagoResponse.class);
        when(respuesta.folio()).thenReturn("PAG-001");
        when(cancelacionService.cancelar(eq(50L), any())).thenReturn(respuesta);

        String vista = controller.cancelar(50L, 3L, "Registro duplicado", mock(Authentication.class),
                new ExtendedModelMap(), new RedirectAttributesModelMap());

        verify(cancelacionService).cancelar(eq(50L), argThat((CancelacionPagoRequest r) ->
                r.version().equals(3L) && r.motivo().equals("Registro duplicado")));
        assertThat(vista).isEqualTo("redirect:/admin/pagos/50/editar");
    }

    private PagoForm formulario() {
        PagoForm form = new PagoForm();
        form.setInstitucionId(1L); form.setPlantelRegistroId(2L); form.setTutorId(3L);
        form.setFolio("PAG-001"); form.setFechaPago(LocalDateTime.of(2026, 9, 15, 10, 0));
        form.setMonto(new BigDecimal("500.00")); form.setMoneda("MXN");
        form.setMetodo(MetodoPago.EFECTIVO);
        return form;
    }

    private InstitucionResponse institucion() {
        return new InstitucionResponse(1L, "RAICES", "Raíces", null, null, null,
                null, null, null, null, null, null, null, "MX", null,
                "America/Mexico_City", "MXN", true, null);
    }
}
