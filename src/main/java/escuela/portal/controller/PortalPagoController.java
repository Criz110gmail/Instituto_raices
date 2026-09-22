package escuela.portal.controller;

import escuela.admin.dto.ResultadoAutocompletado;
import escuela.common.exception.ReglaNegocioException;
import escuela.portal.dto.*;
import escuela.portal.service.PortalPagoService;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.*;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/portal/pagos")
public class PortalPagoController {
    private final PortalPagoService service;
    private final InstitucionService instituciones;
    @GetMapping("/reportar")
    String formulario(@AuthenticationPrincipal UsuarioPrincipal p, Model model){
        var f=new PortalPagoForm(); f.setFechaPago(LocalDateTime.now(ZoneId.of(instituciones.obtener(p.institucionId()).zonaHoraria())).withSecond(0).withNano(0));
        f.getSolicitudes().add(new PortalSolicitudPagoForm()); model.addAttribute("form",f); preparar(p,model); return "portal/pago-form";
    }
    @PostMapping(value="/reportar", consumes="multipart/form-data")
    String guardar(@Valid @ModelAttribute("form") PortalPagoForm form, BindingResult errores,
                   @RequestParam(name="comprobantes",required=false) List<MultipartFile> files,
                   @AuthenticationPrincipal UsuarioPrincipal p, Model model, RedirectAttributes flash){
        if(errores.hasErrors()){preparar(p,model); return "portal/pago-form";}
        try {service.reportar(p,form,files); flash.addFlashAttribute("mensajePortal","Transferencia enviada. Quedará en revisión por la escuela."); return "redirect:/portal#cuenta";}
        catch(ReglaNegocioException ex){model.addAttribute("error",ex.getMessage()); preparar(p,model); return "portal/pago-form";}
    }
    @GetMapping("/cargos") @ResponseBody ResultadoAutocompletado cargos(@RequestParam(defaultValue="")String q,@AuthenticationPrincipal UsuarioPrincipal p){return service.buscarCargos(p,q);}
    @GetMapping("/cuentas") @ResponseBody ResultadoAutocompletado cuentas(@RequestParam Long plantelId,@RequestParam(defaultValue="")String q,@AuthenticationPrincipal UsuarioPrincipal p){return service.buscarCuentas(p,plantelId,q);}
    private void preparar(UsuarioPrincipal p,Model m){m.addAttribute("planteles",service.planteles(p));m.addAttribute("moneda",instituciones.obtener(p.institucionId()).monedaPredeterminada());}
}
