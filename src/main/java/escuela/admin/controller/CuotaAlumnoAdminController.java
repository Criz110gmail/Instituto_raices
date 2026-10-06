package escuela.admin.controller;

import escuela.admin.dto.CuotaAlumnoForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.cobranza.dto.response.CuotaAlumnoResponse;
import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.entity.FrecuenciaCuota;
import escuela.cobranza.service.ConceptoCobroService;
import escuela.cobranza.service.CobranzaInscripcionService;
import escuela.cobranza.service.CuotaAlumnoService;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.service.InscripcionService;
import escuela.academico.service.CicloEscolarService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.service.AlcanceDatosService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cuotas-alumno")
public class CuotaAlumnoAdminController {
    private final CuotaAlumnoService service;
    private final ConceptoCobroService conceptoService;
    private final CobranzaInscripcionService cobranzaService;
    private final InscripcionService inscripcionService;
    private final CicloEscolarService cicloService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping("/vigencia-inscripcion")
    @ResponseBody
    VigenciaCuota vigenciaInscripcion(@RequestParam Long inscripcionId) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, inscripcionId);
        var inscripcion = inscripcionService.obtener(inscripcionId);
        var ciclo = cicloService.obtener(inscripcion.cicloEscolarId());
        LocalDate inicio = inscripcion.fechaInicio().isAfter(ciclo.fechaInicio())
                ? inscripcion.fechaInicio() : ciclo.fechaInicio();
        LocalDate fin = inscripcion.fechaFin() == null || inscripcion.fechaFin().isAfter(ciclo.fechaFin())
                ? ciclo.fechaFin() : inscripcion.fechaFin();
        return new VigenciaCuota(inicio, fin);
    }

    record VigenciaCuota(LocalDate inicio, LocalDate fin) { }

    @GetMapping("/nuevo")
    String nuevo(@RequestParam(required = false) Long inscripcionId, Model model) {
        CuotaAlumnoForm form = new CuotaAlumnoForm();
        form.setDiaVencimiento(10);
        if (inscripcionId != null) prepararDesdeInscripcion(form, inscripcionId);
        preparar(model, form, null);
        return "admin/cuota-alumno-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") CuotaAlumnoForm form,
                 BindingResult errores, Authentication authentication,
                 Model model, RedirectAttributes flash) {
        validarAlcance(form);
        validarRelaciones(form, errores);
        if (form.isGenerarCargoAhora() && !tienePermiso(authentication, "CARGO_ADMINISTRAR")) {
            errores.reject("cuota.generar.permiso",
                    "No tienes permiso para generar el cargo al alumno inmediatamente");
        }
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/cuota-alumno-form";
        }
        try {
            var resultado = cobranzaService.crearCuota(form.request(), form.isGenerarCargoAhora());
            if (form.getRetornoInscripcionId() != null) {
                flash.addFlashAttribute("mensaje", resultado.cargo() == null
                        ? "Cuota preparada correctamente"
                        : "Cuota y pago por cobrar generados correctamente");
                return "redirect:/admin/inscripciones/" + form.getRetornoInscripcionId()
                        + "/editar#cobranza-inscripcion";
            }
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/cuota-alumno-form";
        }
        flash.addFlashAttribute("mensaje", form.isGeneracionAutomatica()
                ? "Cuota creada; quedó preparada para generación automática de cargos"
                : "Cuota creada con generación manual");
        return "redirect:/admin/catalogos/cuotas-alumno";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.CUOTAS_ALUMNO, id);
        preparar(model, CuotaAlumnoForm.desde(service.obtener(id)), id);
        return "admin/cuota-alumno-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") CuotaAlumnoForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CUOTAS_ALUMNO, id);
        validarAlcance(form);
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/cuota-alumno-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/cuota-alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Configuración de cuota actualizada correctamente");
        return "redirect:/admin/catalogos/cuotas-alumno";
    }

    private void preparar(Model model, CuotaAlumnoForm form, Long id) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        if (form.getPlantelId() == null && planteles.size() == 1) {
            form.setPlantelId(planteles.getFirst().id());
        }
        if ((form.getMoneda() == null || form.getMoneda().isBlank()) && form.getInstitucionId() != null) {
            instituciones.stream().filter(i -> i.id().equals(form.getInstitucionId())).findFirst()
                    .map(InstitucionResponse::monedaPredeterminada).ifPresent(form::setMoneda);
        }
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
        model.addAttribute("frecuencias", FrecuenciaCuota.values());
        model.addAttribute("estados", EstadoCuota.values());
        model.addAttribute("inscripcionSeleccionada", etiquetaInscripcion(form.getInscripcionId()));
        model.addAttribute("conceptoSeleccionado", etiquetaConcepto(form.getConceptoCobroId()));
        model.addAttribute("modoAsistido", form.getRetornoInscripcionId() != null);
        model.addAttribute("puedeGenerarCargo", tienePermiso(
                SecurityContextHolder.getContext().getAuthentication(), "CARGO_ADMINISTRAR"));
    }

    private void validarAlcance(CuotaAlumnoForm form) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getPlantelId() != null) alcance.validarPlantel(form.getPlantelId());
        if (form.getInscripcionId() != null) {
            alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, form.getInscripcionId());
        }
        if (form.getConceptoCobroId() != null) {
            alcance.validarRecurso(ModuloCatalogo.CONCEPTOS_COBRO, form.getConceptoCobroId());
        }
    }

    private void validarRelaciones(CuotaAlumnoForm form, BindingResult errores) {
        if (form.getInscripcionId() != null) {
            var vigencia = vigenciaInscripcion(form.getInscripcionId());
            form.prepararCalendario(vigencia.inicio(), vigencia.fin());
        }
        if (form.getFechaInicio() == null) errores.rejectValue("fechaInicio", "cuota.inicio", "Selecciona el primer mes que se cobrará");
        if (form.getFechaFin() == null) errores.rejectValue("fechaFin", "cuota.fin", "Selecciona el último mes que se cobrará");
        if (form.getFrecuencia() == FrecuenciaCuota.MENSUAL) {
            if (form.getPrimerMes() == null) errores.rejectValue("primerMes", "cuota.mes.inicio", "Selecciona el primer mes que se cobrará");
            if (form.getUltimoMes() == null) errores.rejectValue("ultimoMes", "cuota.mes.fin", "Selecciona el último mes que se cobrará");
        }
        if (form.getInstitucionId() == null || form.getPlantelId() == null
                || form.getInscripcionId() == null || form.getConceptoCobroId() == null) return;
        var inscripcion = inscripcionService.obtener(form.getInscripcionId());
        var concepto = conceptoService.obtener(form.getConceptoCobroId());
        if (!inscripcion.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("inscripcionId", "cuota.inscripcion.institucion",
                    "La inscripción no pertenece a la institución indicada");
        }
        if (!inscripcion.plantelId().equals(form.getPlantelId())) {
            errores.rejectValue("inscripcionId", "cuota.inscripcion.plantel",
                    "La inscripción no pertenece al plantel indicado");
        }
        if (!concepto.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("conceptoCobroId", "cuota.concepto.institucion",
                    "El concepto no pertenece a la institución indicada");
        }
        if (form.getRetornoInscripcionId() != null
                && !form.getRetornoInscripcionId().equals(form.getInscripcionId())) {
            errores.rejectValue("inscripcionId", "cuota.inscripcion.retorno",
                    "La inscripción del asistente no puede cambiarse");
        }
    }

    private String etiquetaInscripcion(Long id) {
        if (id == null) return "";
        var inscripcion = inscripcionService.obtener(id);
        return inscripcion.numeroInscripcion() + " · " + inscripcion.alumnoNombre();
    }

    private String etiquetaConcepto(Long id) {
        if (id == null) return "";
        var concepto = conceptoService.obtener(id);
        return concepto.codigo() + " · " + concepto.nombre();
    }

    private void prepararError(Model model, CuotaAlumnoForm form, Long id,
                               RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }

    private void prepararDesdeInscripcion(CuotaAlumnoForm form, Long inscripcionId) {
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, inscripcionId);
        var inscripcion = inscripcionService.obtener(inscripcionId);
        var ciclo = cicloService.obtener(inscripcion.cicloEscolarId());
        form.setInstitucionId(inscripcion.institucionId());
        form.setPlantelId(inscripcion.plantelId());
        form.setInscripcionId(inscripcion.id());
        form.setMoneda(institucionService.obtener(inscripcion.institucionId()).monedaPredeterminada());
        form.setFrecuencia(FrecuenciaCuota.UNICA);
        form.setFechaInicio(inscripcion.fechaInicio());
        form.setFechaFin(inscripcion.fechaFin() == null ? ciclo.fechaFin() : inscripcion.fechaFin());
        form.setFechaVencimientoUnico(inscripcion.fechaInicio());
        form.setDiaVencimiento(null);
        form.setGeneracionAutomatica(true);
        form.setGenerarCargoAhora(true);
        form.setRetornoInscripcionId(inscripcion.id());
    }

    private boolean tienePermiso(Authentication authentication, String permiso) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permiso));
    }
}
