package escuela.compras.service;

import escuela.compras.dto.FiltroCompra;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.*;

@Service @RequiredArgsConstructor
public class ExcelCompraService {
 private final CompraService service;
 public void exportar(FiltroCompra f,OutputStream out)throws IOException{var zona=service.zonaInstitucion(f.institucionId());try(SXSSFWorkbook w=new SXSSFWorkbook(100)){w.setCompressTempFiles(true);Sheet s=w.createSheet("Compras");String[] h={"Folio","Fecha","Proveedor","RFC","Plantel","Cuenta","Documento","Total","Estado","Movimiento"};Row r=s.createRow(0);CellStyle st=w.createCellStyle();Font ft=w.createFont();ft.setBold(true);st.setFont(ft);for(int i=0;i<h.length;i++){Cell c=r.createCell(i);c.setCellValue(h[i]);c.setCellStyle(st);}int n=1,p=0;while(true){var b=service.listar(f.conPagina(p,100));for(var x:b){Row z=s.createRow(n++);int c=0;z.createCell(c++).setCellValue(x.folio());z.createCell(c++).setCellValue(x.fecha().atZone(zona).toLocalDateTime().toString());z.createCell(c++).setCellValue(x.proveedor());z.createCell(c++).setCellValue(x.rfc()==null?"":x.rfc());z.createCell(c++).setCellValue(x.plantel());z.createCell(c++).setCellValue(x.cuenta());z.createCell(c++).setCellValue(x.referencia()==null?"":x.referencia());z.createCell(c++).setCellValue(x.total().doubleValue());z.createCell(c++).setCellValue(x.estado().getEtiqueta());z.createCell(c).setCellValue(x.movimientoId()==null?"":x.movimientoId().toString());}if(b.isLast())break;p++;}for(int i=0;i<h.length;i++)s.setColumnWidth(i,Math.min(50,Math.max(14,h[i].length()+8))*256);w.write(out);w.dispose();}}
}
