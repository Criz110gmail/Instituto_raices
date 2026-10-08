package escuela.admin.service;

import escuela.admin.dto.*;
import java.io.*;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExcelSituacionAdeudoTest {
    @Test void conservaSituacionTextoYPaginacionEnExcel() throws Exception {
        var consulta = mock(CatalogoConsultaService.class);
        var fila = new FilaCatalogo(1L,List.of("Alumno","Concepto","Descripción","Periodo","Fecha","$1000","$700"),"Parcial · Vencido","aviso");
        var filtro = new FiltroCatalogo("Ana","PARCIAL",4,25);
        when(consulta.consultar(ModuloCatalogo.CARGOS,new FiltroCatalogo("Ana","PARCIAL",0,100)))
                .thenReturn(new ResultadoCatalogo(ModuloCatalogo.CARGOS,ModuloCatalogo.CARGOS.columnas(),new PageImpl<>(List.of(fila),PageRequest.of(0,100),101)));
        when(consulta.consultar(ModuloCatalogo.CARGOS,new FiltroCatalogo("Ana","PARCIAL",1,100)))
                .thenReturn(new ResultadoCatalogo(ModuloCatalogo.CARGOS,ModuloCatalogo.CARGOS.columnas(),new PageImpl<>(List.of(fila),PageRequest.of(1,100),101)));
        var salida = new ByteArrayOutputStream();
        new ExcelCatalogoService(consulta).exportar(ModuloCatalogo.CARGOS,filtro,salida);
        try(var libro=new XSSFWorkbook(new ByteArrayInputStream(salida.toByteArray()))) {
            var hoja=libro.getSheetAt(0);
            assertThat(hoja.getRow(0).getCell(7).getStringCellValue()).isEqualTo("Situación del adeudo");
            assertThat(hoja.getRow(1).getCell(7).getStringCellValue()).isEqualTo("Parcial · Vencido");
            assertThat(hoja.getLastRowNum()).isEqualTo(2);
        }
        verify(consulta).consultar(ModuloCatalogo.CARGOS,new FiltroCatalogo("Ana","PARCIAL",0,100));
        verify(consulta).consultar(ModuloCatalogo.CARGOS,new FiltroCatalogo("Ana","PARCIAL",1,100));
        verifyNoMoreInteractions(consulta);
    }
}
