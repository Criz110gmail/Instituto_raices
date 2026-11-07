package escuela.admin.controller;

import escuela.admin.dto.ResultadoAutocompletado;
import escuela.admin.service.ConceptoFiltroBusquedaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConceptoFiltroBusquedaController {
    private final ConceptoFiltroBusquedaService service;
    @GetMapping("/admin/autocompletado/conceptos-filtro")
    ResultadoAutocompletado conceptos(@RequestParam(defaultValue = "") String q) { return service.buscar(q); }
}
