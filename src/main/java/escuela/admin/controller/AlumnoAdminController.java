package escuela.admin.controller;

import escuela.admin.dto.AlumnoForm;
import escuela.admin.dto.DocumentoAlumnoForm;
import escuela.admin.dto.FichaMedicaAlumnoForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.alumno.dto.response.AlumnoResponse;
import escuela.alumno.dto.response.FotografiaAlumnoResponse;
import escuela.alumno.dto.response.DocumentoAlumnoResponse;
import escuela.alumno.entity.TipoDocumentoAlumno;
import escuela.alumno.entity.TipoSanguineo;
import escuela.alumno.service.DocumentoAlumnoService;
import escuela.alumno.service.FichaMedicaAlumnoService;
import escuela.alumno.service.FotografiaAlumnoService;
import escuela.alumno.service.AlumnoService;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/alumnos")
public class AlumnoAdminController {

    private static final Pattern RUTA_FOTOGRAFIA =
            Pattern.compile("/admin/alumnos/(\\d+)/fotografia(?:/.*)?$");
    private static final Pattern RUTA_DOCUMENTO =
            Pattern.compile("/admin/alumnos/(\\d+)/documentos(?:/.*)?$");

    private final AlumnoService service;
    private final FotografiaAlumnoService fotografiaService;
    private final DocumentoAlumnoService documentoService;
    private final FichaMedicaAlumnoService fichaMedicaService;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new AlumnoForm(), null);
        return "admin/alumno-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") AlumnoForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (form.getInstitucionId() != null) {
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
        }
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/alumno-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Alumno registrado correctamente");
        return "redirect:/admin/catalogos/alumnos";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        preparar(model, AlumnoForm.desde(alumno), id);
        return "admin/alumno-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") AlumnoForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        if (form.getInstitucionId() != null) {
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
        }
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/alumno-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Alumno actualizado correctamente");
        return "redirect:/admin/catalogos/alumnos";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, AlumnoForm.desde(alumno), id, excepcion);
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Alumno desactivado correctamente");
        return "redirect:/admin/catalogos/alumnos";
    }

    @PostMapping("/{id}/fotografia")
    String asignarFotografia(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo,
                             Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        try {
            fotografiaService.asignar(id, archivo);
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararErrorFotografia(model, alumno, id, excepcion);
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Fotografía actualizada correctamente");
        return "redirect:/admin/alumnos/" + id + "/editar";
    }

    @PostMapping("/{id}/fotografia/retirar")
    String retirarFotografia(@PathVariable Long id, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        try {
            fotografiaService.retirar(id);
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararErrorFotografia(model, alumno, id, excepcion);
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "La fotografía se retiró del expediente actual");
        return "redirect:/admin/alumnos/" + id + "/editar";
    }

    @GetMapping("/{alumnoId}/fotografias/{fotografiaId}")
    ResponseEntity<Resource> descargarFotografia(@PathVariable Long alumnoId,
                                                 @PathVariable Long fotografiaId) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, alumnoId);
        ArchivoDescarga descarga = fotografiaService.descargar(alumnoId, fotografiaId);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType(descarga.tipoMime()))
                .contentLength(descarga.tamanoBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(descarga.recurso());
    }

    @PostMapping("/{id}/documentos")
    String agregarDocumento(@PathVariable Long id,
                            @Valid @ModelAttribute("documentoForm") DocumentoAlumnoForm form,
                            BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        if (errores.hasErrors()) {
            preparar(model, AlumnoForm.desde(alumno), id);
            return "admin/alumno-form";
        }
        try {
            documentoService.agregar(id, form.getTipo(), form.getDescripcion(),
                    form.getFechaDocumento(), form.getVigenteHasta(), form.getArchivo());
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            preparar(model, AlumnoForm.desde(alumno), id);
            model.addAttribute("errorDocumento", MensajeErrorFormulario.desde(excepcion));
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Documento agregado al expediente correctamente");
        return "redirect:/admin/alumnos/" + id + "/editar#expediente-documental";
    }

    @PostMapping("/{id}/documentos/{documentoId}/retirar")
    String retirarDocumento(@PathVariable Long id, @PathVariable Long documentoId,
                            @RequestParam Long version, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        try {
            documentoService.retirar(id, documentoId, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            preparar(model, AlumnoForm.desde(alumno), id);
            model.addAttribute("errorDocumento", MensajeErrorFormulario.desde(excepcion));
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Documento retirado del expediente vigente");
        return "redirect:/admin/alumnos/" + id + "/editar#expediente-documental";
    }

    @GetMapping("/{alumnoId}/documentos/{documentoId}")
    ResponseEntity<Resource> descargarDocumento(@PathVariable Long alumnoId,
                                                @PathVariable Long documentoId) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, alumnoId);
        ArchivoDescarga descarga = documentoService.descargar(alumnoId, documentoId);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType(descarga.tipoMime()))
                .contentLength(descarga.tamanoBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(descarga.recurso());
    }

    @PostMapping("/{id}/ficha-medica")
    String guardarFichaMedica(@PathVariable Long id,
                              @Valid @ModelAttribute("fichaMedicaForm") FichaMedicaAlumnoForm form,
                              BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        if (errores.hasErrors()) {
            preparar(model, AlumnoForm.desde(alumno), id);
            return "admin/alumno-form";
        }
        try {
            fichaMedicaService.guardar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            preparar(model, AlumnoForm.desde(alumno), id);
            model.addAttribute("errorFichaMedica", MensajeErrorFormulario.desde(excepcion));
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Ficha médica actualizada correctamente");
        return "redirect:/admin/alumnos/" + id + "/editar#ficha-medica";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    String fotografiaDemasiadoGrande(HttpServletRequest request, Model model) {
        Matcher fotografia = RUTA_FOTOGRAFIA.matcher(request.getRequestURI());
        Matcher documento = RUTA_DOCUMENTO.matcher(request.getRequestURI());
        boolean esFotografia = fotografia.matches();
        boolean esDocumento = documento.matches();
        if (!esFotografia && !esDocumento) {
            throw new ReglaNegocioException("El archivo supera el tamaño permitido");
        }
        Long id = Long.valueOf(esFotografia ? fotografia.group(1) : documento.group(1));
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        preparar(model, AlumnoForm.desde(alumno), id);
        model.addAttribute(esFotografia ? "errorFotografia" : "errorDocumento",
                esFotografia ? "La fotografía no puede superar 5 MB"
                        : "El documento no puede superar 10 MB");
        return "admin/alumno-form";
    }

    private void preparar(Model model, AlumnoForm form, Long id) {
        var instituciones = alcance.filtrarInstituciones(institucionService.listar());
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("institucionSeleccionada", instituciones.stream()
                .filter(institucion -> institucion.id().equals(form.getInstitucionId()))
                .map(institucion -> institucion.codigo() + " · " + institucion.nombre())
                .findFirst().orElse("Institución no disponible"));
        if (id != null) {
            List<FotografiaAlumnoResponse> historial = fotografiaService.historial(id);
            model.addAttribute("fotografiaActual", historial.stream()
                    .filter(FotografiaAlumnoResponse::actual).findFirst().orElse(null));
            model.addAttribute("historialFotografias", historial.stream()
                    .filter(fotografia -> !fotografia.actual()).toList());
            List<DocumentoAlumnoResponse> documentos = documentoService.historial(id);
            model.addAttribute("documentosVigentes", documentos.stream()
                    .filter(DocumentoAlumnoResponse::vigente).toList());
            model.addAttribute("historialDocumentos", documentos.stream()
                    .filter(documento -> !documento.vigente()).toList());
            if (!model.containsAttribute("documentoForm")) {
                model.addAttribute("documentoForm", new DocumentoAlumnoForm());
            }
            if (!model.containsAttribute("fichaMedicaForm")) {
                model.addAttribute("fichaMedicaForm",
                        FichaMedicaAlumnoForm.desde(fichaMedicaService.obtener(id)));
            }
            model.addAttribute("tiposDocumentoAlumno", TipoDocumentoAlumno.values());
            model.addAttribute("tiposSanguineos", TipoSanguineo.values());
        }
    }

    private void prepararError(Model model, AlumnoForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }

    private void prepararErrorFotografia(Model model, AlumnoResponse alumno, Long id,
                                         RuntimeException excepcion) {
        preparar(model, AlumnoForm.desde(alumno), id);
        model.addAttribute("errorFotografia", MensajeErrorFormulario.desde(excepcion));
    }
}
