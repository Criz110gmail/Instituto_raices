package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.institucion.dto.response.*;
import escuela.institucion.service.*;
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
@RequestMapping("/admin/reportes-financieros")
public class ReporteFinancieroAdminController {
    private final ReporteFinancieroConsultaService consulta;
    private final ExcelReporteFinancieroService excel;
    private final EstadoCuentaCuentaService estadoCuentaCuenta;
    private final ExcelEstadoCuentaCuentaService excelCuenta;
    private final PdfEstadoCuentaCuentaService pdfCuenta;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping("/tesoreria")
    String tesoreria(@RequestParam(required = false) Long institucionId,
                     @RequestParam(required = false) Long cuentaId,
                     @RequestParam(defaultValue = "") String cuentaTexto,
                     @RequestParam(required = false) Long plantelId,
                     @RequestParam(defaultValue = "DIARIA") String agrupacion,
                     @RequestParam(required = false) LocalDate fechaDesde,
                     @RequestParam(required = false) LocalDate fechaHasta,
                     @RequestParam(defaultValue = "0") int pagina,
                     @RequestParam(defaultValue = "25") int tamanio,
                     Authentication authentication, Model model) {
        var instituciones = instituciones();
        if (institucionId == null && !instituciones.isEmpty()) institucionId = instituciones.getFirst().id();
        FiltroReporteTesoreria filtro = new FiltroReporteTesoreria(institucionId, cuentaId, cuentaTexto,
                plantelId, agrupacion, fechaDesde, fechaHasta, pagina, tamanio);
        try {
            filtro = consulta.normalizar(filtro);
            model.addAttribute("resultado", consulta.tesoreria(filtro));
        } catch (ReglaNegocioException | RecursoNoEncontradoException excepcion) {
            model.addAttribute("errorFiltro", excepcion.getMessage());
            model.addAttribute("resultado", new ResultadoReporteTesoreria(Page.empty(),
                    new ResumenTesoreria(0, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO,
                            java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO,
                            null, null, false, "MXN")));
        }
        comunes(model, authentication, instituciones);
        model.addAttribute("filtro", filtro);
        model.addAttribute("agrupaciones", AgrupacionReporte.values());
        model.addAttribute("reporteActual", "TESORERIA");
        return "admin/reporte-tesoreria";
    }

    @GetMapping("/estado-cuenta-cuenta")
    String estadoCuentaCuenta(@RequestParam(required = false) Long institucionId,
                             @RequestParam(required = false) Long cuentaId,
                             @RequestParam(defaultValue = "") String cuentaTexto,
                             @RequestParam(defaultValue = "MENSUAL") String periodo,
                             @RequestParam(defaultValue = "0") int anio,
                             @RequestParam(required = false) Integer mes,
                             @RequestParam(defaultValue = "0") int pagina,
                             @RequestParam(defaultValue = "25") int tamanio,
                             Authentication authentication, Model model) {
        var instituciones = instituciones();
        if (institucionId == null && !instituciones.isEmpty()) institucionId = instituciones.getFirst().id();
        FiltroEstadoCuentaCuenta filtro = new FiltroEstadoCuentaCuenta(institucionId, cuentaId,
                cuentaTexto, periodo, anio, mes, pagina, tamanio);
        try {
            filtro = estadoCuentaCuenta.normalizar(filtro);
            model.addAttribute("resultadoCuenta", estadoCuentaCuenta.consultar(filtro));
        } catch (ReglaNegocioException | RecursoNoEncontradoException excepcion) {
            model.addAttribute("errorFiltro", excepcion.getMessage());
            model.addAttribute("resultadoCuenta", new ResultadoEstadoCuentaCuenta(null, null, Page.empty()));
        }
        comunes(model, authentication, instituciones);
        model.addAttribute("filtroCuenta", filtro);
        model.addAttribute("meses", new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"});
        return "admin/estado-cuenta-cuenta";
    }

    @GetMapping("/estado-cuenta-cuenta/excel")
    void estadoCuentaCuentaExcel(@RequestParam Long institucionId, @RequestParam Long cuentaId,
                                 @RequestParam(defaultValue = "") String cuentaTexto,
                                 @RequestParam(defaultValue = "MENSUAL") String periodo,
                                 @RequestParam int anio, @RequestParam(required = false) Integer mes,
                                 HttpServletResponse response) throws IOException {
        var filtro = estadoCuentaCuenta.normalizar(new FiltroEstadoCuentaCuenta(institucionId, cuentaId,
                cuentaTexto, periodo, anio, mes, 0, 100));
        prepararExcel(response, "estado-cuenta-" + filtro.periodo().toLowerCase() + "-" + filtro.anio() + ".xlsx");
        excelCuenta.exportar(filtro, response.getOutputStream());
    }

    @GetMapping("/estado-cuenta-cuenta/pdf")
    void estadoCuentaCuentaPdf(@RequestParam Long institucionId, @RequestParam Long cuentaId,
                               @RequestParam(defaultValue = "") String cuentaTexto,
                               @RequestParam(defaultValue = "MENSUAL") String periodo,
                               @RequestParam int anio, @RequestParam(required = false) Integer mes,
                               HttpServletResponse response) throws IOException {
        var filtro = estadoCuentaCuenta.normalizar(new FiltroEstadoCuentaCuenta(institucionId, cuentaId,
                cuentaTexto, periodo, anio, mes, 0, 100));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" +
                URLEncoder.encode("estado-cuenta-" + filtro.periodo().toLowerCase() + "-" + filtro.anio() + ".pdf",
                        StandardCharsets.UTF_8));
        pdfCuenta.exportar(filtro, response.getOutputStream());
    }

