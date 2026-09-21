package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.ExcelRetiroFondoService;
import escuela.admin.service.RetiroFondoConsultaService;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.service.MotivoFinancieroService;
import escuela.finanzas.service.RetiroFondoService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/retiros-fondo")
public class RetiroFondoAdminController {
    private final RetiroFondoService registro;
    private final RetiroFondoConsultaService consulta;
    private final ExcelRetiroFondoService excel;
    private final MotivoFinancieroService motivos;
    private final InstitucionService institucionesService;
    private final PlantelService plantelesService;
    private final AlcanceDatosService alcance;

    @GetMapping
    String listado(@RequestParam(required = false) Long institucionId,
                   @RequestParam(required = false) Long cuentaId,
                   @RequestParam(defaultValue = "") String cuentaTexto,
                   @RequestParam(required = false) Long plantelId,
                   @RequestParam(required = false) LocalDate fechaDesde,
                   @RequestParam(required = false) LocalDate fechaHasta,
                   @RequestParam(defaultValue = "") String beneficiario,
                   @RequestParam(defaultValue = "0") int pagina,
                   @RequestParam(defaultValue = "25") int tamanio,
                   Authentication auth, Model model) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionesService.listar());
        if (institucionId == null && !instituciones.isEmpty()) institucionId = instituciones.getFirst().id();
        FiltroRetiroFondo filtro = new FiltroRetiroFondo(institucionId, cuentaId, cuentaTexto,
                plantelId, fechaDesde, fechaHasta, beneficiario, pagina, tamanio);
        try {
            filtro = filtro.normalizado();
            model.addAttribute("paginaRetiros", consulta.consultar(filtro));
        } catch (ReglaNegocioException ex) {
            model.addAttribute("errorFiltro", ex.getMessage());
            model.addAttribute("paginaRetiros", Page.empty());
        }
        model.addAttribute("filtro", filtro);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", alcance.filtrarPlanteles(plantelesService.listar()));
        model.addAttribute("modulos", modulosVisibles(auth));
        model.addAttribute("moduloActual", ModuloCatalogo.RETIROS_FONDO);
        model.addAttribute("puedeRegistrar", auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("RETIRO_FONDO_REGISTRAR")));
        return "admin/retiros-fondo";
    }

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        RetiroFondoForm form = new RetiroFondoForm();
        preparar(model, form);
        return "admin/retiro-fondo-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") RetiroFondoForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) { preparar(model, form); return "admin/retiro-fondo-form"; }
        try { registro.ejecutar(form.request()); }
        catch (ReglaNegocioException | DataIntegrityViolationException ex) {
            preparar(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex));
            return "admin/retiro-fondo-form";
        }
        flash.addFlashAttribute("mensaje", "Retiro externo registrado; el egreso ya aparece en la cuenta");
        return "redirect:/admin/retiros-fondo?institucionId=" + form.getInstitucionId()
                + "&cuentaId=" + form.getCuentaId() + "&cuentaTexto="
                + URLEncoder.encode(form.getCuentaTexto() == null ? "" : form.getCuentaTexto(), StandardCharsets.UTF_8);
    }

    @GetMapping("/excel")
    void excel(@RequestParam Long institucionId,
               @RequestParam(required = false) Long cuentaId,
               @RequestParam(defaultValue = "") String cuentaTexto,
               @RequestParam(required = false) Long plantelId,
               @RequestParam(required = false) LocalDate fechaDesde,
               @RequestParam(required = false) LocalDate fechaHasta,
               @RequestParam(defaultValue = "") String beneficiario,
               HttpServletResponse response) throws IOException {
        FiltroRetiroFondo filtro = new FiltroRetiroFondo(institucionId, cuentaId, cuentaTexto,
                plantelId, fechaDesde, fechaHasta, beneficiario, 0, 100).normalizado();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                + URLEncoder.encode("retiros-fondos-filtrados.xlsx", StandardCharsets.UTF_8));
        excel.exportar(filtro, response.getOutputStream());
    }

    private void preparar(Model model, RetiroFondoForm form) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionesService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1)
            form.setInstitucionId(instituciones.getFirst().id());
        if (form.getFechaOperacion() == null && form.getInstitucionId() != null)
            instituciones.stream().filter(i -> i.id().equals(form.getInstitucionId())).findFirst()
                    .ifPresent(i -> form.setFechaOperacion(LocalDateTime.now(ZoneId.of(i.zonaHoraria()))
                            .withSecond(0).withNano(0)));
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", alcance.filtrarPlanteles(plantelesService.listar()));
        model.addAttribute("motivos", form.getInstitucionId() == null ? List.of()
                : motivos.listarActivos(form.getInstitucionId()));
    }

    private List<ModuloCatalogo> modulosVisibles(Authentication auth) {
        Set<String> permisos = auth.getAuthorities().stream().map(a -> a.getAuthority())
                .collect(Collectors.toUnmodifiableSet());
        return Arrays.stream(ModuloCatalogo.values()).filter(m -> m.visibleCon(permisos)).toList();
    }
}
