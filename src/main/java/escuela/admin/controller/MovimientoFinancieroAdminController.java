package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.ExcelMovimientoFinancieroService;
import escuela.admin.service.MovimientoFinancieroConsultaService;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.entity.ClaseMovimiento;
import escuela.finanzas.entity.DireccionMovimiento;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/movimientos-financieros")
public class MovimientoFinancieroAdminController {

    private final MovimientoFinancieroConsultaService consultaService;
    private final ExcelMovimientoFinancieroService excelService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping
    String listado(@RequestParam(required = false) Long institucionId,
                   @RequestParam(required = false) Long cuentaId,
                   @RequestParam(defaultValue = "") String cuentaTexto,
                   @RequestParam(required = false) Long plantelId,
                   @RequestParam(defaultValue = "TODOS") String direccion,
                   @RequestParam(defaultValue = "TODOS") String clase,
                   @RequestParam(required = false) LocalDate fechaDesde,
                   @RequestParam(required = false) LocalDate fechaHasta,
                   @RequestParam(defaultValue = "0") int pagina,
                   @RequestParam(defaultValue = "25") int tamanio,
                   Authentication authentication, Model model) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        if (institucionId == null && !instituciones.isEmpty()) institucionId = instituciones.getFirst().id();
        FiltroMovimientoFinanciero filtro = new FiltroMovimientoFinanciero(institucionId, cuentaId,
                cuentaTexto, plantelId, direccion, clase, fechaDesde, fechaHasta, pagina, tamanio).normalizado();
        try {
            model.addAttribute("resultado", consultaService.consultar(filtro));
        } catch (ReglaNegocioException excepcion) {
            model.addAttribute("errorFiltro", excepcion.getMessage());
            model.addAttribute("resultado", new ResultadoMovimientosFinancieros(Page.empty(), null,
                    java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO));
        }
        model.addAttribute("filtro", filtro);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", alcance.filtrarPlanteles(plantelService.listar()));
        model.addAttribute("direcciones", DireccionMovimiento.values());
        model.addAttribute("clases", ClaseMovimiento.values());
        model.addAttribute("modulos", modulosVisibles(authentication));
        model.addAttribute("moduloActual", ModuloCatalogo.MOVIMIENTOS_FINANCIEROS);
        return "admin/movimientos-financieros";
    }

    @GetMapping("/excel")
    void excel(@RequestParam Long institucionId,
               @RequestParam(required = false) Long cuentaId,
               @RequestParam(defaultValue = "") String cuentaTexto,
               @RequestParam(required = false) Long plantelId,
               @RequestParam(defaultValue = "TODOS") String direccion,
               @RequestParam(defaultValue = "TODOS") String clase,
               @RequestParam(required = false) LocalDate fechaDesde,
               @RequestParam(required = false) LocalDate fechaHasta,
               HttpServletResponse response) throws IOException {
        FiltroMovimientoFinanciero filtro = new FiltroMovimientoFinanciero(institucionId, cuentaId,
                cuentaTexto, plantelId, direccion, clase, fechaDesde, fechaHasta, 0, 100).normalizado();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String nombre = URLEncoder.encode("movimientos-financieros-filtrados.xlsx", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + nombre);
        excelService.exportar(filtro, response.getOutputStream());
    }

    private List<ModuloCatalogo> modulosVisibles(Authentication authentication) {
        Set<String> permisos = authentication.getAuthorities().stream().map(a -> a.getAuthority())
                .collect(Collectors.toUnmodifiableSet());
        return Arrays.stream(ModuloCatalogo.values()).filter(m -> m.visibleCon(permisos)).toList();
    }
}
