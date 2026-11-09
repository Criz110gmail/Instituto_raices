package escuela.admin.controller;

import escuela.admin.support.MensajeErrorFormulario;
import escuela.compras.dto.*;
import escuela.compras.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import java.nio.charset.StandardCharsets;

@Controller @RequiredArgsConstructor
@RequestMapping("/admin/compras/{id}/comprobantes")
public class CompraComprobanteController {
    private final CompraComprobanteService comprobantes;
    private final CompraService compras;

    @ModelAttribute void consulta(@PathVariable Long id,@RequestParam(required=false)String retorno,
            @RequestParam(required=false)String origen,Model m) {
        String prefijo="/admin/compras/"+id;
        String seguro=retorno!=null&&retorno.matches(java.util.regex.Pattern.quote(prefijo)+"(?:\\?[^\\r\\n#]*)?")?retorno:regreso(id,origen).split("#",2)[0];
        m.addAttribute("consulta",seguro);
    }
    private String destino(Model m){return (String)m.getAttribute("consulta")+"#compra-comprobantes";}

    @GetMapping("/excel") void excel(@PathVariable Long id,@RequestParam(defaultValue="")String documentoTipo,
        @RequestParam(defaultValue="VIGENTES")String documentoEstado,@RequestParam(defaultValue="")String documentoTexto,
        jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        var datos=new java.util.ArrayList<CompraComprobanteService.Documento>();int pagina=0;boolean mas;
        do{var page=comprobantes.expediente(id,documentoTipo,documentoEstado,documentoTexto,pagina++).documentos();datos.addAll(page.getContent());mas=page.hasNext();}while(mas);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=comprobantes-compra.xlsx");response.setHeader("Cache-Control","no-store");
        try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var hoja=libro.createSheet("Comprobantes");String[] titulos={"Archivo","Descripción","Tipo","UUID","Estado","Motivo de retiro","Justificación de diferencias"};
            var cabecera=hoja.createRow(0);for(int n=0;n<titulos.length;n++){cabecera.createCell(n).setCellValue(titulos[n]);hoja.setColumnWidth(n,36*256);}
            int fila=0;for(var d:datos){var r=hoja.createRow(++fila);String[] valores={d.nombre(),d.descripcion(),d.tipo(),d.uuid(),d.retirado()==null?"Vigente":"Retirado",d.motivoRetiro(),d.justificacion()};for(int n=0;n<valores.length;n++)r.createCell(n).setCellValue(java.util.Objects.toString(valores[n],""));}
            hoja.createFreezePane(0,1);libro.write(response.getOutputStream());
        }
    }

    public static String origenSeguro(String origen){return origen!=null&&origen.matches("/admin/compras(?:\\?[^\\r\\n#]*)?")?origen:"/admin/compras";}
    public static String regreso(Long id,String origen){return UriComponentsBuilder.fromPath("/admin/compras/"+id).queryParam("origen",origenSeguro(origen)).build().encode().toUriString()+"#compra-comprobantes";}

    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    String excedido(jakarta.servlet.http.HttpServletRequest request,RedirectAttributes flash) {
        String ruta=request.getRequestURI();var matcher=java.util.regex.Pattern.compile("/admin/compras/(\\d+)/comprobantes(?:/.*)?").matcher(ruta);
        flash.addFlashAttribute("compraTab","comprobantes");
        flash.addFlashAttribute("errorOperacion","El archivo supera el límite de carga. Usa PDF de hasta 10 MB, fotografía de hasta 20 MB o XML de hasta 2 MB");
        return matcher.matches()?"redirect:/admin/compras/"+matcher.group(1)+"#compra-comprobantes":"redirect:/admin/compras";
    }
    @GetMapping("/nuevo") String nuevo(@PathVariable Long id,@RequestParam(required=false)String origen,Model m){var d=compras.detalle(id);var f=new CompraDocumentoForm();f.setVersion(d.version());m.addAttribute("form",f);base(id,origen,"adjuntar",m);return "admin/compra-comprobante-form";}
    @PostMapping String agregar(@PathVariable Long id,@RequestParam(required=false)String origen,@Valid @ModelAttribute("form")CompraDocumentoForm f,BindingResult errores,Model m,RedirectAttributes flash){
        if(!errores.hasErrors())try{comprobantes.agregar(id,f);flash.addFlashAttribute("mensaje","Comprobante adjuntado. No se modificaron saldos ni partidas");return "redirect:"+destino(m);}catch(AccessDeniedException e){throw e;}catch(RuntimeException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
        f.setConfirmado(false);base(id,origen,"adjuntar",m);return "admin/compra-comprobante-form";
    }
    @GetMapping("/respaldo") String respaldo(@PathVariable Long id,@RequestParam(required=false)String origen,Model m){var actual=comprobantes.expediente(id,"","TODOS","",0).respaldo();var f=new CompraRespaldoForm();f.setVersion(actual==null?0:actual.id());if(actual!=null){f.setTipo(actual.tipo());f.setJustificacion(actual.justificacion());}m.addAttribute("form",f);base(id,origen,"respaldo",m);return "admin/compra-comprobante-form";}
    @PostMapping("/respaldo") String guardarRespaldo(@PathVariable Long id,@RequestParam(required=false)String origen,@Valid @ModelAttribute("form")CompraRespaldoForm f,BindingResult errores,Model m,RedirectAttributes flash){
        if(!errores.hasErrors())try{comprobantes.clasificar(id,f);flash.addFlashAttribute("mensaje","Respaldo documental actualizado; se conserva el historial");return "redirect:"+destino(m);}catch(AccessDeniedException e){throw e;}catch(RuntimeException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
        f.setConfirmado(false);base(id,origen,"respaldo",m);return "admin/compra-comprobante-form";
    }
    @GetMapping("/{doc}/abrir") ResponseEntity<Resource> abrir(@PathVariable Long id,@PathVariable Long doc){var a=comprobantes.abrir(id,doc);return ResponseEntity.ok().cacheControl(CacheControl.noStore())
        .contentType(MediaType.parseMediaType(a.tipoMime())).contentLength(a.tamanoBytes())
        .header("X-Content-Type-Options","nosniff").header("Content-Security-Policy",a.tipoMime().equals("application/xml")?"sandbox; default-src 'none'; frame-ancestors 'self'":"frame-ancestors 'self'")
        .header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.inline().filename(a.nombreOriginal(),StandardCharsets.UTF_8).build().toString()).body(a.recurso());}
    @GetMapping("/{doc}/fiscal") String fiscal(@PathVariable Long id,@PathVariable Long doc,@RequestParam(required=false)String origen,Model m){base(id,origen,"fiscal",m);m.addAttribute("fiscal",comprobantes.fiscal(id,doc));m.addAttribute("puedeAdministrar",org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(p->p.getAuthority().equals("COMPRA_ADMINISTRAR")));return "admin/compra-cfdi";}
    @GetMapping("/{doc}/retirar") String retiro(@PathVariable Long id,@PathVariable Long doc,@RequestParam(required=false)String origen,Model m){comprobantes.abrir(id,doc);base(id,origen,"retirar",m);m.addAttribute("doc",doc);return "admin/compra-comprobante-form";}
    @PostMapping("/{doc}/retirar") String retirar(@PathVariable Long id,@PathVariable Long doc,@RequestParam(required=false)String origen,@RequestParam(defaultValue="")String motivo,@RequestParam(defaultValue="false")boolean confirmado,Model m,RedirectAttributes flash){
        try{comprobantes.retirar(id,doc,motivo,confirmado);flash.addFlashAttribute("mensaje","Comprobante retirado del expediente vigente; el original y el motivo se conservan");return "redirect:"+destino(m);}catch(AccessDeniedException e){throw e;}catch(RuntimeException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));m.addAttribute("motivo",motivo);base(id,origen,"retirar",m);m.addAttribute("doc",doc);return "admin/compra-comprobante-form";}
    }
    @PostMapping("/{doc}/importar") String importar(@PathVariable Long id,@PathVariable Long doc,@RequestParam(required=false)String origen,@RequestParam Long version,@RequestParam(defaultValue="")String motivo,@RequestParam(defaultValue="false")boolean confirmado,Model m,RedirectAttributes flash){
        try{comprobantes.importar(id,doc,version,motivo,confirmado);flash.addFlashAttribute("mensaje","Partidas importadas en el borrador; no se registró ningún egreso");return "redirect:"+destino(m);}catch(AccessDeniedException e){throw e;}catch(RuntimeException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));m.addAttribute("motivo",motivo);return fiscal(id,doc,origen,m);}
    }
    private void base(Long id,String origen,String modo,Model m){m.addAttribute("detalle",compras.detalle(id));m.addAttribute("origen",origenSeguro(origen));m.addAttribute("volver",destino(m));m.addAttribute("modo",modo);}
}
