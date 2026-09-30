package escuela.horario.service;

import escuela.horario.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.io.*;

@Service @RequiredArgsConstructor
public class ExcelHorarioClaseService {
    private final HorarioClaseService service;
    public void exportar(FiltroHorario filtro,OutputStream out)throws IOException{
        try(SXSSFWorkbook w=new SXSSFWorkbook(200)){w.setCompressTempFiles(true);Sheet s=w.createSheet("Horarios y clases");
            String[] h={"Plantel","Ciclo","Maestro","Número","Grado","Grupo","Materia","Día","Inicio","Fin","Vigencia inicial","Vigencia final","Aula","Estado","Observaciones"};Row r=s.createRow(0);CellStyle cs=w.createCellStyle();cs.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);Font ft=w.createFont();ft.setBold(true);ft.setColor(IndexedColors.WHITE.getIndex());cs.setFont(ft);for(int i=0;i<h.length;i++){Cell c=r.createCell(i);c.setCellValue(h[i]);c.setCellStyle(cs);}int n=1,p=0;Page<HorarioClaseFila>b;do{b=service.bloque(filtro.conPagina(p++,100));for(var x:b){Row z=s.createRow(n++);String[]v={x.plantel(),x.ciclo(),x.maestro(),x.numeroEmpleado(),x.grado(),x.grupo(),x.materia(),x.dia(),x.horaInicio().toString(),x.horaFin().toString(),x.fechaInicio().toString(),x.fechaFin().toString(),x.aula(),x.activo()?"Activo":"Inactivo",x.observaciones()};for(int i=0;i<v.length;i++)z.createCell(i).setCellValue(v[i]==null?"":v[i]);}}while(p<b.getTotalPages());s.createFreezePane(0,1);for(int i=0;i<h.length;i++)s.setColumnWidth(i,(i==14?34:18)*256);w.write(out);w.dispose();}
    }
}
