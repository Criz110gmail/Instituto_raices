package escuela.admin.controller;

import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.service.CicloEscolarService;
import escuela.admin.dto.*;
import escuela.admin.service.BoletaConsultaService;
import escuela.admin.service.ExcelBoletaService;
import escuela.admin.service.JasperBoletaService;
import escuela.common.exception.ReglaNegocioException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/boletas")
public class BoletaAdminController {
    private final BoletaConsultaService consulta;
    private final ExcelBoletaService excel;
    private final JasperBoletaService jasper;
    private final InstitucionService institucionesService;
    private final PlantelService plantelesService;
    private final CicloEscolarService ciclosService;
    private final AlcanceDatosService alcance;

    @GetMapping
    String listado(@RequestParam(required = false) Long institucionId,
                   @RequestParam(required = false) Long cicloId,
                   @RequestParam(required = false) Long plantelId,
                   @RequestParam(required = false) Long grupoId,
                   @RequestParam(defaultValue = "") String grupoTexto,
                   @RequestParam(defaultValue = "") String q,
                   @RequestParam(defaultValue = "0") int pagina,
                   @RequestParam(defaultValue = "25") int tamanio,
                   Authentication authentication, Model model) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionesService.listar());
        if (institucionId == null && !instituciones.isEmpty()) institucionId = instituciones.getFirst().id();
        List<CicloEscolarResponse> ciclos = ciclos(institucionId);
        if (cicloId == null && !ciclos.isEmpty()) cicloId = ciclos.stream().filter(CicloEscolarResponse::predeterminado)
                .map(CicloEscolarResponse::id).findFirst().orElse(ciclos.getFirst().id());
        FiltroBoleta filtro = new FiltroBoleta(institucionId, cicloId, plantelId, grupoId,
                grupoTexto, q, pagina, tamanio);
        try {
            filtro = consulta.normalizar(filtro);
            model.addAttribute("resultado", consulta.consultar(filtro));
        } catch (ReglaNegocioException | RecursoNoEncontradoException excepcion) {
            model.addAttribute("errorFiltro", excepcion.getMessage());
            model.addAttribute("resultado", new ResultadoBoletas(Page.empty()));
        }
        Set<String> permisos = authentication.getAuthorities().stream().map(a -> a.getAuthority())
                .collect(Collectors.toUnmodifiableSet());
        model.addAttribute("modulos", Arrays.stream(ModuloCatalogo.values()).filter(m -> m.visibleCon(permisos)).toList());
        model.addAttribute("moduloActual", ModuloCatalogo.BOLETAS);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("planteles", alcance.filtrarPlanteles(plantelesService.listar()));
        model.addAttribute("filtro", filtro);
        return "admin/boletas";
    }

    @GetMapping("/excel")
    void excel(@RequestParam Long institucionId, @RequestParam Long cicloId,
               @RequestParam(required = false) Long plantelId,
               @RequestParam(required = false) Long grupoId,
               @RequestParam(defaultValue = "") String grupoTexto,
               @RequestParam(defaultValue = "") String q,
               HttpServletResponse response) throws IOException {
        FiltroBoleta filtro = new FiltroBoleta(institucionId, cicloId, plantelId, grupoId,
                grupoTexto, q, 0, 100);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", disposicion("attachment", "boletas.xlsx"));
        excel.exportar(filtro, response.getOutputStream());
    }

    @GetMapping("/pdf")
    void pdf(@RequestParam Long institucionId, @RequestParam Long cicloId,
             @RequestParam(required = false) Long plantelId,
             @RequestParam(required = false) Long grupoId,
             @RequestParam(defaultValue = "") String grupoTexto,
             @RequestParam(defaultValue = "") String q,
             HttpServletResponse response) throws IOException {
        FiltroBoleta filtro = new FiltroBoleta(institucionId, cicloId, plantelId, grupoId,
                grupoTexto, q, 0, 100);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", disposicion("inline", "boletas.pdf"));
        jasper.colectivo(filtro, response.getOutputStream());
    }

    @GetMapping("/{inscripcionId}/pdf")
    void individual(@PathVariable Long inscripcionId, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", disposicion("inline", "boleta-" + inscripcionId + ".pdf"));
        jasper.individual(inscripcionId, response.getOutputStream());
    }

    private List<CicloEscolarResponse> ciclos(Long institucionId) {
        if (institucionId == null) return List.of();
        try {
            alcance.validarInstitucion(institucionId);
            return alcance.filtrarCiclos(ciclosService.listarPorInstitucion(institucionId));
        } catch (RuntimeException excepcion) {
            return List.of();
        }
    }

    private String disposicion(String tipo, String archivo) {
        return tipo + "; filename*=UTF-8''" + URLEncoder.encode(archivo, StandardCharsets.UTF_8);
    }
}
