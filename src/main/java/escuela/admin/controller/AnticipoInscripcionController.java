package escuela.admin.controller;

import escuela.cobranza.dto.FilaAnticipoInscripcion;
import escuela.cobranza.service.CargoService;
import escuela.cobranza.service.CobranzaInscripcionService;
import escuela.inscripcion.service.InscripcionService;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.admin.dto.ModuloCatalogo;
import escuela.common.exception.ReglaNegocioException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/inscripciones/{id}/pago-anticipado")
public class AnticipoInscripcionController {
    private final CargoService cargos;
    private final InscripcionService inscripciones;
    private final CobranzaInscripcionService cobranza;
    private final AlcanceDatosService alcance;
    private final PagoAdminController pagos;
    private static final String CLAVE=AnticipoInscripcionController.class.getName();
    private record Vista(Long inscripcion,LocalDate desde,LocalDate hasta,List<FilaAnticipoInscripcion> filas,Instant vence,List<Long> resultado,Set<String> seleccion) {}
    @SuppressWarnings("unchecked") private Map<UUID,Vista> vistas(HttpSession s){var v=(Map<UUID,Vista>)s.getAttribute(CLAVE);if(v==null){v=new LinkedHashMap<>();s.setAttribute(CLAVE,v);}return v;}
    @GetMapping String inicio(@PathVariable Long id,Model m){base(id,m);m.addAttribute("desde",LocalDate.now().withDayOfMonth(1));m.addAttribute("hasta",LocalDate.now().withDayOfMonth(1).plusMonths(3).minusDays(1));return "admin/inscripcion-anticipo";}
    @PostMapping("/consultar") String consultar(@PathVariable Long id,@RequestParam LocalDate desde,@RequestParam LocalDate hasta,HttpSession session,Model m){
        base(id,m);m.addAttribute("desde",desde);m.addAttribute("hasta",hasta);
        try{var filas=cargos.anticipables(id,desde,hasta);UUID token=UUID.randomUUID();synchronized(session){var mapa=vistas(session);mapa.entrySet().removeIf(e->e.getValue().vence().isBefore(Instant.now()));while(mapa.size()>=5)mapa.remove(mapa.keySet().iterator().next());mapa.put(token,new Vista(id,desde,hasta,filas,Instant.now().plusSeconds(1200),null,Set.of()));}mostrarDisponibles(m,filas);m.addAttribute("token",token);}
        catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());}return "admin/inscripcion-anticipo";
    }
    @PostMapping("/preparar") String preparar(@PathVariable Long id,@RequestParam UUID token,@RequestParam(required=false)Set<String> elegidas,@RequestParam(defaultValue="false")boolean confirmado,HttpSession session,Model m){
        base(id,m);synchronized(session){var vista=vistas(session).get(token);
            if(vista==null||!vista.inscripcion().equals(id)||vista.vence().isBefore(Instant.now())){m.addAttribute("errorOperacion","La vista previa venció. Consulta nuevamente los periodos");return "admin/inscripcion-anticipo";}
            m.addAttribute("desde",vista.desde());m.addAttribute("hasta",vista.hasta());mostrarDisponibles(m,vista.filas());m.addAttribute("token",token);m.addAttribute("elegidas",elegidas==null?Set.of():elegidas);
            try{
                if(!confirmado)throw new ReglaNegocioException("Revisa y confirma la selección");
                if(cobranza.resumen(id).tutorSugerido()==null)throw new ReglaNegocioException("Vincula un tutor responsable financiero vigente antes de preparar el pago");
                if(vista.resultado()!=null){if(!vista.seleccion().equals(elegidas))throw new ReglaNegocioException("Esta selección ya se preparó. Consulta otra vez para cambiarla");var actuales=cargos.anticipables(id,vista.desde(),vista.hasta());if(vista.resultado().stream().anyMatch(c->actuales.stream().noneMatch(f->c.equals(f.cargoId())&&f.disponible())))throw new ReglaNegocioException("Uno de los cargos ya tiene un pago en revisión o fue liquidado. Consulta su detalle antes de continuar");return pagos.prepararAnticipo(vista.resultado(),id,m);}
                var resultado=cargos.confirmarAnticipo(id,vista.desde(),vista.hasta(),vista.filas(),elegidas).stream().map(escuela.cobranza.dto.response.CargoResponse::id).toList();
                vistas(session).put(token,new Vista(id,vista.desde(),vista.hasta(),vista.filas(),vista.vence(),resultado,Set.copyOf(elegidas)));
                return pagos.prepararAnticipo(resultado,id,m);
            }catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());return "admin/inscripcion-anticipo";}
        }
    }
    private void mostrarDisponibles(Model model,List<FilaAnticipoInscripcion> filas){model.addAttribute("filas",filas.stream().filter(FilaAnticipoInscripcion::disponible).toList());}
    private void base(Long id,Model m){var auth=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();if(auth==null||java.util.stream.Stream.of("INSCRIPCION_ADMINISTRAR","CARGO_ADMINISTRAR","PAGO_REGISTRAR").anyMatch(p->auth.getAuthorities().stream().noneMatch(a->a.getAuthority().equals(p))))throw new org.springframework.security.access.AccessDeniedException("Se requieren permisos de inscripción, cargos y pagos");alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES,id);m.addAttribute("inscripcion",inscripciones.obtener(id));m.addAttribute("id",id);}
}
