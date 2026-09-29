package escuela.docente.service;

import escuela.docente.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.io.*;

@Service @RequiredArgsConstructor
public class ExcelPlaneacionService {
    private static final int BLOQUE=100;
    private final PlaneacionService planeaciones;

    public void exportar(FiltroPlaneacion filtro,OutputStream salida)throws IOException{
        try(SXSSFWorkbook libro=new SXSSFWorkbook(200)){
            libro.setCompressTempFiles(true);Sheet hoja=libro.createSheet("Planeaciones");
            String[] titulos={"Maestro","Número de empleado","Plantel","Grado","Grupo","Inicio","Fin","Estado","Revisión","Materias"};
            CellStyle estilo=encabezado(libro);Row cabecera=hoja.createRow(0);
            for(int i=0;i<titulos.length;i++){Cell c=cabecera.createCell(i);c.setCellValue(titulos[i]);c.setCellStyle(estilo);}
            int fila=1,pagina=0;Page<PlaneacionFila> bloque;
            do{bloque=planeaciones.bloqueAdmin(filtro.conPagina(pagina++,BLOQUE));for(PlaneacionFila x:bloque){Row r=hoja.createRow(fila++);texto(r,0,x.maestro());texto(r,1,x.numeroEmpleado());texto(r,2,x.plantel());texto(r,3,x.grado());texto(r,4,x.grupo());texto(r,5,x.fechaInicio().toString());texto(r,6,x.fechaFin().toString());texto(r,7,x.estado().getEtiqueta());r.createCell(8).setCellValue(x.revision());r.createCell(9).setCellValue(x.materias());}}while(pagina<bloque.getTotalPages());
            hoja.createFreezePane(0,1);int[] anchos={30,18,26,18,18,13,13,20,11,11};for(int i=0;i<anchos.length;i++)hoja.setColumnWidth(i,anchos[i]*256);hoja.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,fila-1),0,titulos.length-1));libro.write(salida);libro.dispose();
        }
    }
    private CellStyle encabezado(Workbook libro){CellStyle e=libro.createCellStyle();e.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());e.setFillPattern(FillPatternType.SOLID_FOREGROUND);Font f=libro.createFont();f.setBold(true);f.setColor(IndexedColors.WHITE.getIndex());e.setFont(f);return e;}
    private void texto(Row r,int c,String v){r.createCell(c).setCellValue(v==null?"":v);}
}
