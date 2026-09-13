package escuela.admin.controller;

import escuela.admin.dto.AlumnoForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.alumno.dto.response.AlumnoResponse;
import escuela.alumno.dto.response.FotografiaAlumnoResponse;
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

    private final AlumnoService service;
    private final FotografiaAlumnoService fotografiaService;
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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    String fotografiaDemasiadoGrande(HttpServletRequest request, Model model) {
        Matcher coincidencia = RUTA_FOTOGRAFIA.matcher(request.getRequestURI());
        if (!coincidencia.matches()) {
            throw new ReglaNegocioException("El archivo supera el tamaño permitido");
        }
        Long id = Long.valueOf(coincidencia.group(1));
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        prepararErrorFotografia(model, alumno, id,
                new ReglaNegocioException("La fotografía no puede superar 5 MB"));
        return "admin/alumno-form";
    }

    private void preparar(Model model, AlumnoForm form, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", alcance.filtrarInstituciones(institucionService.listar()));
        if (id != null) {
            List<FotografiaAlumnoResponse> historial = fotografiaService.historial(id);
            model.addAttribute("fotografiaActual", historial.stream()
                    .filter(FotografiaAlumnoResponse::actual).findFirst().orElse(null));
            model.addAttribute("historialFotografias", historial.stream()
                    .filter(fotografia -> !fotografia.actual()).toList());
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
