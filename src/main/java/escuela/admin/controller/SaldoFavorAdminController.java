package escuela.admin.controller;
import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.finanzas.service.*;
import escuela.finanzas.entity.EstadoPago;
import escuela.common.exception.ReglaNegocioException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/pagos/{id}/saldo-favor")
public class SaldoFavorAdminController {
    private final SaldoFavorService saldo;
    private final PagoService pagos;
    @GetMapping String formulario(@PathVariable Long id,@RequestParam(defaultValue="")String operacion,
      @RequestParam(defaultValue="0")int pagina,Model m) {
        var f=new SaldoFavorForm();f.setVersion(pagos.obtener(id).auditoria().version());m.addAttribute("form",f);
        preparar(id,operacion,pagina,m);return "admin/saldo-favor-form";
    }
    @GetMapping("/cargos") @ResponseBody ResultadoAutocompletado buscar(@PathVariable Long id,@RequestParam(defaultValue="")String q) {return saldo.buscar(id,q);}
    @PostMapping String aplicar(@PathVariable Long id,@Valid @ModelAttribute("form")SaldoFavorForm f,BindingResult e,Model m,RedirectAttributes flash) {
        try {
            if(e.hasErrors())throw new ReglaNegocioException("Revisa los cargos, importes y motivo obligatorios");
            saldo.aplicar(id,f.request());flash.addFlashAttribute("mensaje","Saldo a favor aplicado. No se creó otro ingreso bancario.");
            return "redirect:/admin/pagos/"+id+"/saldo-favor";
        }catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException|org.springframework.orm.ObjectOptimisticLockingFailureException ex){
            preparar(id,"",0,m);m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));return "admin/saldo-favor-form";
        }
    }
    @PostMapping("/{aplicacionId}/revertir") String revertir(@PathVariable Long id,@PathVariable Long aplicacionId,
      @RequestParam Long version,@RequestParam String motivo,RedirectAttributes flash) {
        try{saldo.revertir(id,aplicacionId,version,motivo);flash.addFlashAttribute("mensaje","Aplicación revertida: el importe vuelve al saldo a favor sin mover el banco.");}
        catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException|org.springframework.orm.ObjectOptimisticLockingFailureException ex){flash.addFlashAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));}
        return "redirect:/admin/pagos/"+id+"/saldo-favor";
    }
    @GetMapping("/excel") void excel(@PathVariable Long id,@RequestParam(defaultValue="")String operacion,HttpServletResponse response)throws IOException {
        saldo.historial(id,operacion,0,1); // Validar alcance/filtro antes de enviar cabeceras.
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=saldo-favor-"+id+".xlsx");
        try(var libro=new XSSFWorkbook()) {
            var hoja=libro.createSheet("Aplicaciones de saldo");var cab=hoja.createRow(0);
            String[] nombres={"Alumno","Concepto","Operación","Importe","Moneda","Fecha","Motivo","Autorizó","Vigente"};
            for(int i=0;i<nombres.length;i++)cab.createCell(i).setCellValue(nombres[i]);int fila=1,pagina=0;
            org.springframework.data.domain.Page<SaldoFavorService.DatoAplicacion> bloque;
            do{bloque=saldo.historial(id,operacion,pagina++,100);for(var d:bloque){var r=hoja.createRow(fila++);
                r.createCell(0).setCellValue(d.alumno());r.createCell(1).setCellValue(d.concepto());r.createCell(2).setCellValue(d.operacion().equals("APLICAR")?"Aplicación":"Reversión");
                r.createCell(3).setCellValue(d.monto().doubleValue());r.createCell(4).setCellValue(d.moneda());r.createCell(5).setCellValue(d.fecha());
                r.createCell(6).setCellValue(d.motivo());r.createCell(7).setCellValue(d.autorizador());r.createCell(8).setCellValue(d.reversible()?"Sí":"No");
            }}while(bloque.hasNext());for(int i=0;i<nombres.length;i++)hoja.setColumnWidth(i,(i==6?60:25)*256);libro.write(response.getOutputStream());
        }
    }
    private void preparar(Long id,String operacion,int pagina,Model m) {
        var historial=saldo.historial(id,operacion,pagina,10);var p=pagos.obtener(id);
        m.addAttribute("pago",p);m.addAttribute("historial",historial);m.addAttribute("operacion",operacion);
        var auth=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        m.addAttribute("puedeAplicar",p.estado()==EstadoPago.VALIDADO&&p.montoDisponible().signum()>0&&auth!=null&&auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("PAGO_VALIDAR")));
        m.addAttribute("puedeRevertir",auth!=null&&auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("PAGO_CANCELAR")));
    }
}
