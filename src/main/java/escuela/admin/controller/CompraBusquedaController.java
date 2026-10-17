package escuela.admin.controller;

import escuela.admin.dto.ResultadoAutocompletado;
import escuela.compras.service.CompraBusquedaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor @RequestMapping("/admin/autocompletado/compras")
public class CompraBusquedaController {
    private final CompraBusquedaService service;
    @GetMapping("/filtros/proveedores")
    ResultadoAutocompletado proveedoresFiltro(@RequestParam Long institucionId,@RequestParam(defaultValue="")String q){return service.proveedoresFiltro(institucionId,q);}
    @GetMapping("/filtros/cuentas")
    ResultadoAutocompletado cuentasFiltro(@RequestParam Long institucionId,@RequestParam(required=false)Long plantelId,@RequestParam(defaultValue="")String q){return service.cuentasFiltro(institucionId,plantelId,q);}
    @GetMapping("/proveedores")
    ResultadoAutocompletado proveedores(@RequestParam Long institucionId,@RequestParam(defaultValue="")String q){return service.proveedores(institucionId,q);}
    @GetMapping("/cuentas")
    ResultadoAutocompletado cuentas(@RequestParam Long institucionId,@RequestParam(required=false)Long plantelId,@RequestParam(defaultValue="")String q){return service.cuentas(institucionId,plantelId,q);}
    @GetMapping("/motivos")
    ResultadoAutocompletado motivos(@RequestParam Long institucionId,@RequestParam(defaultValue="")String q){return service.motivos(institucionId,q);}
}
