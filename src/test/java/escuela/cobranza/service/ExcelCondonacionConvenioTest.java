package escuela.cobranza.service;
import escuela.cobranza.dto.ConvenioPagoFila;
import escuela.cobranza.entity.EstadoConvenioPago;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class ExcelCondonacionConvenioTest {
 @Test void exportaCeroCondonadoYEstadoLegibleConElMismoFiltro()throws Exception{
  var service=mock(ConvenioPagoService.class);var fecha=LocalDate.of(2026,10,8);
  when(service.listar(1L,EstadoConvenioPago.CONDONADO_TOTAL,"CV-TEST",0,100)).thenReturn(new PageImpl<>(List.of(
   new ConvenioPagoFila(20L,"CV-TEST","Tutor",fecha,fecha,new BigDecimal("500"),BigDecimal.ZERO,new BigDecimal("500"),"MXN","CONDONADO_TOTAL"))));
  var out=new ByteArrayOutputStream();new ExcelConvenioPagoService(service).exportar(1L,EstadoConvenioPago.CONDONADO_TOTAL,"CV-TEST",out);
  try(var book=new XSSFWorkbook(new ByteArrayInputStream(out.toByteArray()))){var row=book.getSheetAt(0).getRow(1);
   assertThat(row.getCell(5).getNumericCellValue()).isZero();assertThat(row.getCell(6).getNumericCellValue()).isEqualTo(500);
   assertThat(row.getCell(8).getStringCellValue()).isEqualTo("Condonado totalmente");}
  verify(service).listar(1L,EstadoConvenioPago.CONDONADO_TOTAL,"CV-TEST",0,100);
 }
}
