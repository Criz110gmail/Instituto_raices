package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.*;
import escuela.auditoria.entity.AccionAuditoria;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/auditoria")
public class AuditoriaAdminController {
    private final AuditoriaConsultaService consulta;
    private final ExcelAuditoriaService excel;
    private final InstitucionService instituciones;
    private final AlcanceDatosService alcance;

    @GetMapping
    String listado(@RequestParam(required=false)Long institucionId,@RequestParam(defaultValue="TODAS")String accion,
                   @RequestParam(defaultValue="")String tipoEntidad,@RequestParam(defaultValue="")String entidadId,
                   @RequestParam(defaultValue="")String actor,@RequestParam(defaultValue="")String correlacion,
                   @RequestParam(required=false)LocalDate fechaDesde,@RequestParam(required=false)LocalDate fechaHasta,
                   @RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,
                   Authentication authentication,Model model){
        var disponibles=alcance.filtrarInstituciones(instituciones.listar());
        if(institucionId==null&&!disponibles.isEmpty())institucionId=disponibles.getFirst().id();
        var filtro=new FiltroAuditoria(institucionId,accion,tipoEntidad,entidadId,actor,correlacion,fechaDesde,fechaHasta,pagina,tamanio).normalizado();
        try{model.addAttribute("pagina",consulta.consultar(filtro));}
        catch(ReglaNegocioException ex){model.addAttribute("errorFiltro",ex.getMessage());model.addAttribute("pagina",Page.empty());}
        model.addAttribute("filtro",filtro);model.addAttribute("instituciones",disponibles);
        model.addAttribute("acciones",AccionAuditoria.values());model.addAttribute("tipos",List.of("PAGO","DEVOLUCION_PAGO","TRANSFERENCIA_CUENTA","MOVIMIENTO_FINANCIERO","ROL_PERMISO","USUARIO_ROL","EVENTO_ESCOLAR","AVISO"));
        Set<String> permisos=authentication.getAuthorities().stream().map(a->a.getAuthority()).collect(Collectors.toSet());
        model.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(m->m.visibleCon(permisos)).toList());
        model.addAttribute("moduloActual",ModuloCatalogo.AUDITORIA);return "admin/auditoria";
    }

    @GetMapping("/excel")
    void excel(@RequestParam Long institucionId,@RequestParam(defaultValue="TODAS")String accion,
               @RequestParam(defaultValue="")String tipoEntidad,@RequestParam(defaultValue="")String entidadId,
               @RequestParam(defaultValue="")String actor,@RequestParam(defaultValue="")String correlacion,
               @RequestParam(required=false)LocalDate fechaDesde,@RequestParam(required=false)LocalDate fechaHasta,
               HttpServletResponse response)throws IOException{
        var filtro=new FiltroAuditoria(institucionId,accion,tipoEntidad,entidadId,actor,correlacion,fechaDesde,fechaHasta,0,100).normalizado();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String nombre=URLEncoder.encode("auditoria-filtrada.xlsx",StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+nombre);excel.exportar(filtro,response.getOutputStream());
    }
}
