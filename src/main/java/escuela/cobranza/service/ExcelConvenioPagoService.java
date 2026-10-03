package escuela.cobranza.service;
import escuela.cobranza.entity.EstadoConvenioPago;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.*;
@Service @RequiredArgsConstructor
public class ExcelConvenioPagoService {
 private final ConvenioPagoService service;
 public void exportar(Long institucionId,EstadoConvenioPago estado,String texto,OutputStream out)throws IOException{try(SXSSFWorkbook w=new SXSSFWorkbook(100)){w.setCompressTempFiles(true);Sheet s=w.createSheet("Convenios");String[]h={"Folio","Tutor","Fecha acuerdo","Vencimiento","Saldo sustituido","Monto acordado","Condonado","Moneda","Situación"};Row r=s.createRow(0);CellStyle st=w.createCellStyle();Font ft=w.createFont();ft.setBold(true);st.setFont(ft);for(int i=0;i<h.length;i++){Cell c=r.createCell(i);c.setCellValue(h[i]);c.setCellStyle(st);}int n=1,p=0;while(true){var b=service.listar(institucionId,estado,texto,p,100);for(var x:b){Row z=s.createRow(n++);int c=0;z.createCell(c++).setCellValue(x.folio());z.createCell(c++).setCellValue(x.tutor());z.createCell(c++).setCellValue(x.fechaAcuerdo().toString());z.createCell(c++).setCellValue(x.fechaVencimiento().toString());z.createCell(c++).setCellValue(x.saldoOriginal().doubleValue());z.createCell(c++).setCellValue(x.montoAcordado().doubleValue());z.createCell(c++).setCellValue(x.condonado().doubleValue());z.createCell(c++).setCellValue(x.moneda());z.createCell(c).setCellValue(x.estado());}if(b.isLast())break;p++;}for(int i=0;i<h.length;i++)s.setColumnWidth(i,Math.min(42,Math.max(14,h[i].length()+8))*256);w.write(out);w.dispose();}}
}
