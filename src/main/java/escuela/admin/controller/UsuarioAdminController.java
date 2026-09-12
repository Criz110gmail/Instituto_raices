package escuela.admin.controller;

import escuela.admin.dto.AsignacionRolForm;
import escuela.admin.dto.UsuarioForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.dto.response.InvitacionEmitidaResponse;
import escuela.seguridad.dto.response.UsuarioResponse;
import escuela.seguridad.entity.AlcanceRol;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.service.AdministracionAccesoService;
import escuela.seguridad.service.InvitacionUsuarioService;
import escuela.seguridad.service.RolService;
import escuela.seguridad.service.UsuarioService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Duration;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioService service;
    private final RolService rolService;
    private final AdministracionAccesoService accesoService;
    private final InvitacionUsuarioService invitacionService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        prepararNuevo(model, new UsuarioForm());
        return "admin/usuario-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") UsuarioForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
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
        UsuarioResponse usuario = service.obtener(id);
        prepararEdicion(model, UsuarioForm.desde(usuario), usuario, new AsignacionRolForm());
        return "admin/usuario-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") UsuarioForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        UsuarioResponse usuario = service.obtener(id);
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

    @PostMapping("/{id}/roles")
    String asignarRol(@PathVariable Long id,
                      @Valid @ModelAttribute("asignacionForm") AsignacionRolForm asignacionForm,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        UsuarioResponse usuario = service.obtener(id);
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

    private void prepararNuevo(Model model, UsuarioForm form) {
        model.addAttribute("form", form);
        model.addAttribute("edicion", false);
        model.addAttribute("instituciones", institucionService.listar());
    }

    private void prepararEdicion(Model model, UsuarioForm form, UsuarioResponse usuario,
                                 AsignacionRolForm asignacionForm) {
        model.addAttribute("form", form);
        model.addAttribute("id", usuario.id());
        model.addAttribute("edicion", true);
        model.addAttribute("usuario", usuario);
        model.addAttribute("instituciones", institucionService.listar());
        model.addAttribute("roles", rolService.listarRoles());
        model.addAttribute("planteles", plantelService.listar());
        model.addAttribute("alcances", AlcanceRol.values());
        model.addAttribute("asignacionForm", asignacionForm);
        model.addAttribute("asignaciones", accesoService.listarAsignaciones(usuario.id()));
    }
}
