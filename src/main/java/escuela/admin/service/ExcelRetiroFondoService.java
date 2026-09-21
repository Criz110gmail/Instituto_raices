package escuela.admin.service;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class ExcelRetiroFondoService {
    private static final String[] COLUMNAS = {"ID", "Fecha", "Cuenta", "Plantel", "Destinatario",
            "Motivo", "Concepto", "Referencia / comprobante", "Monto", "Moneda",
            "Autorizado por", "Estado", "Movimiento ID"};
    private final RetiroFondoConsultaService consulta;

    public void exportar(FiltroRetiroFondo filtro, OutputStream salida) throws IOException {
        try (SXSSFWorkbook libro = new SXSSFWorkbook(200)) {
            libro.setCompressTempFiles(true);
            Sheet hoja = libro.createSheet("Retiros de fondos");
            CellStyle estilo = libro.createCellStyle();
            estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font fuente = libro.createFont(); fuente.setBold(true);
            fuente.setColor(IndexedColors.WHITE.getIndex()); estilo.setFont(fuente);
            Row titulos = hoja.createRow(0);
            for (int i = 0; i < COLUMNAS.length; i++) {
                Cell celda = titulos.createCell(i); celda.setCellValue(COLUMNAS[i]); celda.setCellStyle(estilo);
                hoja.setColumnWidth(i, (i == 6 ? 34 : 22) * 256);
            }
            int numero = 1, pagina = 0;
            Page<RetiroFondoFila> bloque;
            do {
                bloque = consulta.consultar(filtro.conPagina(pagina++, 100));
                for (RetiroFondoFila dato : bloque.getContent()) {
                    Row fila = hoja.createRow(numero++);
                    fila.createCell(0).setCellValue(dato.id());
                    fila.createCell(1).setCellValue(dato.fecha());
                    fila.createCell(2).setCellValue(dato.cuenta());
                    fila.createCell(3).setCellValue(dato.plantel());
                    fila.createCell(4).setCellValue(dato.beneficiario());
                    fila.createCell(5).setCellValue(dato.motivo());
                    fila.createCell(6).setCellValue(dato.concepto());
                    fila.createCell(7).setCellValue(dato.referencia());
                    fila.createCell(8).setCellValue(dato.monto().doubleValue());
                    fila.createCell(9).setCellValue(dato.moneda());
                    fila.createCell(10).setCellValue(dato.autorizadoPor());
                    fila.createCell(11).setCellValue(dato.estado());
                    fila.createCell(12).setCellValue(dato.movimientoId());
                }
            } while (pagina < bloque.getTotalPages());
            hoja.createFreezePane(0, 1);
            libro.write(salida);
            libro.dispose();
        }
    }
}
