package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.CatalogoConsultaService;
import escuela.admin.service.ExcelCatalogoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class CatalogoAdminController {

    private final CatalogoConsultaService consultaService;
    private final ExcelCatalogoService excelService;

    @GetMapping("/admin")
    String admin() { return "redirect:/admin/catalogos/instituciones"; }

    @GetMapping("/admin/catalogos/{slug}")
    String catalogo(@PathVariable String slug,
                    @RequestParam(defaultValue = "") String q,
                    @RequestParam(defaultValue = "TODOS") String estado,
                    @RequestParam(defaultValue = "0") int pagina,
                    @RequestParam(defaultValue = "25") int tamanio,
                    Model model) {
        ModuloCatalogo modulo = ModuloCatalogo.desde(slug);
        FiltroCatalogo filtro = new FiltroCatalogo(q, estado, pagina, tamanio).normalizado();
        model.addAttribute("resultado", consultaService.consultar(modulo, filtro));
        model.addAttribute("filtro", filtro);
        model.addAttribute("modulos", ModuloCatalogo.values());
        return "admin/catalogo";
    }

    @GetMapping("/admin/catalogos/{slug}/excel")
    void excel(@PathVariable String slug,
               @RequestParam(defaultValue = "") String q,
               @RequestParam(defaultValue = "TODOS") String estado,
               HttpServletResponse response) throws IOException {
        ModuloCatalogo modulo = ModuloCatalogo.desde(slug);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String nombre = URLEncoder.encode(modulo.slug() + "-filtrado.xlsx", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + nombre);
        excelService.exportar(modulo, new FiltroCatalogo(q, estado, 0, 100), response.getOutputStream());
    }
}
