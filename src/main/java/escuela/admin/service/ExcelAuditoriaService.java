package escuela.admin.service;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
public class ExcelAuditoriaService {
    private static final String[] COLUMNAS = {"Fecha", "Actor", "Acción", "Entidad", "ID entidad",
            "Motivo", "Cambios", "Correlación"};
    private final AuditoriaConsultaService consulta;

    public void exportar(FiltroAuditoria filtro, OutputStream salida) throws IOException {
        try (SXSSFWorkbook libro = new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true); Sheet hoja = libro.createSheet("Auditoría");
            CellStyle encabezado = encabezado(libro); Row titulos = hoja.createRow(0);
            for (int i=0;i<COLUMNAS.length;i++){Cell c=titulos.createCell(i);c.setCellValue(COLUMNAS[i]);c.setCellStyle(encabezado);}
            int numero=1,pagina=0; org.springframework.data.domain.Page<AuditoriaFila> bloque;
            do { bloque=consulta.consultar(filtro.conPagina(pagina++,100));
                for(AuditoriaFila dato:bloque){Row fila=hoja.createRow(numero++);fila.createCell(0).setCellValue(dato.fecha());
                    fila.createCell(1).setCellValue(dato.actor());fila.createCell(2).setCellValue(dato.accion());
                    fila.createCell(3).setCellValue(dato.tipoEntidad());fila.createCell(4).setCellValue(dato.entidadId());
                    fila.createCell(5).setCellValue(dato.motivo());fila.createCell(6).setCellValue(dato.cambios());
                    fila.createCell(7).setCellValue(dato.correlacion());}
            } while(pagina<bloque.getTotalPages());
            hoja.createFreezePane(0,1);for(int i=0;i<COLUMNAS.length;i++)hoja.setColumnWidth(i,(i==6?50:22)*256);
            libro.write(salida);libro.dispose();
        }
    }

    private CellStyle encabezado(Workbook libro){CellStyle e=libro.createCellStyle();e.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());e.setFillPattern(FillPatternType.SOLID_FOREGROUND);Font f=libro.createFont();f.setBold(true);f.setColor(IndexedColors.WHITE.getIndex());e.setFont(f);return e;}
}
