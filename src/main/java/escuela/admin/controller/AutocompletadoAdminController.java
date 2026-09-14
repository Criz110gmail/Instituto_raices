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
}
