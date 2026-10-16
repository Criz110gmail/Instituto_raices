package escuela.admin.controller;

import escuela.admin.dto.ResultadoAutocompletado;
import escuela.admin.service.AlumnoPagoBusquedaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
public class AlumnoPagoBusquedaController {
    private final AlumnoPagoBusquedaService service;

    @GetMapping("/admin/autocompletado/alumnos-pagos")
    ResultadoAutocompletado alumnos(@RequestParam(defaultValue = "") String q) {
        return service.buscar(q);
    }
}
