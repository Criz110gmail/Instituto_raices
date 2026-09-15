package escuela.admin.controller;

import escuela.admin.dto.CuentaFinancieraForm;
import escuela.finanzas.dto.request.CuentaFinancieraRequest;
import escuela.finanzas.entity.TipoCuentaFinanciera;
import escuela.finanzas.service.CuentaFinancieraService;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CuentaFinancieraAdminControllerTest {

    @Mock private CuentaFinancieraService service;
    @Mock private InstitucionService institucionService;
    @Mock private PlantelService plantelService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private CuentaFinancieraAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        lenient().when(alcance.filtrarInstituciones(anyList())).thenAnswer(i -> i.getArgument(0));
        lenient().when(alcance.filtrarPlanteles(anyList())).thenAnswer(i -> i.getArgument(0));
        lenient().when(institucionService.listar()).thenReturn(List.of());
        lenient().when(plantelService.listar()).thenReturn(List.of());
    }

    @Test
    void preparaFormularioNuevoConTiposFinancieros() {
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.nuevo(model);

        assertThat(vista).isEqualTo("admin/cuenta-financiera-form");
        assertThat(model.get("form")).isInstanceOf(CuentaFinancieraForm.class);
        assertThat((TipoCuentaFinanciera[]) model.get("tipos")).containsExactly(
                TipoCuentaFinanciera.CAJA, TipoCuentaFinanciera.BANCO,
                TipoCuentaFinanciera.INVERSION);
    }

    @Test
    void creaCuentaDePlantelRespetandoAlcance() {
        CuentaFinancieraForm form = formulario();
        form.setPlantelId(2L);
        when(plantelService.obtener(2L)).thenReturn(plantel(2L, 1L));
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        RedirectAttributesModelMap flash = new RedirectAttributesModelMap();

        String vista = controller.crear(form, errores, new ExtendedModelMap(), flash);

        ArgumentCaptor<CuentaFinancieraRequest> request = ArgumentCaptor.forClass(CuentaFinancieraRequest.class);
        verify(alcance).validarPlantel(2L);
        verify(service).crear(request.capture());
        assertThat(request.getValue().plantelId()).isEqualTo(2L);
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/cuentas-financieras");
    }

    @Test
    void rechazaPlantelDeOtraInstitucionSinGuardar() {
        CuentaFinancieraForm form = formulario();
        form.setPlantelId(2L);
        when(plantelService.obtener(2L)).thenReturn(plantel(2L, 9L));
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/cuenta-financiera-form");
        assertThat(errores.getFieldError("plantelId")).isNotNull();
        verify(service, never()).crear(any());
    }

    @Test
    void unaCuentaCompartidaExigeAdministracionInstitucional() {
        CuentaFinancieraForm form = formulario();
        form.setPlantelId(null);

        controller.crear(form, new BeanPropertyBindingResult(form, "form"),
                new ExtendedModelMap(), new RedirectAttributesModelMap());

        verify(alcance).validarAdministracionInstitucional(1L);
    }

    private CuentaFinancieraForm formulario() {
        CuentaFinancieraForm form = new CuentaFinancieraForm();
        form.setInstitucionId(1L);
        form.setCodigo("BBVA");
        form.setNombre("Cuenta principal");
        form.setTipo(TipoCuentaFinanciera.BANCO);
        form.setBancoNombre("BBVA");
        form.setNumeroCuenta("12345678");
        form.setMoneda("MXN");
        form.setSaldoInicial(new BigDecimal("0.00"));
        form.setFechaSaldoInicial(LocalDate.now());
        return form;
    }

    private PlantelResponse plantel(Long id, Long institucionId) {
        return new PlantelResponse(id, institucionId, "CENTRO", "Centro", null, null,
                null, null, null, null, null, null, null, null, true, null);
    }
}
