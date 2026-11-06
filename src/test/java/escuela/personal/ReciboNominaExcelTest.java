package escuela.personal;

import escuela.personal.controller.ReciboNominaController;
import escuela.personal.dto.FiltroReciboNomina;
import escuela.personal.service.*;
import escuela.admin.service.JasperReciboNominaService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class ReciboNominaExcelTest {
    @Test void exportaTodasLasPaginasDelFiltroConCerosYFechasLegibles()throws Exception{
        var service=mock(ReciboNominaService.class);
        var filas=IntStream.rangeClosed(1,101).mapToObj(i->new ReciboNominaService.Fila("CERO",(long)i,(long)i,1L,2L,3L,"EMP-QA","Persona Ficticia","NOV-QA","NETO_CERO",LocalDate.of(2030,11,1),LocalDate.of(2030,11,30),BigDecimal.ZERO,"MXN","06/11/2026 10:00")).toList();
        when(service.listar(any())).thenAnswer(invocacion->{FiltroReciboNomina f=invocacion.getArgument(0);assertThat(f.empleadoId()).isEqualTo(2);assertThat(f.estado()).isEqualTo("NETO_CERO");return new PageImpl<>(f.pagina()==0?filas.subList(0,100):List.of(filas.get(100)),PageRequest.of(f.pagina(),100),101);});
        var mvc=MockMvcBuilders.standaloneSetup(new ReciboNominaController(service,mock(JasperReciboNominaService.class),mock(EmpleadoService.class))).build();
        var respuesta=mvc.perform(get("/admin/recibos-nomina/excel").param("empleadoId","2").param("estado","NETO_CERO").param("tamanio","100")).andReturn().getResponse();
        assertThat(respuesta.getStatus()).isEqualTo(200);assertThat(respuesta.getHeader("Cache-Control")).contains("no-store");
        try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(respuesta.getContentAsByteArray()))){
            var hoja=libro.getSheetAt(0);assertThat(hoja.getLastRowNum()).isEqualTo(101);assertThat(hoja.getRow(101).getCell(0).getStringCellValue()).isEqualTo("RN-C-000101");assertThat(hoja.getRow(1).getCell(5).getStringCellValue()).isEqualTo("01/11/2030");assertThat(hoja.getRow(1).getCell(8).getNumericCellValue()).isZero();assertThat(hoja.getRow(1).getCell(8).getCellStyle().getDataFormatString()).isEqualTo("\"$\"#,##0.00");
        }
        verify(service,times(2)).listar(any());
    }
}
