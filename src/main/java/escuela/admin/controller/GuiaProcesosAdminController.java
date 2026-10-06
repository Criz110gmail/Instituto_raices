package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.service.GuiaProcesosService;
import escuela.admin.service.GuiaProcesosService.Filtro;
import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/admin/guias", "/admin/catalogos/guias"})
public class GuiaProcesosAdminController {
    private final GuiaProcesosService service;

    @GetMapping
    String listado(@AuthenticationPrincipal UsuarioPrincipal principal,
                   @ModelAttribute Filtro filtro,Model model) {
        var f=filtro.normalizado(); model.addAttribute("filtro",f);
        model.addAttribute("resultado",service.listar(principal,f));
        navegacion(principal,model); return "admin/guias";
    }

    @GetMapping("/{slug}")
    String detalle(@AuthenticationPrincipal UsuarioPrincipal principal,@PathVariable String slug,Model model) {
        var detalle=service.obtener(principal,slug);
        model.addAttribute("guia",detalle.guia()); model.addAttribute("pasos",detalle.pasos());
        navegacion(principal,model); return "admin/guia-detalle";
    }

    @GetMapping("/excel")
    void excel(@AuthenticationPrincipal UsuarioPrincipal principal,@ModelAttribute Filtro filtro,
               HttpServletResponse response) throws IOException {
        var f=filtro.normalizado(); var bloque=service.listar(principal,new Filtro(f.q(),f.categoria(),f.estado(),0,100));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=guias-procesos.xlsx");
        response.setHeader("Cache-Control","no-store");
        try(var libro=new SXSSFWorkbook(100)) {
            var hoja=libro.createSheet("Guías y pasos");
            String[] titulos={"Guía","Categoría","Estado","Versión","Revisión","Requisitos","Ejemplo del proceso",
                    "Paso","Perfil","Módulo","Instrucciones","Ejemplo","Resultado esperado","Precaución","Ruta"};
            var header=hoja.createRow(0);
            for(int i=0;i<titulos.length;i++){header.createCell(i).setCellValue(titulos[i]);hoja.setColumnWidth(i,8000);}
            hoja.createFreezePane(0,1); int fila=1,pagina=0;
            while(true) {
                for(var guia:bloque) for(var paso:service.obtener(principal,guia.slug()).pasos()) {
                    var row=hoja.createRow(fila++);
                    String[] datos={guia.titulo(),guia.categoria(),guia.estado(),""+guia.versionContenido(),
                            guia.revisadaEl().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            guia.requisitos(),guia.ejemplo(),""+paso.numero(),paso.perfil(),paso.modulo(),
                            paso.instrucciones(),paso.ejemplo(),paso.resultado(),paso.precaucion(),paso.ruta()==null?"":paso.ruta()};
                    for(int i=0;i<datos.length;i++) row.createCell(i).setCellValue(datos[i]);
                }
                if(!bloque.hasNext())break;
                bloque=service.listar(principal,new Filtro(f.q(),f.categoria(),f.estado(),++pagina,100));
            }
            libro.write(response.getOutputStream());
        }
    }

    private void navegacion(UsuarioPrincipal principal,Model model) {
        var permisos=principal.authorities().stream().map(a->a.getAuthority()).collect(Collectors.toSet());
        model.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(m->m.visibleCon(permisos)).toList());
        model.addAttribute("moduloActual",ModuloCatalogo.GUIAS);
    }
}
