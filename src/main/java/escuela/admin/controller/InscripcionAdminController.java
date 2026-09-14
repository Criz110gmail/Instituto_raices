package escuela.admin.controller;

import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.dto.response.GradoResponse;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.academico.service.CicloEscolarService;
import escuela.academico.service.GradoService;
import escuela.academico.service.GrupoService;
import escuela.academico.service.NivelEducativoService;
import escuela.admin.dto.AsignacionGrupoForm;
import escuela.admin.dto.InscripcionForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.dto.OpcionGrado;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.alumno.service.AlumnoService;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.dto.response.InscripcionResponse;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.service.InscripcionService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.dto.response.PlantelNivelResponse;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelNivelService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/inscripciones")
public class InscripcionAdminController {

    private final InscripcionService service;
    private final AlumnoService alumnoService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final PlantelNivelService ofertaService;
    private final CicloEscolarService cicloService;
    private final NivelEducativoService nivelService;
    private final GradoService gradoService;
    private final GrupoService grupoService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new InscripcionForm(), null);
        return "admin/inscripcion-form";
    }

    @GetMapping("/{id}/continuar")
    String continuar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, id);
        preparar(model, InscripcionForm.continuidad(service.obtener(id)), null);
        return "admin/inscripcion-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") InscripcionForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        validarAlcance(form);
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/inscripcion-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/inscripcion-form";
        }
        flash.addFlashAttribute("mensaje", form.getInscripcionAnteriorId() == null
                ? "Inscripción creada correctamente"
                : "La trayectoria anterior se cerró y la nueva inscripción fue creada");
        return "redirect:/admin/catalogos/inscripciones";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, id);
        preparar(model, InscripcionForm.desde(service.obtener(id)), id);
        return "admin/inscripcion-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") InscripcionForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, id);
        validarAlcance(form);
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/inscripcion-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/inscripcion-form";
        }
        flash.addFlashAttribute("mensaje", "Inscripción actualizada correctamente");
        return "redirect:/admin/catalogos/inscripciones";
    }

    @PostMapping("/{id}/asignaciones")
    String asignarGrupo(@PathVariable Long id,
                        @Valid @ModelAttribute("asignacionForm") AsignacionGrupoForm asignacionForm,
                        BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, id);
        if (asignacionForm.getGrupoId() != null) {
            alcance.validarRecurso(ModuloCatalogo.GRUPOS, asignacionForm.getGrupoId());
        }
        InscripcionResponse inscripcion = service.obtener(id);
        if (errores.hasErrors()) {
            preparar(model, InscripcionForm.desde(inscripcion), id);
            return "admin/inscripcion-form";
        }
        try {
            service.asignarGrupo(id, asignacionForm.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            preparar(model, InscripcionForm.desde(inscripcion), id);
            model.addAttribute("errorAsignacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/inscripcion-form";
        }
        flash.addFlashAttribute("mensaje", "Asignación de grupo registrada correctamente");
        return "redirect:/admin/inscripciones/" + id + "/editar";
    }

    @PostMapping("/{id}/asignaciones/{asignacionId}/finalizar")
    String finalizarAsignacion(@PathVariable Long id, @PathVariable Long asignacionId,
                               @RequestParam Long version,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                               Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, id);
        InscripcionResponse inscripcion = service.obtener(id);
        try {
            service.finalizarAsignacion(id, asignacionId, version, fechaFin);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            preparar(model, InscripcionForm.desde(inscripcion), id);
            model.addAttribute("errorAsignacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/inscripcion-form";
        }
        flash.addFlashAttribute("mensaje", "Asignación finalizada correctamente");
        return "redirect:/admin/inscripciones/" + id + "/editar";
    }

    private void preparar(Model model, InscripcionForm form, Long id) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        List<NivelEducativoResponse> niveles = alcance.filtrarNiveles(nivelService.listar());
        List<CicloEscolarResponse> ciclos = instituciones.stream()
                .flatMap(i -> cicloService.listarPorInstitucion(i.id()).stream()).toList();
        List<OpcionGrado> grados = niveles.stream()
                .flatMap(nivel -> gradoService.listarPorNivel(nivel.id()).stream()
                        .map(grado -> opcion(grado, nivel))).toList();
        List<PlantelNivelResponse> ofertas = planteles.stream()
                .flatMap(plantel -> ofertaService.listarPorPlantel(plantel.id()).stream()).toList();

        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("grados", grados);
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("estados", EstadoInscripcion.values());
        model.addAttribute("alumnoSeleccionado", etiquetaAlumno(form.getAlumnoId()));
        model.addAttribute("continuidadAnterior", etiquetaInscripcion(form.getInscripcionAnteriorId()));

        if (id != null) {
            InscripcionResponse inscripcion = service.obtener(id);
            model.addAttribute("inscripcion", inscripcion);
            model.addAttribute("permiteContinuidad", inscripcion.estado() == EstadoInscripcion.ACTIVA
                    || inscripcion.estado() == EstadoInscripcion.PREINSCRITA);
            model.addAttribute("asignaciones", service.listarAsignaciones(id));
            model.addAttribute("grupos", grupoService.listarPorPlantelYCiclo(
                    inscripcion.plantelId(), inscripcion.cicloEscolarId()).stream()
                    .filter(grupo -> grupo.gradoId().equals(inscripcion.gradoId())).toList());
            if (!model.containsAttribute("asignacionForm")) {
                AsignacionGrupoForm asignacion = new AsignacionGrupoForm();
                asignacion.setFechaInicio(inscripcion.fechaInicio().isAfter(LocalDate.now())
                        ? inscripcion.fechaInicio() : LocalDate.now());
                model.addAttribute("asignacionForm", asignacion);
            }
        }
    }

    private OpcionGrado opcion(GradoResponse grado, NivelEducativoResponse nivel) {
        return new OpcionGrado(grado.id(), nivel.id(), nivel.institucionId(),
                nivel.nombre() + " · " + grado.codigo() + " · " + grado.nombre(),
                grado.activo() && nivel.activo());
    }

    private void validarRelaciones(InscripcionForm form, BindingResult errores) {
        if (form.getInstitucionId() == null || form.getAlumnoId() == null
                || form.getPlantelId() == null || form.getCicloEscolarId() == null
                || form.getGradoId() == null) return;
        var alumno = alumnoService.obtener(form.getAlumnoId());
        var plantel = plantelService.obtener(form.getPlantelId());
        var ciclo = cicloService.obtener(form.getCicloEscolarId());
        var grado = gradoService.obtener(form.getGradoId());
        var nivel = nivelService.obtener(grado.nivelEducativoId());
        if (!alumno.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("alumnoId", "inscripcion.alumno.institucion",
                    "El alumno no pertenece a la institución indicada");
        }
        if (!plantel.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("plantelId", "inscripcion.plantel.institucion",
                    "El plantel no pertenece a la institución indicada");
        }
        if (!ciclo.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("cicloEscolarId", "inscripcion.ciclo.institucion",
                    "El ciclo no pertenece a la institución indicada");
        }
        if (!nivel.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("gradoId", "inscripcion.grado.institucion",
                    "El grado no pertenece a la institución indicada");
        }
    }

    private void validarAlcance(InscripcionForm form) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getAlumnoId() != null) alcance.validarRecurso(ModuloCatalogo.ALUMNOS, form.getAlumnoId());
        if (form.getPlantelId() != null) alcance.validarPlantel(form.getPlantelId());
        if (form.getCicloEscolarId() != null) alcance.validarRecurso(ModuloCatalogo.CICLOS, form.getCicloEscolarId());
        if (form.getGradoId() != null) alcance.validarRecurso(ModuloCatalogo.GRADOS, form.getGradoId());
        if (form.getInscripcionAnteriorId() != null) {
            alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, form.getInscripcionAnteriorId());
        }
    }

    private void prepararError(Model model, InscripcionForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }

    private String etiquetaAlumno(Long id) {
        if (id == null) return "";
        var alumno = alumnoService.obtener(id);
        return alumno.matricula() + " · " + alumno.nombres() + " " + alumno.primerApellido();
    }

    private String etiquetaInscripcion(Long id) {
        if (id == null) return "";
        var anterior = service.obtener(id);
        return anterior.numeroInscripcion() + " · " + anterior.plantelNombre()
                + " · " + anterior.cicloNombre();
    }
}
