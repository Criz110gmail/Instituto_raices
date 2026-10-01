package escuela.compras.service;

import escuela.compras.dto.FiltroProveedor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.*;

@Service @RequiredArgsConstructor
public class ExcelProveedorService {
 private final ProveedorService service;
 public void exportar(FiltroProveedor f,OutputStream out)throws IOException{try(SXSSFWorkbook w=new SXSSFWorkbook(100)){w.setCompressTempFiles(true);Sheet s=w.createSheet("Proveedores");String[] h={"Razón social","Nombre comercial","RFC","Contacto","Teléfono","Correo","Estado"};Row r=s.createRow(0);CellStyle st=w.createCellStyle();Font ft=w.createFont();ft.setBold(true);st.setFont(ft);for(int i=0;i<h.length;i++){Cell c=r.createCell(i);c.setCellValue(h[i]);c.setCellStyle(st);}int n=1,p=0;while(true){var b=service.listar(f.conPagina(p,100));for(var x:b){Row z=s.createRow(n++);int c=0;z.createCell(c++).setCellValue(x.razonSocial());z.createCell(c++).setCellValue(x.nombreComercial()==null?"":x.nombreComercial());z.createCell(c++).setCellValue(x.rfc()==null?"":x.rfc());z.createCell(c++).setCellValue(x.contacto()==null?"":x.contacto());z.createCell(c++).setCellValue(x.telefono()==null?"":x.telefono());z.createCell(c++).setCellValue(x.correo()==null?"":x.correo());z.createCell(c).setCellValue(x.activo()?"Activo":"Inactivo");}if(b.isLast())break;p++;}for(int i=0;i<h.length;i++)s.setColumnWidth(i,Math.min(50,Math.max(14,h[i].length()+8))*256);w.write(out);w.dispose();}}
}
