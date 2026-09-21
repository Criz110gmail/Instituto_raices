package escuela.admin.service;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;

@Service @RequiredArgsConstructor
public class ExcelAvisoService {
    private final AvisoConsultaService consulta;
    public void exportar(FiltroAviso filtro, OutputStream salida)throws IOException{try(SXSSFWorkbook l=new SXSSFWorkbook(200)){l.setCompressTempFiles(true);Sheet h=l.createSheet("Avisos");
        String[] cs={"Folio","Aviso","Institución","Alcance","Estado","Publicado","Vence"};Row c=h.createRow(0);for(int i=0;i<cs.length;i++)c.createCell(i).setCellValue(cs[i]);
        int fila=1,p=0;boolean mas;do{var b=consulta.consultar(filtro.conPagina(p++,100));for(AvisoFila a:b){Row r=h.createRow(fila++);r.createCell(0).setCellValue(a.id());r.createCell(1).setCellValue(a.titulo());r.createCell(2).setCellValue(a.institucion());r.createCell(3).setCellValue(a.plantel());r.createCell(4).setCellValue(a.estado());r.createCell(5).setCellValue(a.publicado());r.createCell(6).setCellValue(a.expira());}mas=p<b.getTotalPages();}while(mas);h.createFreezePane(0,1);for(int i=0;i<cs.length;i++)h.setColumnWidth(i,(i==1?38:22)*256);l.write(salida);l.dispose();}}
}
