package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.docente.dto.FiltroPlaneacion;
import escuela.docente.entity.EstadoPlaneacion;
import escuela.docente.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/planeaciones")
public class PlaneacionAdminController {
    private final PlaneacionService service;private final ExcelPlaneacionService excel;private final PdfPlaneacionService pdf;
    @GetMapping String listar(@RequestParam(required=false)Long institucionId,@RequestParam(required=false)Long plantelId,@RequestParam(required=false)Long maestroId,@RequestParam(required=false)Long grupoId,@RequestParam(required=false)EstadoPlaneacion estado,@RequestParam(required=false)LocalDate desde,@RequestParam(required=false)LocalDate hasta,@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,Authentication auth,Model m){FiltroPlaneacion f=new FiltroPlaneacion(institucionId,plantelId,maestroId,grupoId,estado,desde,hasta,q,pagina,tamanio).normalizado();m.addAttribute("filtro",f);m.addAttribute("resultado",service.listarAdmin(f));m.addAttribute("estados",EstadoPlaneacion.values());menu(m,auth);return"admin/planeaciones";}
    @GetMapping("/excel") void excel(@RequestParam(required=false)Long institucionId,@RequestParam(required=false)Long plantelId,@RequestParam(required=false)Long maestroId,@RequestParam(required=false)Long grupoId,@RequestParam(required=false)EstadoPlaneacion estado,@RequestParam(required=false)LocalDate desde,@RequestParam(required=false)LocalDate hasta,@RequestParam(defaultValue="")String q,HttpServletResponse r)throws IOException{r.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");r.setHeader("Content-Disposition","attachment; filename*=UTF-8''planeaciones.xlsx");excel.exportar(new FiltroPlaneacion(institucionId,plantelId,maestroId,grupoId,estado,desde,hasta,q,0,100),r.getOutputStream());}
    @GetMapping("/{id}") String detalle(@PathVariable Long id,Authentication auth,Model m){m.addAttribute("planeacion",service.detalleAdmin(id));m.addAttribute("versionEntidad",service.versionEntidad(id));m.addAttribute("historial",service.historial(id));m.addAttribute("versiones",service.versiones(id));menu(m,auth);return"admin/planeacion-detalle";}
    @PostMapping("/{id}/revision") String revision(@PathVariable Long id,@RequestParam Long version,RedirectAttributes f){return accion(id,f,()->service.iniciarRevision(id,version),"Planeación marcada en revisión");}
    @PostMapping("/{id}/ajustes") String ajustes(@PathVariable Long id,@RequestParam Long version,@RequestParam String motivo,RedirectAttributes f){return accion(id,f,()->service.solicitarAjustes(id,version,motivo),"Se solicitaron ajustes al maestro");}
    @PostMapping("/{id}/publicar") String publicar(@PathVariable Long id,@RequestParam Long version,RedirectAttributes f){return accion(id,f,()->service.publicar(id,version),"Planeación publicada y versión histórica conservada");}
    @PostMapping("/{id}/reabrir") String reabrir(@PathVariable Long id,@RequestParam Long version,@RequestParam String motivo,RedirectAttributes f){return accion(id,f,()->service.reabrir(id,version,motivo),"Planeación reabierta para edición");}
    @GetMapping("/{id}/pdf") void pdf(@PathVariable Long id,HttpServletResponse r)throws IOException{r.setContentType("application/pdf");r.setHeader("Content-Disposition",disposicion("planeacion-"+id+".pdf"));pdf.exportar(service.detalleAdmin(id),r.getOutputStream());}
    @GetMapping("/{id}/versiones/{revision}/pdf") void version(@PathVariable Long id,@PathVariable int revision,HttpServletResponse r)throws IOException{r.setContentType("application/pdf");r.setHeader("Content-Disposition",disposicion("planeacion-"+id+"-revision-"+revision+".pdf"));pdf.exportar(service.version(id,revision,false,null),r.getOutputStream());}
    private String accion(Long id,RedirectAttributes f,Runnable r,String ok){try{r.run();f.addFlashAttribute("mensaje",ok);}catch(RuntimeException e){f.addFlashAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}return"redirect:/admin/planeaciones/"+id;}
    private void menu(Model m,Authentication a){Set<String>p=new HashSet<>();a.getAuthorities().forEach(x->p.add(x.getAuthority()));m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("moduloActual",ModuloCatalogo.PLANEACIONES);}
    private String disposicion(String n){return"inline; filename*=UTF-8''"+URLEncoder.encode(n,StandardCharsets.UTF_8);}
}
