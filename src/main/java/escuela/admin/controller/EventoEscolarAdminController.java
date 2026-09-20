package escuela.admin.controller;

import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.service.CicloEscolarService;
import escuela.admin.dto.*;
import escuela.admin.service.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.comunicacion.dto.response.EventoEscolarResponse;
import escuela.comunicacion.entity.*;
import escuela.comunicacion.service.EventoEscolarService;
import escuela.common.exception.*;
import escuela.institucion.dto.response.*;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/eventos-escolares")
public class EventoEscolarAdminController {
    private final EventoEscolarService service;
    private final EventoEscolarConsultaService consulta;
    private final ExcelEventoEscolarService excel;
    private final DestinatarioEventoBusquedaService destinatarios;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final CicloEscolarService cicloService;
    private final AlcanceDatosService alcance;

    @GetMapping
    String listado(@RequestParam(required = false) Long institucionId,
                   @RequestParam(required = false) Long plantelId,
                   @RequestParam(defaultValue = "") String texto,
                   @RequestParam(defaultValue = "TODOS") String estado,
                   @RequestParam(defaultValue = "TODOS") String tipo,
                   @RequestParam(required = false) LocalDate fechaDesde,
                   @RequestParam(required = false) LocalDate fechaHasta,
                   @RequestParam(defaultValue = "0") int pagina,
                   @RequestParam(defaultValue = "25") int tamanio,
                   Authentication authentication, Model model) {
        List<InstitucionResponse> instituciones = instituciones();
        if (institucionId == null && !instituciones.isEmpty()) institucionId = instituciones.getFirst().id();
        FiltroEventoEscolar filtro = new FiltroEventoEscolar(institucionId, plantelId, texto, estado,
                tipo, fechaDesde, fechaHasta, pagina, tamanio).normalizado();
        try { model.addAttribute("pagina", consulta.consultar(filtro)); }
        catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
            model.addAttribute("errorFiltro", ex.getMessage()); model.addAttribute("pagina", org.springframework.data.domain.Page.empty());
        }
        model.addAttribute("filtro", filtro); model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", alcance.filtrarPlanteles(plantelService.listar()));
        model.addAttribute("estados", EstadoEventoEscolar.values()); model.addAttribute("tipos", TipoEventoEscolar.values());
        navegacion(model, authentication); return "admin/eventos-escolares";
    }

    @GetMapping("/excel")
    void excel(@RequestParam Long institucionId, @RequestParam(required = false) Long plantelId,
               @RequestParam(defaultValue = "") String texto, @RequestParam(defaultValue = "TODOS") String estado,
               @RequestParam(defaultValue = "TODOS") String tipo,
               @RequestParam(required = false) LocalDate fechaDesde,
               @RequestParam(required = false) LocalDate fechaHasta, HttpServletResponse response) throws IOException {
        var filtro = new FiltroEventoEscolar(institucionId, plantelId, texto, estado, tipo,
                fechaDesde, fechaHasta, 0, 100).normalizado();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" +
                URLEncoder.encode("eventos-escolares.xlsx", StandardCharsets.UTF_8));
        excel.exportar(filtro, response.getOutputStream());
    }

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        EventoEscolarForm form = new EventoEscolarForm();
        List<InstitucionResponse> instituciones = instituciones();
        if (instituciones.size() == 1) form.setInstitucionId(instituciones.getFirst().id());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        if (planteles.size() == 1 && !alcance.alcanceInstitucionalActual(planteles.getFirst().institucionId())) {
            form.setPlantelId(planteles.getFirst().id()); form.setAlcance(AlcanceEventoEscolar.PLANTEL);
        }
        ZoneId zona = form.getInstitucionId() == null ? ZoneId.systemDefault()
                : ZoneId.of(institucionService.obtener(form.getInstitucionId()).zonaHoraria());
        LocalDate manana = LocalDate.now(zona).plusDays(1);
        form.setInicioLocal(manana.atTime(8, 0)); form.setFinLocal(manana.atTime(9, 0));
        prepararFormulario(model, form, null, instituciones); return "admin/evento-escolar-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") EventoEscolarForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        validarDestinatarios(form, errores);
        if (errores.hasErrors()) { prepararFormulario(model, form, null, instituciones()); return "admin/evento-escolar-form"; }
        try {
            EventoEscolarResponse creado = service.crear(form.request());
            flash.addFlashAttribute("mensaje", "Evento guardado como borrador; revísalo antes de publicarlo");
            return "redirect:/admin/eventos-escolares/" + creado.id();
        } catch (ReglaNegocioException | DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            prepararFormulario(model, form, null, instituciones());
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex)); return "admin/evento-escolar-form";
        }
    }

    @GetMapping("/{id}")
    String detalle(@PathVariable Long id, Model model, Authentication authentication) {
        EventoEscolarResponse evento = service.obtener(id); CancelacionEventoForm cancelacion = new CancelacionEventoForm();
        cancelacion.setVersion(evento.auditoria().version()); prepararDetalle(model, evento, cancelacion, authentication);
        return "admin/evento-escolar-detalle";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        EventoEscolarResponse evento = service.obtener(id);
        if (evento.estado() != EstadoEventoEscolar.BORRADOR)
            throw new ReglaNegocioException("Sólo los eventos en borrador pueden modificarse");
        prepararFormulario(model, EventoEscolarForm.desde(evento), id, instituciones()); return "admin/evento-escolar-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") EventoEscolarForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        validarDestinatarios(form, errores);
        if (errores.hasErrors()) { prepararFormulario(model, form, id, instituciones()); return "admin/evento-escolar-form"; }
        try { service.actualizar(id, form.request()); flash.addFlashAttribute("mensaje", "Borrador actualizado");
            return "redirect:/admin/eventos-escolares/" + id;
        } catch (ReglaNegocioException | DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            prepararFormulario(model, form, id, instituciones()); model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex));
            return "admin/evento-escolar-form";
        }
    }

    @PostMapping("/{id}/publicar")
    String publicar(@PathVariable Long id, @RequestParam Long version, Model model,
                    Authentication authentication, RedirectAttributes flash) {
        try {
            service.publicar(id, version);
            flash.addFlashAttribute("mensaje", "Evento publicado para sus destinatarios");
            return "redirect:/admin/eventos-escolares/" + id;
        } catch (ReglaNegocioException | DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            EventoEscolarResponse evento = service.obtener(id);
            CancelacionEventoForm cancelacion = new CancelacionEventoForm();
            cancelacion.setVersion(evento.auditoria().version());
            prepararDetalle(model, evento, cancelacion, authentication);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex));
            return "admin/evento-escolar-detalle";
        }
    }

    @PostMapping("/{id}/cancelar")
    String cancelar(@PathVariable Long id, @Valid @ModelAttribute("cancelacion") CancelacionEventoForm form,
                    BindingResult errores, Model model, Authentication authentication, RedirectAttributes flash) {
        if (errores.hasErrors()) { prepararDetalle(model, service.obtener(id), form, authentication); return "admin/evento-escolar-detalle"; }
        try { service.cancelar(id, form.getVersion(), form.getMotivo());
            flash.addFlashAttribute("mensaje", "Evento cancelado; el historial se conservó");
            return "redirect:/admin/eventos-escolares/" + id;
        } catch (ReglaNegocioException | DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            prepararDetalle(model, service.obtener(id), form, authentication);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex)); return "admin/evento-escolar-detalle";
        }
    }

    private void validarDestinatarios(EventoEscolarForm form, BindingResult errores) {
        List<String> valores = form.getDestinatarios() == null ? List.of() : form.getDestinatarios();
        if (valores.size() > 200) errores.rejectValue("destinatarios", "evento.maximo", "Agrega como máximo 200 destinos por evento");
        try { valores.forEach(EventoEscolarForm::destino); }
        catch (IllegalArgumentException ex) { errores.rejectValue("destinatarios", "evento.destino", "La selección de destinatarios no es válida"); }
    }

    private void prepararFormulario(Model model, EventoEscolarForm form, Long id,
                                    List<InstitucionResponse> instituciones) {
        model.addAttribute("form", form); model.addAttribute("id", id); model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", alcance.filtrarPlanteles(plantelService.listar()));
        List<CicloEscolarResponse> ciclos = instituciones.stream().flatMap(i -> cicloService.listarPorInstitucion(i.id()).stream()).toList();
        model.addAttribute("ciclos", alcance.filtrarCiclos(ciclos)); model.addAttribute("tipos", TipoEventoEscolar.values());
        model.addAttribute("alcances", AlcanceEventoEscolar.values());
        model.addAttribute("destinatariosSeleccionados", destinatarios.resolver(form.getDestinatarios()));
    }

    private void prepararDetalle(Model model, EventoEscolarResponse evento, CancelacionEventoForm form,
                                 Authentication authentication) {
        model.addAttribute("evento", evento); model.addAttribute("cancelacion", form);
        model.addAttribute("puedeAdministrar", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("EVENTO_ESCOLAR_ADMINISTRAR")));
    }

    private List<InstitucionResponse> instituciones() { return alcance.filtrarInstituciones(institucionService.listar()); }
    private void navegacion(Model model, Authentication authentication) {
        Set<String> permisos = authentication.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toUnmodifiableSet());
        model.addAttribute("modulos", Arrays.stream(ModuloCatalogo.values()).filter(m -> m.visibleCon(permisos)).toList());
        model.addAttribute("moduloActual", ModuloCatalogo.EVENTOS_ESCOLARES);
        model.addAttribute("puedeAdministrar", permisos.contains("EVENTO_ESCOLAR_ADMINISTRAR"));
    }
}
