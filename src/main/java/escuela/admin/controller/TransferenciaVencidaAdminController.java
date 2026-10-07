package escuela.admin.controller;
import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.cobranza.service.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/cargos")
public class TransferenciaVencidaAdminController {
    private final CargoService cargos;
    private final TransferenciaVencidaService transferencias;
    private final AlcanceDatosService alcance;
    @GetMapping("/{id}/transferencia-vencida")
    String formulario(@PathVariable Long id,Model model) {
        alcance.validarRecurso(ModuloCatalogo.CARGOS,id);
        var cargo=cargos.obtener(id); var config=transferencias.consultar(id);
        var form=new TransferenciaVencidaForm(); form.setVersion(cargo.auditoria().version());
        form.setPermitir(config.excepcionAutorizada());
        preparar(id,form,model); return "admin/transferencia-vencida";
    }
    @PostMapping("/{id}/transferencia-vencida")
    String guardar(@PathVariable Long id,@Valid @ModelAttribute("form") TransferenciaVencidaForm form,
                   BindingResult errores,Model model,RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CARGOS,id);
        if(!errores.hasErrors()) {
            try {
                transferencias.configurar(id,form.getVersion(),form.isPermitir(),form.getMotivo());
                flash.addFlashAttribute("mensaje","Autorización de transferencia actualizada. No se modificaron el vencimiento ni el saldo.");
                return "redirect:/admin/cargos/"+id+"/editar";
            } catch(ReglaNegocioException|DataIntegrityViolationException|ObjectOptimisticLockingFailureException e) {
                model.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));
            }
        }
        preparar(id,form,model); return "admin/transferencia-vencida";
    }
    private void preparar(Long id,TransferenciaVencidaForm form,Model model) {
        model.addAttribute("cargo",cargos.obtener(id)); model.addAttribute("config",transferencias.consultar(id));
        model.addAttribute("form",form);
    }
}
