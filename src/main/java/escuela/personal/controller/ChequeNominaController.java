package escuela.personal.controller;
import escuela.personal.service.ChequeNominaService;
import escuela.admin.dto.ResultadoAutocompletado;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@RestController @RequiredArgsConstructor @RequestMapping("/admin/preparacion-nomina/cheques")
public class ChequeNominaController {
 private final ChequeNominaService service;
 @GetMapping("/chequeras") ResponseEntity<ResultadoAutocompletado> buscar(@RequestParam Long cuentaId,@RequestParam(defaultValue="")String q){var r=service.buscar(cuentaId,q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(r.stream().limit(10).toList(),r.size()>10));}
 @GetMapping("/contexto") ResponseEntity<ChequeNominaService.Contexto> contexto(@RequestParam Long cuentaId,@RequestParam Long chequeraId){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.contexto(cuentaId,chequeraId));}
}
