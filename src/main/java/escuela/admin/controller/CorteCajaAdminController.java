package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.CorteCajaConsultaService;
import escuela.admin.service.ExcelCorteCajaService;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.CorteCajaResponse;
import escuela.finanzas.entity.EstadoCorteCaja;
import escuela.finanzas.service.CorteCajaService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cortes-caja")
public class CorteCajaAdminController {
    private static final DateTimeFormatter FECHA = DateTimeFormatter
            .ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"));

    private final CorteCajaService service;
    private final CorteCajaConsultaService consultaService;
    private final ExcelCorteCajaService excelService;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping
    String listado(@RequestParam(required = false) Long institucionId,
                   @RequestParam(required = false) Long cuentaId,
                   @RequestParam(defaultValue = "") String cuentaTexto,
                   @RequestParam(defaultValue = "TODOS") String estado,
                   @RequestParam(required = false) LocalDate fechaDesde,
                   @RequestParam(required = false) LocalDate fechaHasta,
                   @RequestParam(defaultValue = "0") int pagina,
                   @RequestParam(defaultValue = "25") int tamanio,
                   Authentication authentication, Model model) {
        List<InstitucionResponse> instituciones = instituciones();
        if (institucionId == null && !instituciones.isEmpty()) institucionId = instituciones.getFirst().id();
        FiltroCorteCaja filtro = new FiltroCorteCaja(institucionId, cuentaId, cuentaTexto, estado,
                fechaDesde, fechaHasta, pagina, tamanio).normalizado();
        try {
            model.addAttribute("resultado", consultaService.consultar(filtro));
        } catch (ReglaNegocioException excepcion) {
            model.addAttribute("errorFiltro", excepcion.getMessage());
            model.addAttribute("resultado", new ResultadoCortesCaja(Page.empty(), 0));
        }
        model.addAttribute("filtro", filtro);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("estados", EstadoCorteCaja.values());
        navegacion(model, authentication);
        return "admin/cortes-caja";
    }

    @GetMapping("/excel")
    void excel(@RequestParam Long institucionId,
               @RequestParam(required = false) Long cuentaId,
               @RequestParam(defaultValue = "") String cuentaTexto,
               @RequestParam(defaultValue = "TODOS") String estado,
               @RequestParam(required = false) LocalDate fechaDesde,
               @RequestParam(required = false) LocalDate fechaHasta,
               HttpServletResponse response) throws IOException {
        FiltroCorteCaja filtro = new FiltroCorteCaja(institucionId, cuentaId, cuentaTexto, estado,
                fechaDesde, fechaHasta, 0, 100).normalizado();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String nombre = URLEncoder.encode("cortes-de-caja-filtrados.xlsx", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + nombre);
        excelService.exportar(filtro, response.getOutputStream());
    }

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        AperturaCorteCajaForm form = new AperturaCorteCajaForm();
        prepararApertura(model, form);
        return "admin/corte-caja-apertura-form";
    }

    @PostMapping
    String abrir(@Valid @ModelAttribute("form") AperturaCorteCajaForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            prepararApertura(model, form);
            return "admin/corte-caja-apertura-form";
        }
        try {
            CorteCajaResponse corte = service.abrir(form.request());
            flash.addFlashAttribute("mensaje", "Corte abierto; desde ahora los movimientos se conciliarán por folio");
            return "redirect:/admin/cortes-caja/" + corte.id();
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararApertura(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/corte-caja-apertura-form";
        }
    }

    @GetMapping("/{id}")
    String detalle(@PathVariable Long id, Model model) {
        CorteCajaResponse corte = service.obtener(id);
        CierreCorteCajaForm form = new CierreCorteCajaForm();
        form.setVersion(corte.version());
        prepararDetalle(model, corte, form);
        return "admin/corte-caja-detalle";
    }

    @PostMapping("/{id}/cerrar")
    String cerrar(@PathVariable Long id,
                  @Valid @ModelAttribute("form") CierreCorteCajaForm form,
                  BindingResult errores, Model model, RedirectAttributes flash) {
        CorteCajaResponse corte = service.obtener(id);
        if (errores.hasErrors()) {
            prepararDetalle(model, corte, form);
            return "admin/corte-caja-detalle";
        }
        try {
            service.cerrar(id, form.request());
            flash.addFlashAttribute("mensaje", "Corte cerrado y conciliado correctamente");
            return "redirect:/admin/cortes-caja/" + id;
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararDetalle(model, service.obtener(id), form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/corte-caja-detalle";
        }
    }

    private void prepararApertura(Model model, AperturaCorteCajaForm form) {
        List<InstitucionResponse> instituciones = instituciones();
        if (form.getInstitucionId() == null && instituciones.size() == 1)
            form.setInstitucionId(instituciones.getFirst().id());
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
    }

    private void prepararDetalle(Model model, CorteCajaResponse corte, CierreCorteCajaForm form) {
        ZoneId zona = ZoneId.of(institucionService.obtener(corte.institucionId()).zonaHoraria());
        model.addAttribute("corte", corte);
        model.addAttribute("form", form);
        model.addAttribute("abiertoEnLocal", FECHA.withZone(zona).format(corte.abiertoEn()));
        model.addAttribute("cerradoEnLocal", corte.cerradoEn() == null ? null
                : FECHA.withZone(zona).format(corte.cerradoEn()));
        model.addAttribute("puedeAdministrar", SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("CORTE_CAJA_ADMINISTRAR")));
    }

    private List<InstitucionResponse> instituciones() {
        return alcance.filtrarInstituciones(institucionService.listar());
    }

    private void navegacion(Model model, Authentication authentication) {
        Set<String> permisos = authentication.getAuthorities().stream().map(a -> a.getAuthority())
                .collect(Collectors.toUnmodifiableSet());
        model.addAttribute("modulos", Arrays.stream(ModuloCatalogo.values())
                .filter(m -> m.visibleCon(permisos)).toList());
        model.addAttribute("moduloActual", ModuloCatalogo.MOVIMIENTOS_FINANCIEROS);
        model.addAttribute("puedeAdministrar", permisos.contains("CORTE_CAJA_ADMINISTRAR"));
    }
}
