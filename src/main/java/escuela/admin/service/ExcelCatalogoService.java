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
        filtro=filtro.normalizado();filtro.validarRango();if(modulo==ModuloCatalogo.PAGOS)filtro.validarCobertura();
        try (SXSSFWorkbook libro = new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true);
            Sheet hoja = libro.createSheet(modulo.titulo());
            CellStyle encabezado = encabezado(libro);
            CellStyle coberturaEstilo=libro.createCellStyle();coberturaEstilo.setWrapText(true);
            CellStyle beneficioEstilo=libro.createCellStyle();beneficioEstilo.setDataFormat(libro.createDataFormat().getFormat("#,##0.00"));
            Row filaEncabezado = hoja.createRow(0);
            int columna = 0;
            for (String titulo : modulo.columnas()) {
                Cell celda = filaEncabezado.createCell(columna++);
                celda.setCellValue(titulo);
                celda.setCellStyle(encabezado);
            }
            Cell estado = filaEncabezado.createCell(columna);
            estado.setCellValue(modulo == ModuloCatalogo.CARGOS ? "Situación del adeudo" : "Estado");
            estado.setCellStyle(encabezado);

            int numeroFila = 1;
            int pagina = 0;
            ResultadoCatalogo bloque;
            do {
                bloque = consultaService.consultar(modulo,
                        new FiltroCatalogo(filtro.q(), filtro.estado(), filtro.fecha(), pagina++, 100, filtro.desde(), filtro.hasta(), filtro.tipoFecha(),filtro.cobertura(),filtro.anioCobertura(),filtro.conceptoCobro(),filtro.alumnoId(),filtro.alumnoTexto()));
                for (FilaCatalogo dato : bloque.pagina().getContent()) {
                    Row fila = hoja.createRow(numeroFila++);
                    int c = 0;
                    for (String valor : dato.celdas()) {
                        Cell celda=fila.createCell(c);
                        if(modulo==ModuloCatalogo.PAGOS&&c==12){celda.setCellValue(new java.math.BigDecimal(valor.replace("$","").replace(",","").trim()).doubleValue());celda.setCellStyle(beneficioEstilo);}
                        else {celda.setCellValue(valor);if(modulo==ModuloCatalogo.PAGOS&&c==11){celda.setCellStyle(coberturaEstilo);fila.setHeightInPoints(60);}}
                        c++;
                    }
                    fila.createCell(c).setCellValue(dato.estado());
                }
            } while (pagina < bloque.pagina().getTotalPages());

            hoja.createFreezePane(0, 1);
            for (int i = 0; i <= modulo.columnas().size(); i++) hoja.setColumnWidth(i, 22 * 256);
            if(modulo==ModuloCatalogo.PAGOS)hoja.setColumnWidth(11,60*256);
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
