package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.CatalogoConsultaService;
import escuela.admin.service.ExcelCatalogoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class CatalogoAdminController {

    private final CatalogoConsultaService consultaService;
    private final ExcelCatalogoService excelService;

    @GetMapping("/admin")
    String admin(Authentication authentication) {
        return modulosVisibles(authentication).stream().findFirst()
                .map(modulo -> "redirect:" + modulo.rutaListado())
                .orElseGet(() -> authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("PORTAL_TUTOR_ACCEDER"))
                        ? "redirect:/portal" : "redirect:/acceso-denegado");
    }

    @GetMapping("/admin/catalogos/{slug}")
    String catalogo(@PathVariable String slug,
                    @RequestParam(defaultValue = "") String q,
                    @RequestParam(defaultValue = "TODOS") String estado,
                    @RequestParam(required = false) LocalDate fecha,
                    @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate desde,
                    @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate hasta,
                    @RequestParam(defaultValue = "VENCIMIENTO") String tipoFecha,
                    @RequestParam(defaultValue = "0") int pagina,
                    @RequestParam(defaultValue = "25") int tamanio,
                    Authentication authentication,
                    Model model) {
        ModuloCatalogo modulo = ModuloCatalogo.desde(slug);
        if (modulo == ModuloCatalogo.PORTAL_MAESTRO) return "redirect:" + modulo.rutaListado();
        if (modulo == ModuloCatalogo.PORTAL_TUTOR) {
            return "redirect:/admin/portal-soporte";
        }
        if (modulo == ModuloCatalogo.MOVIMIENTOS_FINANCIEROS) {
            return "redirect:" + modulo.rutaListado();
        }
        List<ModuloCatalogo> modulos = modulosVisibles(authentication);
        validarPermiso(modulo, modulos);
        FiltroCatalogo filtro = new FiltroCatalogo(q, estado, fecha, pagina, tamanio, desde, hasta, tipoFecha).normalizado();
        try {
            filtro.validarRango();
            model.addAttribute("resultado", consultaService.consultar(modulo, filtro));
        } catch (escuela.common.exception.ReglaNegocioException e) {
            model.addAttribute("errorFiltro", e.getMessage());
            model.addAttribute("resultado",new ResultadoCatalogo(modulo,modulo.columnas(),org.springframework.data.domain.Page.empty()));
        }
        model.addAttribute("hoyFiltros",LocalDate.now(java.time.ZoneId.of("America/Mexico_City")));
        model.addAttribute("filtro", filtro);
        model.addAttribute("modulos", modulos);
        model.addAttribute("puedeRegistrarPago", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PAGO_REGISTRAR")));
        return "admin/catalogo";
    }

    String catalogo(String slug, String q, String estado, int pagina, int tamanio,
                    Authentication authentication, Model model) {
        return catalogo(slug, q, estado, null, null, null, "VENCIMIENTO", pagina, tamanio, authentication, model);
    }

    @GetMapping("/admin/catalogos/{slug}/excel")
    void excel(@PathVariable String slug,
               @RequestParam(defaultValue = "") String q,
               @RequestParam(defaultValue = "TODOS") String estado,
               @RequestParam(required = false) LocalDate fecha,
               @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate desde,
               @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate hasta,
               @RequestParam(defaultValue = "VENCIMIENTO") String tipoFecha,
               Authentication authentication,
               HttpServletResponse response) throws IOException {
        ModuloCatalogo modulo = ModuloCatalogo.desde(slug);
        validarPermiso(modulo, modulosVisibles(authentication));
        FiltroCatalogo filtro=new FiltroCatalogo(q,estado,fecha,0,100,desde,hasta,tipoFecha).normalizado();
        filtro.validarRango();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String nombre = URLEncoder.encode(modulo.slug() + "-filtrado.xlsx", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + nombre);
        excelService.exportar(modulo, filtro, response.getOutputStream());
    }

    private List<ModuloCatalogo> modulosVisibles(Authentication authentication) {
        Set<String> permisos = authentication.getAuthorities().stream()
                .map(autoridad -> autoridad.getAuthority())
                .collect(Collectors.toUnmodifiableSet());
        return Arrays.stream(ModuloCatalogo.values())
                .filter(modulo -> modulo.visibleCon(permisos))
                .toList();
    }

    private void validarPermiso(ModuloCatalogo modulo, List<ModuloCatalogo> modulos) {
        if (!modulos.contains(modulo)) {
            throw new AccessDeniedException("No tienes permiso para consultar este módulo");
        }
    }
}
