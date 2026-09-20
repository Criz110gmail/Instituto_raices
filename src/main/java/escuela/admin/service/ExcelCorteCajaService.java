package escuela.admin.service;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
public class ExcelCorteCajaService {
    private static final String[] COLUMNAS = {"Folio", "Cuenta", "Plantel", "Estado", "Apertura",
            "Abierto por", "Cierre", "Saldo inicial", "Saldo esperado", "Efectivo declarado",
            "Diferencia", "Moneda"};
    private final CorteCajaConsultaService consultaService;

    public void exportar(FiltroCorteCaja filtro, OutputStream salida) throws IOException {
        try (SXSSFWorkbook libro = new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true);
            Sheet hoja = libro.createSheet("Cortes de caja");
            CellStyle encabezado = encabezado(libro);
            Row titulos = hoja.createRow(0);
            for (int i = 0; i < COLUMNAS.length; i++) {
                Cell celda = titulos.createCell(i); celda.setCellValue(COLUMNAS[i]); celda.setCellStyle(encabezado);
            }
            int numeroFila = 1, pagina = 0;
            ResultadoCortesCaja bloque;
            do {
                bloque = consultaService.consultar(filtro.conPagina(pagina++, 100));
                for (CorteCajaFila dato : bloque.pagina().getContent()) {
                    Row fila = hoja.createRow(numeroFila++);
                    fila.createCell(0).setCellValue(dato.id()); fila.createCell(1).setCellValue(dato.cuenta());
                    fila.createCell(2).setCellValue(dato.plantel()); fila.createCell(3).setCellValue(dato.estado());
                    fila.createCell(4).setCellValue(dato.abiertoEn()); fila.createCell(5).setCellValue(dato.abiertoPor());
                    fila.createCell(6).setCellValue(dato.cerradoEn());
                    numero(fila, 7, dato.saldoInicial()); numero(fila, 8, dato.saldoEsperado());
                    numero(fila, 9, dato.efectivoDeclarado()); numero(fila, 10, dato.diferencia());
                    fila.createCell(11).setCellValue(dato.moneda());
                }
            } while (pagina < bloque.pagina().getTotalPages());
            hoja.createFreezePane(0, 1);
            for (int i = 0; i < COLUMNAS.length; i++) hoja.setColumnWidth(i, (i == 1 ? 32 : 20) * 256);
            libro.write(salida); libro.dispose();
        }
    }

    private void numero(Row fila, int indice, java.math.BigDecimal valor) {
        if (valor != null) fila.createCell(indice).setCellValue(valor.doubleValue());
        else fila.createCell(indice).setBlank();
    }

    private CellStyle encabezado(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font fuente = libro.createFont(); fuente.setBold(true); fuente.setColor(IndexedColors.WHITE.getIndex());
        estilo.setFont(fuente); return estilo;
    }
}
