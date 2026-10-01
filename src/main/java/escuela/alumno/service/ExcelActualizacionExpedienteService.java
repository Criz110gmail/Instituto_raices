package escuela.alumno.service;

import escuela.alumno.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ExcelActualizacionExpedienteService {
    private final ActualizacionExpedienteFamiliarService service;

    public void exportar(FiltroActualizacionExpediente filtro, OutputStream salida) throws IOException {
        try (SXSSFWorkbook libro = new SXSSFWorkbook(100)) {
            libro.setCompressTempFiles(true);
            Sheet hoja = libro.createSheet("Actualizaciones familiares");
            String[] encabezados={"Folio","Alumno","Matrícula","Tutor","Tipo","Detalle","Estado","Enviada","Revisada","Respuesta"};
            Row cabecera=hoja.createRow(0);CellStyle estilo=libro.createCellStyle();Font fuente=libro.createFont();fuente.setBold(true);estilo.setFont(fuente);
            for(int i=0;i<encabezados.length;i++){Cell c=cabecera.createCell(i);c.setCellValue(encabezados[i]);c.setCellStyle(estilo);}
            int fila=1,pagina=0;while(true){var bloque=service.listarAdministracion(filtro.conPagina(pagina,100));for(var x:bloque){Row r=hoja.createRow(fila++);int c=0;r.createCell(c++).setCellValue(x.id());r.createCell(c++).setCellValue(x.alumno());r.createCell(c++).setCellValue(x.matricula());r.createCell(c++).setCellValue(x.tutor());r.createCell(c++).setCellValue(x.tipo().getEtiqueta());r.createCell(c++).setCellValue(x.resumen());r.createCell(c++).setCellValue(x.estado().getEtiqueta());r.createCell(c++).setCellValue(x.enviadaEn().atZone(ZoneId.systemDefault()).toLocalDateTime().toString());r.createCell(c++).setCellValue(x.revisadaEn()==null?"":x.revisadaEn().atZone(ZoneId.systemDefault()).toLocalDateTime().toString());r.createCell(c).setCellValue(x.respuestaAdmin()==null?"":x.respuestaAdmin());}if(bloque.isLast())break;pagina++;}
            for(int i=0;i<encabezados.length;i++)hoja.setColumnWidth(i,Math.min(60,Math.max(14,encabezados[i].length()+4))*256);
            libro.write(salida);libro.dispose();
        }
    }
}
