package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.docente.dto.PlaneacionForm;
import escuela.docente.entity.EstadoPlaneacion;
import escuela.docente.service.*;
import escuela.horario.service.HorarioClaseService;
import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/portal-maestros-soporte")
public class PortalMaestroSoporteAdminController {
    private final SoportePortalMaestroService soporte;
    private final PlaneacionService planeaciones;
    private final PortalAlumnoMaestroService alumnos;
    private final FotografiaMaestroService fotografias;
    private final HorarioClaseService horarios;
    private final PdfPlaneacionService pdf;

    @GetMapping
    String listado(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") int pagina,
                   @RequestParam(defaultValue = "25") int tamanio,
                   @AuthenticationPrincipal UsuarioPrincipal admin, Model model) {
        model.addAttribute("resultado", soporte.listar(admin, q, pagina, tamanio));
        model.addAttribute("q", q);
        var permisos = admin.authorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
        model.addAttribute("modulos", Arrays.stream(ModuloCatalogo.values()).filter(m -> m.visibleCon(permisos)).toList());
        model.addAttribute("moduloActual", ModuloCatalogo.PORTAL_MAESTRO);
        return "admin/portal-maestros-soporte";
    }

    @GetMapping("/excel")
    void excel(@RequestParam(defaultValue = "") String q, @AuthenticationPrincipal UsuarioPrincipal admin,
               HttpServletResponse response) throws IOException {
        var bloque = soporte.listar(admin, q, 0, 100); // Authorize before producing the response.
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=soporte-maestros.xlsx");
        try (var libro = new SXSSFWorkbook(100)) {
            var hoja = libro.createSheet("Maestros disponibles");
            var encabezado = hoja.createRow(0);
            String[] titulos = {"Número de empleado", "Maestro", "Usuario"};
            for (int i = 0; i < titulos.length; i++) { encabezado.createCell(i).setCellValue(titulos[i]); hoja.setColumnWidth(i, i == 1 ? 12000 : 6500); }
            int fila = 1, pagina = 0;
            while (true) {
                for (var maestro : bloque) {
                    var row = hoja.createRow(fila++);
                    row.createCell(0).setCellValue(maestro.numero());
                    row.createCell(1).setCellValue(maestro.nombre());
                    row.createCell(2).setCellValue(maestro.usuario());
                }
                if (!bloque.hasNext()) break;
                bloque = soporte.listar(admin, q, ++pagina, 100);
            }
            hoja.createFreezePane(0, 1);
            libro.write(response.getOutputStream());
        }
    }

    @GetMapping("/{maestroId}")
    String inicio(@PathVariable Long maestroId, @RequestParam(required = false) LocalDate desde,
                  @RequestParam(required = false) LocalDate hasta, @RequestParam(required = false) EstadoPlaneacion estado,
                  @RequestParam(defaultValue = "0") int pagina, @AuthenticationPrincipal UsuarioPrincipal admin, Model m) {
        var docente = preparar(maestroId, "PLANEACIONES", admin, m);
        m.addAttribute("desde", desde); m.addAttribute("hasta", hasta); m.addAttribute("estado", estado);
        m.addAttribute("estados", EstadoPlaneacion.values());
        if (desde != null && hasta != null && hasta.isBefore(desde)) m.addAttribute("errorFiltro", "La fecha final debe ser igual o posterior a la fecha inicial");
        m.addAttribute("planeaciones", planeaciones.listarMaestro(docente, desde, hasta, estado, pagina));
        return "maestros/inicio";
    }

    @GetMapping("/{maestroId}/horario")
    String horario(@PathVariable Long maestroId, @RequestParam(required = false) LocalDate fecha,
                   @AuthenticationPrincipal UsuarioPrincipal admin, Model m) {
        var docente = preparar(maestroId, "HORARIO", admin, m);
        m.addAttribute("fecha", fecha == null ? LocalDate.now() : fecha);
        m.addAttribute("horario", horarios.horarioMaestro(docente, fecha));
        return "maestros/horario";
    }

    @GetMapping("/{maestroId}/alumnos")
    String alumnos(@PathVariable Long maestroId, @RequestParam(defaultValue = "") String q,
                   @RequestParam(defaultValue = "0") int pagina, @AuthenticationPrincipal UsuarioPrincipal admin, Model m) {
        var docente = preparar(maestroId, "ALUMNOS", admin, m);
        m.addAttribute("q", q); m.addAttribute("alumnos", alumnos.listar(docente, q, pagina));
        return "maestros/alumnos";
    }

