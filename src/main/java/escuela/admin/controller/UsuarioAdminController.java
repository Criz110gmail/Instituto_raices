package escuela.admin.controller;

import escuela.admin.dto.AsignacionRolForm;
import escuela.admin.dto.UsuarioForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.dto.response.InvitacionEmitidaResponse;
import escuela.seguridad.dto.response.RecuperacionPasswordEmitidaResponse;
import escuela.seguridad.dto.response.UsuarioResponse;
import escuela.seguridad.entity.AlcanceRol;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.service.AdministracionAccesoService;
import escuela.seguridad.service.InvitacionUsuarioService;
import escuela.seguridad.service.FotografiaUsuarioService;
import escuela.seguridad.service.RecuperacionPasswordService;
import escuela.seguridad.service.RolService;
import escuela.seguridad.service.UsuarioService;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.admin.dto.ModuloCatalogo;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private static final Pattern RUTA_FOTOGRAFIA =
            Pattern.compile("/admin/usuarios/(\\d+)/fotografia(?:/.*)?$");

    private final UsuarioService service;
    private final FotografiaUsuarioService fotografiaService;
    private final RolService rolService;
    private final AdministracionAccesoService accesoService;
    private final InvitacionUsuarioService invitacionService;
    private final RecuperacionPasswordService recuperacionPasswordService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        prepararNuevo(model, new UsuarioForm());
        return "admin/usuario-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") UsuarioForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (form.getInstitucionId() != null) alcance.validarAdministracionInstitucional(form.getInstitucionId());
        if (errores.hasErrors()) {
            prepararNuevo(model, form);
            return "admin/usuario-form";
        }
        try {
            UsuarioResponse usuario = service.crearInvitado(form.request());
            flash.addFlashAttribute("mensaje", "Usuario invitado creado correctamente");
            return "redirect:/admin/usuarios/" + usuario.id() + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararNuevo(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/usuario-form";
        }
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.USUARIOS, id);
        UsuarioResponse usuario = service.obtener(id);
        alcance.validarAdministracionInstitucional(usuario.institucionId());
        prepararEdicion(model, UsuarioForm.desde(usuario), usuario, new AsignacionRolForm());
        return "admin/usuario-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") UsuarioForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.USUARIOS, id);
        UsuarioResponse usuario = service.obtener(id);
        alcance.validarAdministracionInstitucional(usuario.institucionId());
        if (errores.hasErrors()) {
            prepararEdicion(model, form, usuario, new AsignacionRolForm());
            return "admin/usuario-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararEdicion(model, form, usuario, new AsignacionRolForm());
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/usuario-form";
        }
        flash.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        return "redirect:/admin/usuarios/" + id + "/editar";
    }

    @PostMapping("/{id}/estado")
    String cambiarEstado(@PathVariable Long id, @RequestParam Long version,
                         @RequestParam EstadoUsuario estado, Model model,
                         RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.USUARIOS, id);
        alcance.validarAdministracionInstitucional(service.obtener(id).institucionId());
        try {
            service.cambiarEstado(id, version, estado);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            UsuarioResponse usuario = service.obtener(id);
            prepararEdicion(model, UsuarioForm.desde(usuario), usuario, new AsignacionRolForm());
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/usuario-form";
        }
        flash.addFlashAttribute("mensaje", "Estado del usuario actualizado correctamente");
        return "redirect:/admin/usuarios/" + id + "/editar";
    }

    @PostMapping("/{id}/fotografia")
    String asignarFotografia(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo,
                             Model model, RedirectAttributes flash) {
        UsuarioResponse usuario = usuarioAdministrable(id);
        try {
            fotografiaService.asignar(id, archivo);
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararErrorFotografia(model, usuario, excepcion);
            return "admin/usuario-form";
        }
        flash.addFlashAttribute("mensaje", "Fotografía del usuario actualizada correctamente");
        return "redirect:/admin/usuarios/" + id + "/editar";
    }

    @PostMapping("/{id}/fotografia/retirar")
    String retirarFotografia(@PathVariable Long id, Model model, RedirectAttributes flash) {
        UsuarioResponse usuario = usuarioAdministrable(id);
        try {
            fotografiaService.retirar(id);
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararErrorFotografia(model, usuario, excepcion);
            return "admin/usuario-form";
        }
        flash.addFlashAttribute("mensaje", "Se restauró el avatar genérico del usuario");
        return "redirect:/admin/usuarios/" + id + "/editar";
    }

    @GetMapping("/{id}/fotografia")
    ResponseEntity<Resource> descargarFotografia(@PathVariable Long id) {
        usuarioAdministrable(id);
        return fotografiaService.descargarActual(id)
                .map(this::respuestaFotografia)
                .orElseGet(this::avatarGenerico);
    }

    @PostMapping("/{id}/roles")
    String asignarRol(@PathVariable Long id,
                      @Valid @ModelAttribute("asignacionForm") AsignacionRolForm asignacionForm,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.USUARIOS, id);
        UsuarioResponse usuario = service.obtener(id);
        alcance.validarAdministracionInstitucional(usuario.institucionId());
        if (asignacionForm.getPlantelId() != null) alcance.validarPlantel(asignacionForm.getPlantelId());
        if (errores.hasErrors()) {
            prepararEdicion(model, UsuarioForm.desde(usuario), usuario, asignacionForm);
            return "admin/usuario-form";
        }
        try {
            accesoService.asignarRol(asignacionForm.request(id));
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararEdicion(model, UsuarioForm.desde(usuario), usuario, asignacionForm);
            model.addAttribute("errorAsignacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/usuario-form";
        }
        flash.addFlashAttribute("mensaje", "Rol asignado correctamente");
        return "redirect:/admin/usuarios/" + id + "/editar";
    }

    @PostMapping("/{usuarioId}/roles/{asignacionId}/desactivar")
    String desactivarRol(@PathVariable Long usuarioId, @PathVariable Long asignacionId,
                         @RequestParam Long version, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.USUARIOS, usuarioId);
        alcance.validarAdministracionInstitucional(service.obtener(usuarioId).institucionId());
        try {
            accesoService.desactivarAsignacion(usuarioId, asignacionId, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            UsuarioResponse usuario = service.obtener(usuarioId);
            prepararEdicion(model, UsuarioForm.desde(usuario), usuario, new AsignacionRolForm());
            model.addAttribute("errorAsignacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/usuario-form";
        }
        flash.addFlashAttribute("mensaje", "Asignación desactivada correctamente");
        return "redirect:/admin/usuarios/" + usuarioId + "/editar";
    }

    @PostMapping("/{id}/invitacion")
    String emitirInvitacion(@PathVariable Long id, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.USUARIOS, id);
        alcance.validarAdministracionInstitucional(service.obtener(id).institucionId());
        try {
            InvitacionEmitidaResponse invitacion = invitacionService.emitir(id, Duration.ofHours(48));
            String enlace = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/activar-cuenta").queryParam("token", invitacion.token())
                    .build().toUriString();
            flash.addFlashAttribute("invitacionEnlace", enlace);
            flash.addFlashAttribute("invitacionExpira", invitacion.expiraEn());
            flash.addFlashAttribute("mensaje", "Invitación generada. Copia el enlace antes de salir de la pantalla");
            return "redirect:/admin/usuarios/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            UsuarioResponse usuario = service.obtener(id);
            prepararEdicion(model, UsuarioForm.desde(usuario), usuario, new AsignacionRolForm());
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/usuario-form";
        }
    }

    @PostMapping("/{id}/recuperacion-password")
    String emitirRecuperacionPassword(@PathVariable Long id, Model model,
                                      RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.USUARIOS, id);
        alcance.validarAdministracionInstitucional(service.obtener(id).institucionId());
        try {
            RecuperacionPasswordEmitidaResponse recuperacion =
                    recuperacionPasswordService.emitir(id, Duration.ofMinutes(30));
            String enlace = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/restablecer-password").queryParam("token", recuperacion.token())
                    .build().toUriString();
            flash.addFlashAttribute("recuperacionEnlace", enlace);
            flash.addFlashAttribute("recuperacionExpira", recuperacion.expiraEn());
            flash.addFlashAttribute("mensaje", "Enlace de recuperación generado. Cópialo antes de salir de la pantalla");
            return "redirect:/admin/usuarios/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            UsuarioResponse usuario = service.obtener(id);
            prepararEdicion(model, UsuarioForm.desde(usuario), usuario, new AsignacionRolForm());
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/usuario-form";
        }
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    String fotografiaDemasiadoGrande(HttpServletRequest request, Model model) {
        Matcher coincidencia = RUTA_FOTOGRAFIA.matcher(request.getRequestURI());
        if (!coincidencia.matches()) {
            throw new ReglaNegocioException("El archivo supera el tamaño permitido");
        }
        UsuarioResponse usuario = usuarioAdministrable(Long.valueOf(coincidencia.group(1)));
        prepararErrorFotografia(model, usuario,
                new ReglaNegocioException("La fotografía no puede superar 5 MB"));
        return "admin/usuario-form";
    }

    private void prepararNuevo(Model model, UsuarioForm form) {
        model.addAttribute("form", form);
        model.addAttribute("edicion", false);
        model.addAttribute("instituciones", alcance.filtrarInstituciones(institucionService.listar()));
    }

    private void prepararEdicion(Model model, UsuarioForm form, UsuarioResponse usuario,
                                 AsignacionRolForm asignacionForm) {
        model.addAttribute("form", form);
        model.addAttribute("id", usuario.id());
        model.addAttribute("edicion", true);
        model.addAttribute("usuario", usuario);
        model.addAttribute("instituciones", alcance.filtrarInstituciones(institucionService.listar()));
        model.addAttribute("roles", alcance.filtrarRoles(rolService.listarRoles()));
        model.addAttribute("planteles", alcance.filtrarPlanteles(plantelService.listar()));
        model.addAttribute("alcances", AlcanceRol.values());
        model.addAttribute("asignacionForm", asignacionForm);
        model.addAttribute("asignaciones", accesoService.listarAsignaciones(usuario.id()));
    }

    private UsuarioResponse usuarioAdministrable(Long id) {
        alcance.validarRecurso(ModuloCatalogo.USUARIOS, id);
        UsuarioResponse usuario = service.obtener(id);
        alcance.validarAdministracionInstitucional(usuario.institucionId());
        return usuario;
    }

    private void prepararErrorFotografia(Model model, UsuarioResponse usuario,
                                         RuntimeException excepcion) {
        prepararEdicion(model, UsuarioForm.desde(usuario), usuario, new AsignacionRolForm());
        model.addAttribute("errorFotografia", MensajeErrorFormulario.desde(excepcion));
    }

    private ResponseEntity<Resource> respuestaFotografia(ArchivoDescarga descarga) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType(descarga.tipoMime()))
                .contentLength(descarga.tamanoBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(descarga.recurso());
    }

    private ResponseEntity<Resource> avatarGenerico() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(new ClassPathResource("static/images/avatar-generico.svg"));
    }
}
