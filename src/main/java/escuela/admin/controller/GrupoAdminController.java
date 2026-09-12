package escuela.admin.controller;

import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.dto.response.GradoResponse;
import escuela.academico.dto.response.GrupoResponse;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.Turno;
import escuela.academico.service.CicloEscolarService;
import escuela.academico.service.GradoService;
import escuela.academico.service.GrupoService;
import escuela.academico.service.NivelEducativoService;
import escuela.admin.dto.GrupoForm;
import escuela.admin.dto.OpcionGrado;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.dto.response.PlantelNivelResponse;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelNivelService;
import escuela.institucion.service.PlantelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/grupos")
public class GrupoAdminController {
    private final GrupoService service;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final PlantelNivelService ofertaService;
    private final CicloEscolarService cicloService;
    private final NivelEducativoService nivelService;
    private final GradoService gradoService;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new GrupoForm(), null);
        return "admin/grupo-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") GrupoForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/grupo-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/grupo-form";
        }
        flash.addFlashAttribute("mensaje", "Grupo creado correctamente");
        return "redirect:/admin/catalogos/grupos";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        GrupoResponse grupo = service.obtener(id);
        PlantelResponse plantel = plantelService.obtener(grupo.plantelId());
        preparar(model, GrupoForm.desde(grupo, plantel.institucionId()), id);
        return "admin/grupo-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") GrupoForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/grupo-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/grupo-form";
        }
        flash.addFlashAttribute("mensaje", "Grupo actualizado correctamente");
        return "redirect:/admin/catalogos/grupos";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            GrupoResponse grupo = service.obtener(id);
            PlantelResponse plantel = plantelService.obtener(grupo.plantelId());
            prepararError(model, GrupoForm.desde(grupo, plantel.institucionId()), id, excepcion);
            return "admin/grupo-form";
        }
        flash.addFlashAttribute("mensaje", "Grupo desactivado correctamente");
        return "redirect:/admin/catalogos/grupos";
    }

    private void preparar(Model model, GrupoForm form, Long id) {
        List<InstitucionResponse> instituciones = institucionService.listar();
        List<PlantelResponse> planteles = plantelService.listar();
        List<NivelEducativoResponse> niveles = nivelService.listar();
        List<CicloEscolarResponse> ciclos = instituciones.stream()
                .flatMap(institucion -> cicloService.listarPorInstitucion(institucion.id()).stream())
                .toList();
        List<OpcionGrado> grados = niveles.stream()
                .flatMap(nivel -> gradoService.listarPorNivel(nivel.id()).stream()
                        .map(grado -> opcion(grado, nivel)))
                .toList();
        List<PlantelNivelResponse> ofertas = planteles.stream()
                .flatMap(plantel -> ofertaService.listarPorPlantel(plantel.id()).stream())
                .toList();
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("grados", grados);
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("turnos", Turno.values());
    }

    private OpcionGrado opcion(GradoResponse grado, NivelEducativoResponse nivel) {
        String etiqueta = nivel.nombre() + " · " + grado.codigo() + " · " + grado.nombre();
        return new OpcionGrado(grado.id(), nivel.id(), nivel.institucionId(), etiqueta,
                grado.activo() && nivel.activo());
    }

    private void validarRelaciones(GrupoForm form, BindingResult errores) {
        if (form.getInstitucionId() == null || form.getPlantelId() == null
                || form.getCicloEscolarId() == null || form.getGradoId() == null) {
            return;
        }
        PlantelResponse plantel = plantelService.obtener(form.getPlantelId());
        CicloEscolarResponse ciclo = cicloService.obtener(form.getCicloEscolarId());
        GradoResponse grado = gradoService.obtener(form.getGradoId());
        NivelEducativoResponse nivel = nivelService.obtener(grado.nivelEducativoId());
        if (!plantel.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("plantelId", "grupo.plantel.institucion",
                    "El plantel seleccionado no pertenece a la institución indicada");
        }
        if (!ciclo.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("cicloEscolarId", "grupo.ciclo.institucion",
                    "El ciclo seleccionado no pertenece a la institución indicada");
        }
        if (!nivel.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("gradoId", "grupo.grado.institucion",
                    "El grado seleccionado no pertenece a la institución indicada");
        } else if (ofertaService.listarPorPlantel(plantel.id()).stream()
                .noneMatch(oferta -> oferta.activo()
                        && oferta.nivelEducativoId().equals(nivel.id()))) {
            errores.rejectValue("gradoId", "grupo.grado.oferta",
                    "El plantel no ofrece activamente el nivel de este grado");
        }
    }

    private void prepararError(Model model, GrupoForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
