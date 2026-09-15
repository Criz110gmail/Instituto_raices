package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.admin.service.BusquedaAutocompletadoService;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import escuela.finanzas.entity.MetodoPago;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/autocompletado")
public class AutocompletadoAdminController {

    private final BusquedaAutocompletadoService service;
    private final AlcanceDatosService alcance;

    @GetMapping("/alumnos")
    ResultadoAutocompletado alumnos(@RequestParam Long institucionId,
                                    @RequestParam(defaultValue = "") String q) {
        boolean administraInscripciones = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("INSCRIPCION_ADMINISTRAR"));
        if (administraInscripciones) return service.alumnosParaInscripcion(institucionId, q);
        return service.alumnos(institucionId, q);
    }

    @GetMapping("/tutores")
    ResultadoAutocompletado tutores(@RequestParam Long institucionId,
                                    @RequestParam(defaultValue = "") String q) {
        boolean registraPagos = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PAGO_REGISTRAR"));
        if (registraPagos) return service.tutoresParaPago(institucionId, q);
        return service.tutores(institucionId, q);
    }

    @GetMapping("/usuarios")
    ResultadoAutocompletado usuarios(@RequestParam Long institucionId,
                                     @RequestParam(defaultValue = "") String q,
                                     @RequestParam(required = false) Long tutorId) {
        if (tutorId != null) alcance.validarRecurso(ModuloCatalogo.TUTORES, tutorId);
        return service.usuarios(institucionId, q, tutorId);
    }

    @GetMapping("/inscripciones")
    ResultadoAutocompletado inscripciones(@RequestParam Long plantelId,
                                          @RequestParam(defaultValue = "") String q) {
        return service.inscripciones(plantelId, q);
    }

    @GetMapping("/conceptos-cobro")
    ResultadoAutocompletado conceptosCobro(@RequestParam Long institucionId,
                                           @RequestParam(defaultValue = "") String q) {
        return service.conceptosCobro(institucionId, q);
    }

    @GetMapping("/periodos-cargo")
    ResultadoAutocompletado periodosCargo(@RequestParam Long inscripcionId,
                                          @RequestParam(defaultValue = "") String q) {
        return service.periodosCargo(inscripcionId, q);
    }

    @GetMapping("/tipos-beca")
    ResultadoAutocompletado tiposBeca(@RequestParam Long institucionId,
                                      @RequestParam(defaultValue = "") String q) {
        return service.tiposBeca(institucionId, q);
    }

    @GetMapping("/cuentas-pago")
    ResultadoAutocompletado cuentasPago(@RequestParam Long institucionId,
                                        @RequestParam Long plantelId,
                                        @RequestParam MetodoPago metodo,
                                        @RequestParam(defaultValue = "") String q) {
        return service.cuentasParaPago(institucionId, plantelId, metodo, q);
    }

    @GetMapping("/cargos-pago")
    ResultadoAutocompletado cargosPago(@RequestParam Long institucionId,
                                       @RequestParam Long tutorId,
                                       @RequestParam(defaultValue = "") String q) {
        return service.cargosParaPago(institucionId, tutorId, q);
    }

    @GetMapping("/cuentas-movimiento")
    ResultadoAutocompletado cuentasMovimiento(@RequestParam Long institucionId,
                                              @RequestParam(defaultValue = "") String q) {
        return service.cuentasParaMovimientos(institucionId, q);
    }
}
