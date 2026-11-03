package escuela.admin.controller;

import escuela.finanzas.dto.FiltroCheque;
import escuela.finanzas.service.*;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@Controller @RequiredArgsConstructor
@RequestMapping("/admin/cuentas-financieras/{cuentaId}/chequeras/cheques")
public class ConsultaChequeController {
 private final ConsultaChequeService service;
 private final ChequeraService chequeras;
 @GetMapping String listado(@PathVariable Long cuentaId,@ModelAttribute FiltroCheque filtro,Model m){
  m.addAttribute("cuenta",chequeras.cuenta(cuentaId));m.addAttribute("filtro",filtro.normalizado());m.addAttribute("ruta","/admin/cuentas-financieras/"+cuentaId+"/chequeras");m.addAttribute("rutaCheques","/admin/cuentas-financieras/"+cuentaId+"/chequeras/cheques");m.addAttribute("seccionCheques",true);
  var a=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();m.addAttribute("puedeEditarCuenta",a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("CUENTA_FINANCIERA_ADMINISTRAR")));
  try{m.addAttribute("resultado",service.listar(cuentaId,filtro));m.addAttribute("totalesCheques",service.totales(cuentaId,filtro));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}
  return "admin/cheques";
 }
 @GetMapping("/excel") void excel(@PathVariable Long cuentaId,@ModelAttribute FiltroCheque filtro,HttpServletResponse response)throws IOException{
  var f=filtro.normalizado();service.listar(cuentaId,f.pagina(0));response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=cheques.xlsx");response.setHeader("Cache-Control","no-store");
  try(var libro=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var hoja=libro.createSheet("Cheques");String[] cab={"Cheque","Chequera histórica","Fecha de emisión","Beneficiario","Importe","Moneda","Pago","Estado","Fecha de cancelación","Motivo de cancelación"};var h=hoja.createRow(0);for(int i=0;i<cab.length;i++){h.createCell(i).setCellValue(cab[i]);hoja.setColumnWidth(i,(i==3||i==9?55:28)*256);}int fila=1,pagina=0;org.springframework.data.domain.Page<ConsultaChequeService.Cheque> bloque;
   do{bloque=service.listar(cuentaId,f.pagina(pagina++));for(var c:bloque){var row=hoja.createRow(fila++);row.createCell(0).setCellValue(c.folio());row.createCell(1).setCellValue(c.chequera());row.createCell(2).setCellValue(c.fecha().toString());row.createCell(3).setCellValue(c.beneficiario());row.createCell(4).setCellValue(c.importe().doubleValue());row.createCell(5).setCellValue(c.moneda());row.createCell(6).setCellValue("NPI-"+c.pago());row.createCell(7).setCellValue(c.cancelado()?"Cancelado · No reutilizable":"Emitido");row.createCell(8).setCellValue(c.fechaCancelacion()==null?"":c.fechaCancelacion());row.createCell(9).setCellValue(c.motivo()==null?"":c.motivo());}}while(bloque.hasNext());hoja.createFreezePane(0,1);hoja.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,fila-1),0,9));libro.write(response.getOutputStream());}finally{libro.dispose();}}
 }
}
