package escuela.admin.service;
import escuela.admin.dto.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class ExcelCoberturaAnualPagoTest {
 @Test void mismosFiltrosAnualesEnTodosLosBloquesYColumnasDeCobertura()throws Exception{
  var consulta=mock(CatalogoConsultaService.class);
  var celdas=new java.util.ArrayList<>(ModuloCatalogo.PAGOS.columnas().stream().map(x->"Dato anual").toList());celdas.set(12,"$1,000.00");
  var fila=new FilaCatalogo(1L,celdas,"Validado","positivo");
  for(int i=0;i<2;i++)when(consulta.consultar(ModuloCatalogo.PAGOS,new FiltroCatalogo("Tutor","VALIDADO",null,i,100,null,null,"REGISTRO","ANUAL_LIQUIDADO",2027))).thenReturn(new ResultadoCatalogo(ModuloCatalogo.PAGOS,ModuloCatalogo.PAGOS.columnas(),new PageImpl<>(List.of(fila),PageRequest.of(i,100),101)));
  var salida=new ByteArrayOutputStream();new ExcelCatalogoService(consulta).exportar(ModuloCatalogo.PAGOS,new FiltroCatalogo("Tutor","VALIDADO",null,3,25,null,null,"REGISTRO","ANUAL_LIQUIDADO",2027),salida);
  try(var libro=new XSSFWorkbook(new ByteArrayInputStream(salida.toByteArray()))){
   assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(2);
   assertThat(libro.getSheetAt(0).getRow(0).getCell(11).getStringCellValue()).isEqualTo("Cobertura por alumno / año");
   assertThat(libro.getSheetAt(0).getRow(0).getCell(12).getStringCellValue()).isEqualTo("Beneficio anual pactado");
   assertThat(libro.getSheetAt(0).getRow(1).getCell(12).getNumericCellValue()).isEqualTo(1000);
  }
  verify(consulta,times(2)).consultar(eq(ModuloCatalogo.PAGOS),argThat(f->f.cobertura().equals("ANUAL_LIQUIDADO")&&f.anioCobertura()==2027&&f.tamanio()==100));
 }
}
