package escuela.portal.controller;

import escuela.alumno.service.FotografiaAlumnoService;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.portal.service.PortalTutorService;
import escuela.portal.service.NotificacionPortalService;
import escuela.portal.service.PortalBoletaService;
import escuela.admin.service.JasperBoletaService;
import escuela.calificacion.service.CalificacionService;
import escuela.portal.dto.PortalTutorResultado;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.horario.service.HorarioClaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/portal")
public class PortalTutorController {
    private final PortalTutorService service;
    private final FotografiaAlumnoService fotografiaService;
    private final NotificacionPortalService notificaciones;
    private final CalificacionService calificaciones;
    private final PortalBoletaService boletas;
    private final JasperBoletaService jasperBoletas;
    private final HorarioClaseService horarios;

    @GetMapping
    String portal(@RequestParam(required = false) Long alumnoId,
                  @RequestParam(defaultValue = "0") int paginaEventos,
                  @RequestParam(defaultValue = "0") int paginaCargos,
                  @RequestParam(defaultValue = "0") int paginaAvisos,
                  @RequestParam(defaultValue = "0") int paginaNotificaciones,
                  @RequestParam(defaultValue = "0") int paginaPagos,
                  @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("portal", service.consultar(principal, alumnoId, paginaEventos, paginaCargos,
                paginaAvisos, paginaNotificaciones, paginaPagos));
        return "portal/inicio";
    }

    @GetMapping("/notificaciones")
    String notificaciones(@RequestParam(required = false) Long alumnoId,
                          @RequestParam(defaultValue = "0") int pagina,
                          @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        return seccion("NOTIFICACIONES", alumnoId, 0, 0, 0, pagina, 0, principal, model);
    }

    @GetMapping("/avisos")
    String avisos(@RequestParam(required = false) Long alumnoId,
                  @RequestParam(defaultValue = "0") int pagina,
                  @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        return seccion("AVISOS", alumnoId, 0, 0, pagina, 0, 0, principal, model);
    }

    @GetMapping("/agenda")
    String agenda(@RequestParam(required = false) Long alumnoId,
                  @RequestParam(defaultValue = "0") int pagina,
                  @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        return seccion("AGENDA", alumnoId, pagina, 0, 0, 0, 0, principal, model);
    }

    @GetMapping("/pagos")
    String pagos(@RequestParam(required = false) Long alumnoId,
                 @RequestParam(defaultValue = "0") int paginaCargos,
                 @RequestParam(defaultValue = "0") int paginaPagos,
                 @RequestParam(required = false) Integer mes,
                 @RequestParam(required = false) Integer anio,
                 @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        PortalTutorResultado resultado = service.consultarPagos(principal, alumnoId, paginaCargos,
                paginaPagos, mes, anio);
        model.addAttribute("portal", resultado);
        model.addAttribute("seccion", "PAGOS");
        model.addAttribute("rutaInicio", "/portal");
        model.addAttribute("rutaSeccion", "/portal/pagos");
        agregarFiltrosPagos(model, principal, resultado, mes, anio);
        return "portal/seccion";
    }

    @GetMapping("/calificaciones")
    String calificaciones(@RequestParam(required = false) Long alumnoId,
                          @RequestParam(defaultValue = "0") int pagina,
                          @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        String vista = seccion("CALIFICACIONES", alumnoId, 0, 0, 0, 0, 0, principal, model);
        PortalTutorResultado portal = (PortalTutorResultado) model.getAttribute("portal");
        model.addAttribute("calificaciones", portal == null || portal.hijo() == null
                ? org.springframework.data.domain.Page.empty()
                : calificaciones.publicadasAlumno(portal.hijo().alumnoId(), pagina, 10));
        return vista;
    }

    @GetMapping("/boletas")
    String boletas(@RequestParam(required = false) Long alumnoId,
                   @RequestParam(defaultValue = "0") int pagina,
                   @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        String vista = seccion("BOLETAS", alumnoId, 0, 0, 0, 0, 0, principal, model);
        PortalTutorResultado portal = (PortalTutorResultado) model.getAttribute("portal");
        model.addAttribute("boletas", portal == null || portal.hijo() == null
                ? org.springframework.data.domain.Page.empty()
                : boletas.listar(principal, portal.hijo().alumnoId(), pagina));
        return vista;
    }

