package escuela.compras.service;

import escuela.compras.dto.*;
import escuela.compras.entity.EstadoCompra;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcelCompraServiceTest {
 private final CompraService compras = mock(CompraService.class);
 private final ExcelCompraService excel = new ExcelCompraService(compras);
 private final FiltroCompra filtro = new FiltroCompra(1L,2L,3L,4L,EstadoCompra.CANCELADA,
   LocalDate.of(2026,10,16),LocalDate.of(2026,10,16),"COM-00000001",0,25);

 private CompraFila fila(String total, Instant fecha) {
  return new CompraFila(1L,"COM-00000001",fecha,"Papelería Prueba Raíces",null,
   "Plantel prueba de permisos","Cuenta de prueba de compras","TICKET-PRUEBA-001",
   new BigDecimal(total),EstadoCompra.CANCELADA,4L,2L);
 }
 private byte[] exportar() throws Exception {
  when(compras.zonaInstitucion(1L)).thenReturn(ZoneId.of("America/Mexico_City"));
  var salida = new ByteArrayOutputStream();
  excel.exportar(filtro,salida);
  return salida.toByteArray();
 }
 @Test void fechaYMonedaNativasConservanContenidoYHoraInstitucional() throws Exception {
  when(compras.listar(filtro.conPagina(0,100))).thenReturn(new PageImpl<>(List.of(
   fila("406.00",Instant.parse("2026-10-16T23:58:00Z")))));
  byte[] bytes = exportar();
  try (var libro = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
   var hoja = libro.getSheet("Compras");
   var registro = hoja.getRow(1);
   var formato = new DataFormatter(Locale.US);
   assertThat(registro.getCell(1).getCellType()).isEqualTo(CellType.NUMERIC);
   assertThat(DateUtil.isCellDateFormatted(registro.getCell(1))).isTrue();
   assertThat(registro.getCell(1).getLocalDateTimeCellValue()).isEqualTo(LocalDateTime.of(2026,10,16,17,58));
   assertThat(formato.formatCellValue(registro.getCell(1))).isEqualTo("16/10/2026 17:58");
   assertThat(registro.getCell(7).getCellType()).isEqualTo(CellType.NUMERIC);
   assertThat(registro.getCell(7).getNumericCellValue()).isEqualTo(406.0);
   assertThat(formato.formatCellValue(registro.getCell(7))).isEqualTo("$406.00");
   assertThat(registro.getCell(0).getStringCellValue()).isEqualTo("COM-00000001");
   assertThat(registro.getCell(3).getStringCellValue()).isEmpty();
   assertThat(registro.getCell(8).getStringCellValue()).isEqualTo("Cancelada");
   assertThat(registro.getCell(9).getStringCellValue()).isEqualTo("4");
   assertThat(hoja.getPhysicalNumberOfRows()).isEqualTo(2);
   assertThat(libro.getNumberOfSheets()).isEqualTo(1);
   assertThat(hoja.getColumnWidth(1)).isGreaterThanOrEqualTo(23*256);
   assertThat(hoja.getColumnWidth(7)).isGreaterThanOrEqualTo(20*256);
  }
  // Fixture ficticia para inspección visual; no lee datos de la instalación.
  Path carpeta = Path.of("target/qa-compras");
  Files.createDirectories(carpeta);
  Files.write(carpeta.resolve("compras-formato.xlsx"),bytes);
 }
 @Test void agrupaMilesYRespetaCambioDeDiaAlRecorrerPaginasConMismosFiltros() throws Exception {
  var primera = filtro.conPagina(0,100);
  var segunda = filtro.conPagina(1,100);
  when(compras.listar(primera)).thenReturn(new PageImpl<>(Collections.nCopies(100,
   fila("1000000.50",Instant.parse("2026-10-17T01:58:00Z"))),PageRequest.of(0,100),101));
  when(compras.listar(segunda)).thenReturn(new PageImpl<>(List.of(
   fila("0.00",Instant.parse("2026-10-17T01:58:00Z"))),PageRequest.of(1,100),101));
  try (var libro = new XSSFWorkbook(new ByteArrayInputStream(exportar()))) {
   var hoja = libro.getSheetAt(0);
   var formato = new DataFormatter(Locale.US);
   assertThat(hoja.getLastRowNum()).isEqualTo(101);
   assertThat(formato.formatCellValue(hoja.getRow(1).getCell(7))).isEqualTo("$1,000,000.50");
   assertThat(formato.formatCellValue(hoja.getRow(101).getCell(7))).isEqualTo("$0.00");
   assertThat(formato.formatCellValue(hoja.getRow(101).getCell(1))).isEqualTo("16/10/2026 19:58");
   assertThat(hoja.getRow(1).getCell(7).getCellStyle().getIndex())
    .isEqualTo(hoja.getRow(101).getCell(7).getCellStyle().getIndex());
  }
  verify(compras).listar(primera);
  verify(compras).listar(segunda);
  verify(compras, times(2)).listar(any());
 }
 @Test void sinResultadosConservaSoloEncabezados() throws Exception {
  when(compras.listar(filtro.conPagina(0,100))).thenReturn(Page.empty());
  try (var libro = new XSSFWorkbook(new ByteArrayInputStream(exportar()))) {
   assertThat(libro.getSheetAt(0).getPhysicalNumberOfRows()).isEqualTo(1);
   assertThat(libro.getSheetAt(0).getRow(0).getLastCellNum()).isEqualTo((short)10);
  }
 }
}
