package escuela.calendario.service;

import escuela.calendario.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.io.*;

@Service @RequiredArgsConstructor
public class ExcelCalendarioEscolarService {
    private final CalendarioEscolarService service;
    public void exportar(FiltroCalendarioEscolar filtro,OutputStream out)throws IOException{try(SXSSFWorkbook w=new SXSSFWorkbook(200)){w.setCompressTempFiles(true);Sheet s=w.createSheet("Calendario escolar");String[]h={"Ciclo","Tipo","Título","Alcance","Inicio","Fin","Horario","Suspende clases","Estado","Descripción"};Row r=s.createRow(0);CellStyle cs=w.createCellStyle();cs.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);Font ft=w.createFont();ft.setBold(true);ft.setColor(IndexedColors.WHITE.getIndex());cs.setFont(ft);for(int i=0;i<h.length;i++){Cell c=r.createCell(i);c.setCellValue(h[i]);c.setCellStyle(cs);}int n=1,p=0;Page<CalendarioEscolarFila>b;do{b=service.bloque(filtro.conPagina(p++,100));for(var x:b){String horario=x.horaInicio()==null?"Todo el día":x.horaInicio()+" – "+x.horaFin();String[]v={x.ciclo(),x.tipo().getEtiqueta(),x.titulo(),x.alcance(),x.fechaInicio().toString(),x.fechaFin().toString(),horario,x.suspendeClases()?"Sí":"No",x.activo()?"Activo":"Inactivo",x.descripcion()};Row z=s.createRow(n++);for(int i=0;i<v.length;i++)z.createCell(i).setCellValue(v[i]==null?"":v[i]);}}while(p<b.getTotalPages());s.createFreezePane(0,1);for(int i=0;i<h.length;i++)s.setColumnWidth(i,(i==9?40:20)*256);w.write(out);w.dispose();}}
}
