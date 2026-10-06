package escuela.admin.controller;

import escuela.academico.service.PeriodoAcademicoService;
import escuela.admin.dto.CargoForm;
import escuela.admin.dto.GeneracionCargosForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.cobranza.service.CargoService;
import escuela.cobranza.service.ConceptoCobroService;
import escuela.cobranza.service.CuotaAlumnoService;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.service.InscripcionService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cargos")
public class CargoAdminController {

    private final CargoService service;
    private final CuotaAlumnoService cuotaAlumnoService;
    private final ConceptoCobroService conceptoService;
    private final InscripcionService inscripcionService;
    private final PeriodoAcademicoService periodoService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;
    private final AjusteCargoAdminController ajusteController;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        prepararManual(model, new CargoForm());
        return "admin/cargo-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") CargoForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        validarAlcance(form);
        validarFechaRegistro(form, errores);
        validarPeriodoCaptura(form, errores);
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            prepararManual(model, form);
            return "admin/cargo-form";
        }
        try {
            service.crearManual(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararManual(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/cargo-form";
        }
        flash.addFlashAttribute("mensaje", "Cargo al alumno registrado correctamente");
        return "redirect:/admin/catalogos/cargos";
    }

    @GetMapping("/{id}/editar")
    String detalle(@PathVariable Long id, Model model, Authentication authentication) {
        alcance.validarRecurso(ModuloCatalogo.CARGOS, id);
        ajusteController.prepararCargo(model, id, new escuela.admin.dto.AjusteCargoForm());
        model.addAttribute("puedeValidarPago", authentication.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("PAGO_VALIDAR") || a.getAuthority().equals("PAGO_LEER")
                        || a.getAuthority().equals("PAGO_REGISTRAR")));
        model.addAttribute("puedeRegistrarPago", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PAGO_REGISTRAR")));
        return "admin/cargo-detalle";
    }

    @PostMapping("/{id}/cancelar")
    String cancelar(@PathVariable Long id, @RequestParam Long version,
                    @RequestParam String motivo, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CARGOS, id);
        try {
            service.cancelar(id, version, motivo);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            ajusteController.prepararCargo(model, id, new escuela.admin.dto.AjusteCargoForm());
            model.addAttribute("motivoCapturado", motivo);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/cargo-detalle";
        }
        flash.addFlashAttribute("mensaje", "Cargo al alumno cancelado; el historial permanece disponible");
        return "redirect:/admin/catalogos/cargos";
    }

    @GetMapping("/generar")
    String generador(Model model) {
        prepararGenerador(model, new GeneracionCargosForm());
        return "admin/cargo-generar";
    }

    @GetMapping("/generar/vista-previa")
    String vistaPreviaGeneracion(@Valid @ModelAttribute("form") GeneracionCargosForm form,
                                 BindingResult errores,
                                 @RequestParam(defaultValue = "0") int pagina,
                                 @RequestParam(defaultValue = "25") int tamanio,
                                 @RequestParam(defaultValue = "0") int paginaExcluidas,
                                 @RequestParam(defaultValue = "false") boolean mostrarExcluidas,
                                 Model model) {
        validarGeneracion(form, errores);
        prepararGenerador(model, form);
        if (!errores.hasErrors()) {
            try {model.addAttribute("vistaPrevia", service.previsualizar(form.request(), pagina, tamanio));}
            catch(ReglaNegocioException e){model.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));return "admin/cargo-generar";}
            model.addAttribute("cuotasExcluidas",service.diagnosticar(form.request(),paginaExcluidas));
            model.addAttribute("mostrarExcluidas",mostrarExcluidas);
            model.addAttribute("tamanio", Math.min(Math.max(tamanio, 10), 100));
        }
        return "admin/cargo-generar";
    }

    @GetMapping("/generar/excluidas/excel")
    void excelExcluidas(@Valid @ModelAttribute("form") GeneracionCargosForm form,BindingResult errores,
                       jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        validarGeneracion(form,errores);
        if(errores.hasErrors()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Revisa institución, plantel y fecha de corte");
        var bloque=service.diagnosticar(form.request(),0);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=cuotas-no-incluidas.xlsx");response.setHeader("Cache-Control","no-store");
        try(var libro=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)) {
            var hoja=libro.createSheet("Cuotas no incluidas");var cabecera=hoja.createRow(0);
            String[] titulos={"Cuota","Alumno","Concepto","Motivo","Cargo relacionado"};
            for(int i=0;i<titulos.length;i++){cabecera.createCell(i).setCellValue(titulos[i]);hoja.setColumnWidth(i,i==3?18000:8000);}
            hoja.createFreezePane(0,1);int fila=1,pagina=0;
            while(true) {
                for(var q:bloque){var row=hoja.createRow(fila++);row.createCell(0).setCellValue(q.cuotaId());row.createCell(1).setCellValue(q.alumno());row.createCell(2).setCellValue(q.concepto());row.createCell(3).setCellValue(q.motivo());if(q.cargoId()!=null)row.createCell(4).setCellValue(q.cargoId());}
                if(!bloque.hasNext())break;bloque=service.diagnosticar(form.request(),++pagina);
            }
            libro.write(response.getOutputStream());
        }
    }

    @PostMapping("/generar")
    String generar(@Valid @ModelAttribute("form") GeneracionCargosForm form,
                   BindingResult errores,
                   @RequestParam(defaultValue = "false") boolean confirmacion,
                   Model model, RedirectAttributes flash) {
        validarGeneracion(form, errores);
        if (!confirmacion) {
            errores.reject("cargo.generacion.confirmacion",
                    "Primero visualiza las cuotas por aplicar y confirma el resultado mostrado");
        }
        if(form.getSeleccionId()==null)errores.reject("cargo.seleccion","Primero visualiza y revisa la selección antes de confirmar");
        if (errores.hasErrors()) {
            prepararGenerador(model, form);
            return "admin/cargo-generar";
        }
        try {
            var resultado = service.generar(form.request());
            flash.addFlashAttribute("mensaje", "Generación terminada: "
                    + resultado.cargosGenerados() + " adeudos de alumnos nuevos, "
                    + resultado.cargosYaExistentes() + " ya existían; "
                    + resultado.cuotasRevisadas() + " cuotas revisadas");
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararGenerador(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/cargo-generar";
        }
        return "redirect:/admin/catalogos/cargos";
    }

    @PostMapping("/cuotas/{cuotaId}/generar-unico")
    String generarCargoUnico(@PathVariable Long cuotaId,
                             @RequestParam Long inscripcionId,
                             RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CUOTAS_ALUMNO, cuotaId);
        alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, inscripcionId);
        try {
            var cuota = cuotaAlumnoService.obtener(cuotaId);
            if (!cuota.inscripcionId().equals(inscripcionId)) {
                throw new ReglaNegocioException("La cuota no pertenece a esta inscripción");
            }
            var cargo = service.generarCargoUnico(cuotaId);
            flash.addFlashAttribute("mensaje", "Cargo disponible: " + cargo.descripcion());
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            flash.addFlashAttribute("errorCobranza", MensajeErrorFormulario.desde(excepcion));
        }
        return "redirect:/admin/inscripciones/" + inscripcionId + "/editar#cobranza-inscripcion";
    }

    private void prepararManual(Model model, CargoForm form) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        completarPredeterminados(form, instituciones, planteles);
        LocalDate fechaRegistroPredeterminada = fechaActual(form, instituciones);
        if (!form.isModificarFechaRegistro()) {
            form.setFechaEmision(fechaRegistroPredeterminada);
            form.setMotivoFechaRegistroDiferente(null);
        }
        model.addAttribute("form", form);
        model.addAttribute("fechaRegistroPredeterminada", fechaRegistroPredeterminada);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
        model.addAttribute("inscripcionSeleccionada", etiquetaInscripcion(form.getInscripcionId()));
        model.addAttribute("conceptoSeleccionado", etiquetaConcepto(form.getConceptoCobroId()));
        model.addAttribute("periodoSeleccionado", etiquetaPeriodo(form.getPeriodoAcademicoId()));
    }

    private void prepararGenerador(Model model, GeneracionCargosForm form) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
    }

    private void completarPredeterminados(CargoForm form, List<InstitucionResponse> instituciones,
                                           List<PlantelResponse> planteles) {
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
    }

    private void validarFechaRegistro(CargoForm form, BindingResult errores) {
        LocalDate hoy = form.getInstitucionId() == null ? LocalDate.now()
                : LocalDate.now(ZoneId.of(institucionService.obtener(form.getInstitucionId()).zonaHoraria()));
        if (!form.isModificarFechaRegistro()) {
            form.setFechaEmision(hoy);
            form.setMotivoFechaRegistroDiferente(null);
            return;
        }
        if (form.getFechaEmision() == null) {
            errores.rejectValue("fechaEmision", "cargo.fechaRegistro.requerida",
                    "Selecciona la fecha histórica en que debe quedar registrado el cargo");
        } else if (form.getFechaEmision().isAfter(hoy)) {
            errores.rejectValue("fechaEmision", "cargo.fechaRegistro.futura",
                    "La fecha de registro no puede estar en el futuro");
        }
        if (form.getMotivoFechaRegistroDiferente() == null
                || form.getMotivoFechaRegistroDiferente().isBlank()) {
            errores.rejectValue("motivoFechaRegistroDiferente", "cargo.fechaRegistro.motivo",
                    "Explica por qué necesitas registrar el cargo con otra fecha");
        }
    }

    private LocalDate fechaActual(CargoForm form, List<InstitucionResponse> instituciones) {
        if (form.getInstitucionId() == null) return LocalDate.now();
        return instituciones.stream().filter(i -> i.id().equals(form.getInstitucionId())).findFirst()
                .map(i -> LocalDate.now(ZoneId.of(i.zonaHoraria())))
                .orElseGet(LocalDate::now);
    }

    private void validarAlcance(CargoForm form) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getPlantelId() != null) alcance.validarPlantel(form.getPlantelId());
        if (form.getInscripcionId() != null) alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES,
                form.getInscripcionId());
        if (form.getConceptoCobroId() != null) alcance.validarRecurso(ModuloCatalogo.CONCEPTOS_COBRO,
                form.getConceptoCobroId());
        if (form.getPeriodoAcademicoId() != null) alcance.validarRecurso(ModuloCatalogo.PERIODOS,
                form.getPeriodoAcademicoId());
    }

    private void validarRelaciones(CargoForm form, BindingResult errores) {
        if (form.getInstitucionId() == null || form.getPlantelId() == null) return;
        if (form.getInscripcionId() != null) {
            var inscripcion = inscripcionService.obtener(form.getInscripcionId());
            if (!inscripcion.institucionId().equals(form.getInstitucionId())) {
                errores.rejectValue("inscripcionId", "cargo.inscripcion.institucion",
                        "La inscripción no pertenece a la institución indicada");
            }
            if (!inscripcion.plantelId().equals(form.getPlantelId())) {
                errores.rejectValue("inscripcionId", "cargo.inscripcion.plantel",
                        "La inscripción no pertenece al plantel indicado");
            }
        }
        if (form.getConceptoCobroId() != null
                && !conceptoService.obtener(form.getConceptoCobroId()).institucionId()
                .equals(form.getInstitucionId())) {
            errores.rejectValue("conceptoCobroId", "cargo.concepto.institucion",
                    "El concepto no pertenece a la institución indicada");
        }
    }

    private void validarPeriodoCaptura(CargoForm form, BindingResult errores) {
        if (form.getModoPeriodo() == null) return;
        switch (form.getModoPeriodo()) {
            case MES_COMPLETO -> {
                if (form.getMesPeriodo() == null) {
                    errores.rejectValue("mesPeriodo", "cargo.periodo.mes",
                            "Selecciona el mes al que corresponde el cargo");
                }
            }
            case FECHA_ESPECIFICA -> {
                if (form.getFechaEspecifica() == null) {
                    errores.rejectValue("fechaEspecifica", "cargo.periodo.fecha",
                            "Selecciona la fecha a la que corresponde el cargo");
                }
            }
            case RANGO_PERSONALIZADO -> {
                if (form.getPeriodoCobroInicio() == null) {
                    errores.rejectValue("periodoCobroInicio", "cargo.periodo.inicio",
                            "Indica cuándo comienza el periodo que estás cobrando");
                }
                if (form.getPeriodoCobroFin() == null) {
                    errores.rejectValue("periodoCobroFin", "cargo.periodo.fin",
                            "Indica cuándo termina el periodo que estás cobrando");
                } else if (form.getPeriodoCobroInicio() != null
                        && form.getPeriodoCobroFin().isBefore(form.getPeriodoCobroInicio())) {
                    errores.rejectValue("periodoCobroFin", "cargo.periodo.orden",
                            "La fecha final no puede ser anterior a la fecha inicial");
                }
            }
        }
    }

    private void validarGeneracion(GeneracionCargosForm form, BindingResult errores) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getPlantelId() == null) {
            if (form.getInstitucionId() != null) {
                alcance.validarAdministracionInstitucional(form.getInstitucionId());
            }
            return;
        }
        alcance.validarPlantel(form.getPlantelId());
        if (form.getInstitucionId() != null
                && !plantelService.obtener(form.getPlantelId()).institucionId()
                .equals(form.getInstitucionId())) {
            errores.rejectValue("plantelId", "cargo.generacion.plantel",
                    "El plantel no pertenece a la institución indicada");
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

    private String etiquetaPeriodo(Long id) {
        if (id == null) return "";
        var periodo = periodoService.obtener(id);
        return periodo.codigo() + " · " + periodo.nombre();
    }
}
