package escuela.admin.controller;

import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.academico.dto.response.PeriodoAcademicoResponse;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.TipoPeriodoAcademico;
import escuela.academico.service.CicloEscolarService;
import escuela.academico.service.NivelEducativoService;
import escuela.academico.service.PeriodoAcademicoService;
import escuela.admin.dto.PeriodoAcademicoForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/periodos")
public class PeriodoAcademicoAdminController {
    private final PeriodoAcademicoService service;
    private final CicloEscolarService cicloService;
    private final NivelEducativoService nivelService;
    private final InstitucionService institucionService;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new PeriodoAcademicoForm(), null);
        return "admin/periodo-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") PeriodoAcademicoForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/periodo-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/periodo-form";
        }
        flash.addFlashAttribute("mensaje", "Periodo académico creado correctamente");
        return "redirect:/admin/catalogos/periodos";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        PeriodoAcademicoResponse periodo = service.obtener(id);
        CicloEscolarResponse ciclo = cicloService.obtener(periodo.cicloEscolarId());
        preparar(model, PeriodoAcademicoForm.desde(periodo, ciclo.institucionId()), id);
        return "admin/periodo-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") PeriodoAcademicoForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/periodo-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/periodo-form";
        }
        flash.addFlashAttribute("mensaje", "Periodo académico actualizado correctamente");
        return "redirect:/admin/catalogos/periodos";
    }

    private void preparar(Model model, PeriodoAcademicoForm form, Long id) {
        List<InstitucionResponse> instituciones = institucionService.listar();
        List<CicloEscolarResponse> ciclos = instituciones.stream()
                .flatMap(institucion -> cicloService.listarPorInstitucion(institucion.id()).stream())
                .toList();
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("niveles", nivelService.listar());
        model.addAttribute("tipos", TipoPeriodoAcademico.values());
        model.addAttribute("estados", EstadoAcademico.values());
    }

    private void validarRelaciones(PeriodoAcademicoForm form, BindingResult errores) {
        if (form.getInstitucionId() == null || form.getCicloEscolarId() == null
                || form.getNivelEducativoId() == null) {
            return;
        }
        CicloEscolarResponse ciclo = cicloService.obtener(form.getCicloEscolarId());
        NivelEducativoResponse nivel = nivelService.obtener(form.getNivelEducativoId());
        if (!ciclo.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("cicloEscolarId", "periodo.ciclo.institucion",
                    "El ciclo seleccionado no pertenece a la institución indicada");
        }
        if (!nivel.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("nivelEducativoId", "periodo.nivel.institucion",
                    "El nivel seleccionado no pertenece a la institución indicada");
        }
    }

    private void prepararError(Model model, PeriodoAcademicoForm form, Long id,
                               RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
