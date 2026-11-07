package escuela.admin.controller;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.admin.service.AlumnoCobranzaBusquedaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController @RequiredArgsConstructor
public class AlumnoCobranzaBusquedaController {
    private final AlumnoCobranzaBusquedaService service;
    @GetMapping("/admin/autocompletado/alumnos-adeudos")
    ResultadoAutocompletado adeudos(@RequestParam(defaultValue="") String q){return service.buscar(q,true);}
    @GetMapping("/admin/autocompletado/alumnos-cuotas")
    ResultadoAutocompletado cuotas(@RequestParam(defaultValue="") String q){return service.buscar(q,false);}
}
