package escuela.personal.controller;
import escuela.personal.dto.FiltroReciboNomina;
import escuela.personal.service.*;
import escuela.admin.service.JasperReciboNominaService;
import escuela.common.support.SalidaArchivoHttp;
import escuela.common.exception.ReglaNegocioException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@Controller @RequiredArgsConstructor @RequestMapping("/personal")
public class PortalRecibosEmpleadoController {
 private final AccesoRecibosEmpleadoService accesos;
 private final ReciboNominaService recibos;
 private final JasperReciboNominaService pdf;
 @GetMapping String inicio(@ModelAttribute FiltroReciboNomina filtro,Model m,HttpServletResponse r){
  r.setHeader("Cache-Control","no-store");m.addAttribute("perfil",accesos.identidadActual());m.addAttribute("filtro",filtro.normalizado());
  try{m.addAttribute("resultado",recibos.listarPropios(filtro));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",Page.empty());}return "personal/recibos";
 }
 @GetMapping("/recibos/{tipo:PAGO|CERO|HISTORICO}/{id:\\d+}/pdf") void pdf(@PathVariable String tipo,@PathVariable Long id,HttpServletResponse r)throws IOException{
  var propio=recibos.obtenerPropio(tipo,id);
  pdf.exportarAutorizado(propio,new SalidaArchivoHttp(r,"application/pdf","inline; filename=mi-recibo-"+tipo+"-"+id+".pdf"));
 }
}
