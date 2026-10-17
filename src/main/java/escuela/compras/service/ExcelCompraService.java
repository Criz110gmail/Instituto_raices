package escuela.compras.service;

import escuela.compras.dto.FiltroCompra;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.*;

@Service @RequiredArgsConstructor
public class ExcelCompraService {
 private final CompraService service;
 public void exportar(FiltroCompra filtro, OutputStream out) throws IOException {
  var zona = service.zonaInstitucion(filtro.institucionId());
  try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
   workbook.setCompressTempFiles(true);
   Sheet sheet = workbook.createSheet("Compras");
   String[] encabezados = {"Folio", "Fecha", "Proveedor", "RFC", "Plantel", "Cuenta", "Documento", "Total", "Estado", "Movimiento"};
   CellStyle encabezado = workbook.createCellStyle();
   Font negrita = workbook.createFont();
   negrita.setBold(true);
   encabezado.setFont(negrita);
   // Estilos compartidos: no crear uno por registro en exportaciones grandes.
   CellStyle fecha = workbook.createCellStyle();
   fecha.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
   CellStyle moneda = workbook.createCellStyle();
   moneda.setDataFormat(workbook.createDataFormat().getFormat("\"$\"#,##0.00"));
   Row cabecera = sheet.createRow(0);
   for (int i = 0; i < encabezados.length; i++) {
    Cell celda = cabecera.createCell(i);
    celda.setCellValue(encabezados[i]);
    celda.setCellStyle(encabezado);
   }
   int numeroFila = 1, pagina = 0;
   while (true) {
    var bloque = service.listar(filtro.conPagina(pagina, 100));
    for (var compra : bloque) {
     Row fila = sheet.createRow(numeroFila++);
     fila.createCell(0).setCellValue(compra.folio());
     Cell fechaCompra = fila.createCell(1);
     // Fecha nativa de Excel, conservando la hora de la institución.
     fechaCompra.setCellValue(compra.fecha().atZone(zona).toLocalDateTime());
     fechaCompra.setCellStyle(fecha);
     fila.createCell(2).setCellValue(compra.proveedor());
     fila.createCell(3).setCellValue(compra.rfc() == null ? "" : compra.rfc());
     fila.createCell(4).setCellValue(compra.plantel());
     fila.createCell(5).setCellValue(compra.cuenta());
     fila.createCell(6).setCellValue(compra.referencia() == null ? "" : compra.referencia());
     Cell total = fila.createCell(7);
     total.setCellValue(compra.total().doubleValue());
     total.setCellStyle(moneda);
     fila.createCell(8).setCellValue(compra.estado().getEtiqueta());
     fila.createCell(9).setCellValue(compra.movimientoId() == null ? "" : compra.movimientoId().toString());
    }
    if (bloque.isLast()) break;
    pagina++;
   }
   for (int i = 0; i < encabezados.length; i++) {
    sheet.setColumnWidth(i, Math.min(50, Math.max(14, encabezados[i].length() + 8)) * 256);
   }
   sheet.setColumnWidth(1, 23 * 256);
   sheet.setColumnWidth(7, 20 * 256);
   workbook.write(out);
   workbook.dispose();
  }
 }
}
