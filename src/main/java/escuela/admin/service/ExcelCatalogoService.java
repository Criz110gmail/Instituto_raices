package escuela.admin.service;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class ExcelCatalogoService {

    private final CatalogoConsultaService consultaService;

    public void exportar(ModuloCatalogo modulo, FiltroCatalogo filtro, OutputStream salida) throws IOException {
        try (SXSSFWorkbook libro = new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true);
            Sheet hoja = libro.createSheet(modulo.titulo());
            CellStyle encabezado = encabezado(libro);
            Row filaEncabezado = hoja.createRow(0);
            int columna = 0;
            for (String titulo : modulo.columnas()) {
                Cell celda = filaEncabezado.createCell(columna++);
                celda.setCellValue(titulo);
                celda.setCellStyle(encabezado);
            }
            Cell estado = filaEncabezado.createCell(columna);
            estado.setCellValue("Estado");
            estado.setCellStyle(encabezado);

            int numeroFila = 1;
            int pagina = 0;
            ResultadoCatalogo bloque;
            do {
                bloque = consultaService.consultar(modulo,
                        new FiltroCatalogo(filtro.q(), filtro.estado(), pagina++, 100));
                for (FilaCatalogo dato : bloque.pagina().getContent()) {
                    Row fila = hoja.createRow(numeroFila++);
                    int c = 0;
                    for (String valor : dato.celdas()) fila.createCell(c++).setCellValue(valor);
                    fila.createCell(c).setCellValue(dato.estado());
                }
            } while (pagina < bloque.pagina().getTotalPages());

            hoja.createFreezePane(0, 1);
            for (int i = 0; i <= modulo.columnas().size(); i++) hoja.setColumnWidth(i, 22 * 256);
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
}
