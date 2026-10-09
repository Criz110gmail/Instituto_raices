package escuela.admin.controller;
import escuela.cobranza.service.AcuerdoAnticipadoService;
import escuela.cobranza.service.AcuerdoAnticipadoService.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class AnticipoExcelTest {
 @Test void exportaDosPaginasConMismosFiltrosYMontosNumericos()throws Exception {
  var s=mock(AcuerdoAnticipadoService.class);var ctl=new AnticipoAdminController(s,null,null,null);
  var row=new Fila(7L,"Hijo","Colegiatura","Octubre",new BigDecimal("1000"),new BigDecimal("200"),new BigDecimal("800"),new BigDecimal("800"),new BigDecimal("80"),new BigDecimal("720"),"MXN");
  var d=new Vista(8L,0L,"ANT-TEST","Familia","Centro","MXN",LocalDate.now(),"CONSERVAR","PORCENTAJE","Prueba","Administrador","APLICADO",new BigDecimal("800"),new BigDecimal("80"),new BigDecimal("720"),List.of(row),null,null,null,1L,2L,"10 % adicional",false);
  when(s.listar(1L,"APLICADO","ANT-TEST",0,100)).thenReturn(new PageImpl<>(List.of(d),PageRequest.of(0,100),101));when(s.listar(1L,"APLICADO","ANT-TEST",1,100)).thenReturn(new PageImpl<>(List.of(d),PageRequest.of(1,100),101));
  var response=new MockHttpServletResponse();ctl.excel(1L,"APLICADO","ANT-TEST",response);
  try(var book=new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()))){var sh=book.getSheetAt(0);assertThat(sh.getLastRowNum()).isEqualTo(2);assertThat(sh.getRow(0).getCell(12).getStringCellValue()).isEqualTo("Otros ajustes previos");assertThat(sh.getRow(1).getCell(15).getNumericCellValue()).isEqualTo(720);assertThat(sh.getRow(2).getCell(11).getNumericCellValue()).isEqualTo(200);}
  verify(s).listar(1L,"APLICADO","ANT-TEST",0,100);verify(s).listar(1L,"APLICADO","ANT-TEST",1,100);
 }
}
