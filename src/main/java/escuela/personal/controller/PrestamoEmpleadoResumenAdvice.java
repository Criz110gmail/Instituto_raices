package escuela.personal.controller;
import escuela.personal.service.PrestamoProgramadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.Map;
@ControllerAdvice(assignableTypes=EmpleadoAdminController.class) @RequiredArgsConstructor
public class PrestamoEmpleadoResumenAdvice {
 private final PrestamoProgramadoService service;
 @ModelAttribute void resumen(@PathVariable Map<String,String> variables,Model m){String id=variables.get("id");if(id!=null&&id.matches("[0-9]+")&&service.permitido("PRESTAMO_LEER"))m.addAttribute("resumenPrestamos",service.resumenEmpleado(Long.valueOf(id)));}
}