    @GetMapping("/tesoreria/excel")
    void tesoreriaExcel(@RequestParam Long institucionId, @RequestParam(required = false) Long cuentaId,
                        @RequestParam(defaultValue = "") String cuentaTexto,
                        @RequestParam(required = false) Long plantelId,
                        @RequestParam(defaultValue = "DIARIA") String agrupacion,
                        @RequestParam LocalDate fechaDesde, @RequestParam LocalDate fechaHasta,
                        HttpServletResponse response) throws IOException {
        var filtro = consulta.normalizar(new FiltroReporteTesoreria(institucionId, cuentaId, cuentaTexto,
                plantelId, agrupacion, fechaDesde, fechaHasta, 0, 100));
        prepararExcel(response, "reporte-tesoreria.xlsx"); excel.tesoreria(filtro, response.getOutputStream());
    }

    @GetMapping("/estado-cuenta")
    String estadoCuenta(@RequestParam(required = false) Long institucionId,
                        @RequestParam(required = false) Long alumnoId,
                        @RequestParam(defaultValue = "") String alumnoTexto,
                        @RequestParam(required = false) Long plantelId,
                        @RequestParam(defaultValue = "TODOS") String situacion,
                        @RequestParam(required = false) LocalDate fechaCorte,
                        @RequestParam(defaultValue = "0") int pagina,
                        @RequestParam(defaultValue = "25") int tamanio,
                        Authentication authentication, Model model) {
        var instituciones = instituciones();
        if (institucionId == null && !instituciones.isEmpty()) institucionId = instituciones.getFirst().id();
        FiltroEstadoCuentaAlumno filtro = new FiltroEstadoCuentaAlumno(institucionId, alumnoId, alumnoTexto,
                plantelId, situacion, fechaCorte, pagina, tamanio);
        try {
            filtro = consulta.normalizar(filtro);
            model.addAttribute("resultado", consulta.estadoCuenta(filtro));
        } catch (ReglaNegocioException | RecursoNoEncontradoException excepcion) {
            model.addAttribute("errorFiltro", excepcion.getMessage());
            model.addAttribute("resultado", new ResultadoEstadoCuentaAlumno(null, null, null,
                    Page.empty(), ResumenEstadoCuenta.vacio("MXN")));
        }
        comunes(model, authentication, instituciones);
        model.addAttribute("filtro", filtro);
        model.addAttribute("situaciones", List.of("PENDIENTE", "PARCIAL", "PAGADO", "VENCIDO", "CANCELADO"));
        model.addAttribute("reporteActual", "ESTADO_CUENTA");
        return "admin/estado-cuenta-alumno";
    }

    @GetMapping("/estado-cuenta/excel")
    void estadoCuentaExcel(@RequestParam Long institucionId, @RequestParam Long alumnoId,
                           @RequestParam(defaultValue = "") String alumnoTexto,
                           @RequestParam(required = false) Long plantelId,
                           @RequestParam(defaultValue = "TODOS") String situacion,
                           @RequestParam LocalDate fechaCorte, HttpServletResponse response) throws IOException {
        var filtro = consulta.normalizar(new FiltroEstadoCuentaAlumno(institucionId, alumnoId, alumnoTexto,
                plantelId, situacion, fechaCorte, 0, 100));
        prepararExcel(response, "estado-cuenta-alumno.xlsx"); excel.estadoCuenta(filtro, response.getOutputStream());
    }

    private void comunes(Model model, Authentication authentication, List<InstitucionResponse> instituciones) {
        Set<String> permisos = authentication.getAuthorities().stream().map(a -> a.getAuthority())
                .collect(Collectors.toUnmodifiableSet());
        model.addAttribute("modulos", Arrays.stream(ModuloCatalogo.values()).filter(m -> m.visibleCon(permisos)).toList());
        model.addAttribute("moduloActual", ModuloCatalogo.REPORTES_FINANCIEROS);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", alcance.filtrarPlanteles(plantelService.listar()));
    }

    private List<InstitucionResponse> instituciones() {
        return alcance.filtrarInstituciones(institucionService.listar());
    }

    private void prepararExcel(HttpServletResponse response, String archivo) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                + URLEncoder.encode(archivo, StandardCharsets.UTF_8));
    }
}
