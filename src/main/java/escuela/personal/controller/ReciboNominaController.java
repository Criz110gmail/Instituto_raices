package escuela.personal.controller;

import escuela.admin.dto.*;
import escuela.admin.service.JasperReciboNominaService;
import escuela.common.exception.ReglaNegocioException;
import escuela.common.support.SalidaArchivoHttp;
import escuela.personal.dto.FiltroReciboNomina;
import escuela.personal.service.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/recibos-nomina")
public class ReciboNominaController {
    private final ReciboNominaService service;
    private final JasperReciboNominaService pdf;
    private final EmpleadoService empleados;
    private void base(FiltroReciboNomina filtro,Model m,HttpServletResponse response){
        empleados.autorizarSalarios(false);response.setHeader("Cache-Control","no-store");m.addAttribute("filtro",filtro.normalizado());
        var a=SecurityContextHolder.getContext().getAuthentication();var permisos=a.getAuthorities().stream().map(x->x.getAuthority()).collect(java.util.stream.Collectors.toSet());
        m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(permisos)).toList());m.addAttribute("moduloActual",ModuloCatalogo.PREPARACION_NOMINA);
    }
    @GetMapping String listado(@ModelAttribute FiltroReciboNomina filtro,Model m,HttpServletResponse response){
        base(filtro,m,response);try{m.addAttribute("resultado",service.listar(filtro));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",Page.empty());}
        return "admin/recibos-nomina";
    }
    @GetMapping("/{tipo:PAGO|CERO|HISTORICO}/{id:\\d+}") String detalle(@PathVariable String tipo,@PathVariable Long id,@ModelAttribute FiltroReciboNomina filtro,Model m,HttpServletResponse response){
        base(filtro,m,response);m.addAttribute("detalle",service.obtener(tipo,id));return "admin/recibo-nomina";
    }
    @GetMapping("/{tipo:PAGO|CERO|HISTORICO}/{id:\\d+}/pdf") void pdf(@PathVariable String tipo,@PathVariable Long id,HttpServletResponse response)throws IOException{
        empleados.autorizarSalarios(false);
        pdf.exportar(tipo,id,new SalidaArchivoHttp(response,"application/pdf","inline; filename=recibo-nomina-"+tipo+"-"+id+".pdf"));
    }
    @GetMapping("/empleados/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> empleados(@RequestParam(defaultValue="")String q){return ayuda(q,false);}
    @GetMapping("/periodos/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> periodos(@RequestParam(defaultValue="")String q){return ayuda(q,true);}
    private ResponseEntity<ResultadoAutocompletado> ayuda(String q,boolean periodo){var r=service.buscar(q,periodo);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(r.stream().limit(10).toList(),r.size()>10));}
    @GetMapping("/excel") void excel(@ModelAttribute FiltroReciboNomina filtro,HttpServletResponse response)throws IOException{
        var f=filtro.normalizado();var primero=service.listar(f.pagina(0));
        try(var libro=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){
            try{var hoja=libro.createSheet("Recibos internos");String[] cab={"Recibo","Nómina","Código laboral","Empleado","Periodo","Inicio","Fin","Estado","Importe del comprobante","Moneda","Registro"};
                var estilo=libro.createCellStyle();estilo.setDataFormat(libro.createDataFormat().getFormat("\"$\"#,##0.00"));
                var fuente=libro.createFont();fuente.setBold(true);fuente.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());var header=libro.createCellStyle();header.setFont(fuente);header.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());header.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
                var h=hoja.createRow(0);for(int k=0;k<cab.length;k++){h.createCell(k).setCellValue(cab[k]);h.getCell(k).setCellStyle(header);hoja.setColumnWidth(k,(k==3?46:28)*256);}int n=1,p=0;var pagina=primero;
                do{for(var r:pagina){var row=hoja.createRow(n++);String[] valores={r.folio(),r.nomina(),r.codigo(),r.nombre(),r.periodo(),r.inicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),r.fin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),r.estadoTexto()};for(int k=0;k<valores.length;k++)row.createCell(k).setCellValue(valores[k]);row.createCell(8).setCellValue(r.neto().doubleValue());row.getCell(8).setCellStyle(estilo);row.createCell(9).setCellValue(r.moneda());row.createCell(10).setCellValue(r.fecha());}if(!pagina.hasNext())break;pagina=service.listar(f.pagina(++p));}while(true);
                hoja.createFreezePane(0,1);hoja.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,n-1,0,10));
                libro.write(new SalidaArchivoHttp(response,"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet","attachment; filename=recibos-nomina.xlsx"));
            }finally{libro.dispose();}
        }
    }
}
