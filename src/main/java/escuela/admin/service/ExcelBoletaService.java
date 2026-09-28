package escuela.admin.service;

import escuela.admin.dto.BoletaCalificacionFila;
import escuela.admin.dto.BoletaDetalle;
import escuela.admin.dto.FiltroBoleta;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class ExcelBoletaService {
    private static final int BLOQUE = 100;
    private final BoletaConsultaService consulta;

    public void exportar(FiltroBoleta original, OutputStream salida) throws IOException {
        FiltroBoleta filtro = consulta.exigirExportacion(original);
        try (SXSSFWorkbook libro = new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true);
            Sheet hoja = libro.createSheet("Boletas");
            String[] titulos = {"Inscripción", "Matrícula", "Alumno", "Institución", "Plantel", "Ciclo",
                    "Grado", "Grupo", "Periodo", "Materia", "Resultado", "Escala", "Observaciones"};
            CellStyle encabezado = encabezado(libro);
            Row cabecera = hoja.createRow(0);
            for (int i = 0; i < titulos.length; i++) {
                Cell celda = cabecera.createCell(i);
                celda.setCellValue(titulos[i]);
                celda.setCellStyle(encabezado);
            }
            int fila = 1;
            int pagina = 0;
            org.springframework.data.domain.Page<BoletaDetalle> bloque;
            do {
                bloque = consulta.bloqueDetallado(filtro.conPagina(pagina++, BLOQUE));
                for (BoletaDetalle boleta : bloque.getContent()) {
                    for (BoletaCalificacionFila calificacion : boleta.calificaciones()) {
                        Row r = hoja.createRow(fila++);
                        texto(r, 0, boleta.numeroInscripcion());
                        texto(r, 1, boleta.matricula());
                        texto(r, 2, boleta.alumno());
                        texto(r, 3, boleta.institucion());
                        texto(r, 4, boleta.plantel());
                        texto(r, 5, boleta.ciclo());
                        texto(r, 6, boleta.grado());
                        texto(r, 7, boleta.grupo());
                        texto(r, 8, calificacion.periodo());
                        texto(r, 9, calificacion.materia());
                        texto(r, 10, calificacion.resultado());
                        texto(r, 11, calificacion.escala());
                        texto(r, 12, calificacion.observaciones());
                    }
                }
            } while (pagina < bloque.getTotalPages());
            hoja.createFreezePane(0, 1);
            int[] anchos = {18, 16, 32, 28, 24, 20, 18, 22, 20, 30, 14, 28, 40};
            for (int i = 0; i < anchos.length; i++) hoja.setColumnWidth(i, anchos[i] * 256);
            hoja.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, Math.max(0, fila - 1), 0, titulos.length - 1));
            libro.write(salida);
            libro.dispose();
        }
    }

    private CellStyle encabezado(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        estilo.setFont(fuente);
        return estilo;
    }

    private void texto(Row fila, int columna, String valor) {
        fila.createCell(columna).setCellValue(valor == null ? "" : valor);
    }
}
