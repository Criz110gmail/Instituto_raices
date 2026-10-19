package escuela.personal;

import escuela.personal.dto.*;
import escuela.personal.service.*;
import org.junit.jupiter.api.Test;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.data.domain.*;
import java.io.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class ExcelEmpleadoServiceTest {
 @Test void paginaSinPerderFiltrosYNoInterpretaTextoComoFormula() throws Exception {
  var servicio=mock(EmpleadoService.class);
  var fila=new EmpleadoVista(1L,2L,"=NO_FORMULA","Persona","Ficticia",null,"qa@example.invalid",null,true,false,0L,null,false,null,null);
  var primero=new FiltroEmpleado(2L,"QA","DOCENTE",0,100);
  var segundo=new FiltroEmpleado(2L,"QA","DOCENTE",1,100);
  when(servicio.listar(primero)).thenReturn(new PageImpl<>(Collections.nCopies(100,fila),PageRequest.of(0,100),101));
  when(servicio.listar(segundo)).thenReturn(new PageImpl<>(List.of(fila),PageRequest.of(1,100),101));
  var salida=new ByteArrayOutputStream();
  new ExcelEmpleadoService(servicio).exportar(new FiltroEmpleado(2L,"QA","DOCENTE",8,25),salida);
  try(var libro=new XSSFWorkbook(new ByteArrayInputStream(salida.toByteArray()))){
   var hoja=libro.getSheetAt(0);assertThat(hoja.getLastRowNum()).isEqualTo(101);
   assertThat(hoja.getRow(1).getCell(0).getCellType()).isEqualTo(CellType.STRING);
   assertThat(hoja.getRow(1).getCell(0).getStringCellValue()).isEqualTo("=NO_FORMULA");
   assertThat(hoja.getRow(0).getLastCellNum()).isEqualTo((short)7);
  }
  verify(servicio).listar(primero);verify(servicio).listar(segundo);verifyNoMoreInteractions(servicio);
 }
}
