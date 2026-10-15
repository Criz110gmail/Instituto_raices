package escuela.admin.controller;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.admin.service.MotivoMovimientoBusquedaService;
import escuela.finanzas.entity.DireccionMovimiento;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController @RequiredArgsConstructor
public class MotivoMovimientoBusquedaController {
    private final MotivoMovimientoBusquedaService service;
    @GetMapping("/admin/autocompletado/motivos-movimiento")
    ResultadoAutocompletado buscar(@RequestParam Long institucionId,@RequestParam DireccionMovimiento direccion,@RequestParam(defaultValue="")String q){
        return service.buscar(institucionId,direccion,q);
    }
}