    @GetMapping("/{maestroId}/alumnos/{alumnoId}")
    String alumno(@PathVariable Long maestroId, @PathVariable Long alumnoId,
                  @AuthenticationPrincipal UsuarioPrincipal admin, Model m) {
        var docente = preparar(maestroId, "FICHA_ALUMNO", admin, m);
        m.addAttribute("alumno", alumnos.detalle(docente, alumnoId));
        return "maestros/alumno-detalle";
    }

    @GetMapping("/{maestroId}/planeaciones/{id}")
    String planeacion(@PathVariable Long maestroId, @PathVariable Long id,
                      @AuthenticationPrincipal UsuarioPrincipal admin, Model m) {
        var docente = preparar(maestroId, "DETALLE_PLANEACION", admin, m);
        m.addAttribute("planeacion", planeaciones.detalleMaestro(docente, id));
        return "maestros/planeacion-detalle";
    }

    @GetMapping("/{maestroId}/planeaciones/{id}/captura")
    String captura(@PathVariable Long maestroId, @PathVariable Long id,
                   @AuthenticationPrincipal UsuarioPrincipal admin, Model m) {
        var docente = preparar(maestroId, "CAPTURA_PLANEACION", admin, m);
        var documento = planeaciones.detalleMaestro(docente, id);
        var form = PlaneacionForm.desde(documento, planeaciones.versionEntidad(id));
        m.addAttribute("form", form); m.addAttribute("id", id); m.addAttribute("edicion", true);
        // Preserve the historical group even if its teaching assignment is no longer active.
        m.addAttribute("grupos", List.of(new escuela.docente.dto.AsignacionMaestroResponse(null,
                documento.grupoId(), documento.grupo(), documento.plantel(), documento.ciclo(),
                null, null, documento.fechaInicio(), documento.fechaFin(), false, null)));
        m.addAttribute("materias", documento.materias());
        return "maestros/planeacion-form";
    }

    @GetMapping("/{maestroId}/planeaciones/{id}/pdf")
    void pdf(@PathVariable Long maestroId, @PathVariable Long id,
             @AuthenticationPrincipal UsuarioPrincipal admin, HttpServletResponse response) throws IOException {
        var docente = soporte.contexto(admin, maestroId, "PDF_PLANEACION");
        var documento = planeaciones.detalleMaestro(docente, id);
        response.setContentType("application/pdf"); response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=planeacion-" + id + ".pdf");
        pdf.exportar(documento, response.getOutputStream());
    }

    @GetMapping("/{maestroId}/fotografia")
    ResponseEntity<Resource> fotoMaestro(@PathVariable Long maestroId, @AuthenticationPrincipal UsuarioPrincipal admin) {
        soporte.contexto(admin, maestroId, "FOTOGRAFIA_MAESTRO");
        return archivo(fotografias.descargar(maestroId));
    }

    @GetMapping("/{maestroId}/alumnos/{alumnoId}/fotografia")
    ResponseEntity<Resource> fotoAlumno(@PathVariable Long maestroId, @PathVariable Long alumnoId,
                                        @AuthenticationPrincipal UsuarioPrincipal admin) {
        return archivo(alumnos.fotografia(soporte.contexto(admin, maestroId, "FOTOGRAFIA_ALUMNO"), alumnoId));
    }

    private UsuarioPrincipal preparar(Long id, String seccion, UsuarioPrincipal admin, Model m) {
        var docente = soporte.contexto(admin, id, seccion);
        var perfil = planeaciones.perfil(docente);
        m.addAttribute("perfil", perfil); m.addAttribute("fotoPerfil", fotografias.actual(perfil.id()) != null);
        m.addAttribute("soporte", true); m.addAttribute("rutaPortal", "/admin/portal-maestros-soporte/" + id);
        return docente;
    }

    private ResponseEntity<Resource> archivo(ArchivoDescarga a) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.parseMediaType(a.tipoMime()))
                .contentLength(a.tamanoBytes()).header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(a.nombreOriginal(), StandardCharsets.UTF_8).build().toString()).body(a.recurso());
    }
}
