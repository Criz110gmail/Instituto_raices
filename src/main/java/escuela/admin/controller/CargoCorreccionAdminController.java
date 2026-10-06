package escuela.admin.controller;
import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.cobranza.service.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/cargos")
public class CargoCorreccionAdminController {
    private final CargoService cargos;
    private final CargoCorreccionService correcciones;
    private final AlcanceDatosService alcance;
    @GetMapping("/{id}/corregir")
    String formulario(@PathVariable Long id,Model model) {
        alcance.validarRecurso(ModuloCatalogo.CARGOS,id);
        var cargo=cargos.obtener(id); var form=new CargoCorreccionForm();
        form.setVersion(cargo.auditoria().version()); form.setFechaVencimiento(correcciones.fechaSugerida(id));
        preparar(model,id,form); return "admin/cargo-corregir";
    }
    @PostMapping("/{id}/corregir")
    String corregir(@PathVariable Long id,@Valid @ModelAttribute("form") CargoCorreccionForm form,
                    BindingResult errores,Model model,RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CARGOS,id);
        if(!errores.hasErrors()) {
            try {
                var nuevo=correcciones.reemplazar(id,form.getVersion(),form.getFechaVencimiento(),form.getMotivo());
                flash.addFlashAttribute("mensaje","Cargo #"+id+" reemplazado por #"+nuevo.id()+". Revisa su beca y saldo antes de continuar.");
                return "redirect:/admin/cargos/"+nuevo.id()+"/editar";
            } catch(ReglaNegocioException|DataIntegrityViolationException|ObjectOptimisticLockingFailureException e) {
                model.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));
            }
        }
        preparar(model,id,form); return "admin/cargo-corregir";
    }
    private void preparar(Model model,Long id,CargoCorreccionForm form) {
        model.addAttribute("cargo",cargos.obtener(id)); model.addAttribute("enlaces",correcciones.enlaces(id));
        model.addAttribute("form",form);
    }
}
