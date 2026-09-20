package escuela.admin.service;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;

@Service @RequiredArgsConstructor
public class ExcelEventoEscolarService {
    private final EventoEscolarConsultaService consulta;

    public void exportar(FiltroEventoEscolar filtro, OutputStream salida) throws IOException {
        try (SXSSFWorkbook libro = new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true); Sheet hoja = libro.createSheet("Eventos escolares");
            String[] columnas = {"Folio", "Evento", "Institución", "Plantel", "Ciclo", "Inicio", "Fin",
                    "Tipo", "Estado", "Alcance", "Destinatarios"};
            CellStyle estilo = libro.createCellStyle();
            estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND); Font fuente = libro.createFont();
            fuente.setBold(true); fuente.setColor(IndexedColors.WHITE.getIndex()); estilo.setFont(fuente);
            Row cabecera = hoja.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell c = cabecera.createCell(i); c.setCellValue(columnas[i]); c.setCellStyle(estilo);
            }
            int fila = 1, pagina = 0; boolean continuar;
            do {
                var bloque = consulta.consultar(filtro.conPagina(pagina++, 100));
                for (EventoEscolarFila d : bloque.getContent()) {
                    Row r = hoja.createRow(fila++); r.createCell(0).setCellValue(d.id());
                    r.createCell(1).setCellValue(d.titulo()); r.createCell(2).setCellValue(d.institucion());
                    r.createCell(3).setCellValue(d.plantel()); r.createCell(4).setCellValue(d.ciclo());
                    r.createCell(5).setCellValue(d.inicio()); r.createCell(6).setCellValue(d.fin());
                    r.createCell(7).setCellValue(d.tipo()); r.createCell(8).setCellValue(d.estado());
                    r.createCell(9).setCellValue(d.alcance()); r.createCell(10).setCellValue(d.destinatarios());
                }
                continuar = pagina < bloque.getTotalPages();
            } while (continuar);
            hoja.createFreezePane(0, 1);
            for (int i = 0; i < columnas.length; i++) hoja.setColumnWidth(i, (i == 1 ? 34 : 20) * 256);
            libro.write(salida); libro.dispose();
        }
    }
}