    @GetMapping("/horario")
    String horario(@RequestParam(required=false)Long alumnoId,@RequestParam(required=false)java.time.LocalDate fecha,
                   @AuthenticationPrincipal UsuarioPrincipal principal,Model model){String vista=seccion("HORARIO",alumnoId,0,0,0,0,0,principal,model);PortalTutorResultado p=(PortalTutorResultado)model.getAttribute("portal");java.time.LocalDate f=fecha==null?java.time.LocalDate.now():fecha;model.addAttribute("fechaHorario",f);model.addAttribute("horario",p==null||p.hijo()==null?java.util.List.of():horarios.horarioAlumno(p.hijo().alumnoId(),f));return vista;}

    @GetMapping("/boletas/{inscripcionId}/pdf")
    void boletaPdf(@PathVariable Long inscripcionId, @RequestParam Long alumnoId,
                   @AuthenticationPrincipal UsuarioPrincipal principal,
                   jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        var detalle = boletas.detalle(principal, alumnoId, inscripcionId);
        response.setContentType("application/pdf");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" +
                URLEncoder.encode("boleta-" + detalle.ciclo() + ".pdf", StandardCharsets.UTF_8));
        jasperBoletas.exportar(detalle, response.getOutputStream());
    }

    @PostMapping("/notificaciones/{id}/leer")
    String leer(@PathVariable Long id,@RequestParam(required=false)Long alumnoId,
                @AuthenticationPrincipal UsuarioPrincipal principal){
        String destino=notificaciones.marcarLeida(principal,id);
        return "redirect:/portal/"+destino+(alumnoId==null?"":"?alumnoId="+alumnoId);
    }

    private String seccion(String seccion, Long alumnoId, int paginaEventos, int paginaCargos,
                           int paginaAvisos, int paginaNotificaciones, int paginaPagos,
                           UsuarioPrincipal principal, Model model) {
        model.addAttribute("portal", service.consultar(principal, alumnoId, paginaEventos, paginaCargos,
                paginaAvisos, paginaNotificaciones, paginaPagos));
        model.addAttribute("seccion", seccion);
        model.addAttribute("rutaInicio", "/portal");
        model.addAttribute("rutaSeccion", "/portal/" + seccion.toLowerCase(java.util.Locale.ROOT));
        return "portal/seccion";
    }

    private void agregarFiltrosPagos(Model model, UsuarioPrincipal principal,
                                     PortalTutorResultado resultado, Integer mes, Integer anio) {
        model.addAttribute("mesPago", mes);
        model.addAttribute("anioPago", anio);
        model.addAttribute("nombreMesPago", nombreMes(mes));
        model.addAttribute("aniosPago", resultado.hijo() == null
                ? java.util.List.of()
                : service.aniosPagos(principal, resultado.hijo().alumnoId()));
    }

    private String nombreMes(Integer mes) {
        if (mes == null) return "Todos los meses";
        return java.time.Month.of(mes).getDisplayName(java.time.format.TextStyle.FULL,
                java.util.Locale.forLanguageTag("es-MX"));
    }

    @GetMapping("/alumnos/{alumnoId}/fotografia")
    ResponseEntity<Resource> fotografia(@PathVariable Long alumnoId,
                                         @AuthenticationPrincipal UsuarioPrincipal principal) {
        service.validarHijo(principal, alumnoId);
        var actual = fotografiaService.actual(alumnoId);
        if (actual == null) return avatarGenerico();
        ArchivoDescarga descarga = fotografiaService.descargar(alumnoId, actual.id());
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType(descarga.tipoMime()))
                .contentLength(descarga.tamanoBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(descarga.recurso());
    }

    private ResponseEntity<Resource> avatarGenerico() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(new ClassPathResource("static/images/avatar-generico.svg"));
    }
}
