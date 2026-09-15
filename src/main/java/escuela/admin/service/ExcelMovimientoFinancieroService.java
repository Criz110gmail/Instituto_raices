package escuela.admin.service;

import escuela.admin.dto.FiltroMovimientoFinanciero;
import escuela.admin.dto.MovimientoFinancieroFila;
import escuela.admin.dto.ResultadoMovimientosFinancieros;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class ExcelMovimientoFinancieroService {

    private static final String[] COLUMNAS = {"Fecha", "Cuenta", "Plantel", "Dirección", "Clase",
            "Concepto", "Referencia", "Tercero", "Monto", "Moneda", "Saldo anterior",
            "Saldo posterior", "Folio de pago", "Secuencia"};
    private final MovimientoFinancieroConsultaService consultaService;

    public void exportar(FiltroMovimientoFinanciero filtro, OutputStream salida) throws IOException {
        try (SXSSFWorkbook libro = new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true);
            Sheet hoja = libro.createSheet("Movimientos financieros");
            CellStyle encabezado = encabezado(libro);
            Row titulos = hoja.createRow(0);
            for (int i = 0; i < COLUMNAS.length; i++) {
                Cell celda = titulos.createCell(i);
                celda.setCellValue(COLUMNAS[i]);
                celda.setCellStyle(encabezado);
            }
            int filaNumero = 1;
            int pagina = 0;
            ResultadoMovimientosFinancieros bloque;
            do {
                bloque = consultaService.consultar(filtro.conPagina(pagina++, 100));
                for (MovimientoFinancieroFila dato : bloque.pagina().getContent()) {
                    Row fila = hoja.createRow(filaNumero++);
                    fila.createCell(0).setCellValue(dato.fecha());
                    fila.createCell(1).setCellValue(dato.cuenta());
                    fila.createCell(2).setCellValue(dato.plantel());
                    fila.createCell(3).setCellValue(dato.direccion());
                    fila.createCell(4).setCellValue(dato.clase());
                    fila.createCell(5).setCellValue(dato.concepto());
                    fila.createCell(6).setCellValue(dato.referencia());
                    fila.createCell(7).setCellValue(dato.tercero());
                    fila.createCell(8).setCellValue(dato.monto().doubleValue());
                    fila.createCell(9).setCellValue(dato.moneda());
                    fila.createCell(10).setCellValue(dato.saldoAnterior().doubleValue());
                    fila.createCell(11).setCellValue(dato.saldoPosterior().doubleValue());
                    fila.createCell(12).setCellValue(dato.folioPago());
                    fila.createCell(13).setCellValue(dato.secuenciaCuenta());
                }
            } while (pagina < bloque.pagina().getTotalPages());
            hoja.createFreezePane(0, 1);
            for (int i = 0; i < COLUMNAS.length; i++) hoja.setColumnWidth(i, (i == 5 ? 32 : 20) * 256);
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
