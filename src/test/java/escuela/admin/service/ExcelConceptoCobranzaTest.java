package escuela.admin.service;

import escuela.admin.dto.*;
import java.io.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcelConceptoCobranzaTest {
    @Test void alumnoYCobroSeConservanEnCadaBloqueDeCuotasYAdeudos() throws Exception {
        for(var modulo:new ModuloCatalogo[]{ModuloCatalogo.CARGOS,ModuloCatalogo.CUOTAS_ALUMNO}) {
            var consulta=mock(CatalogoConsultaService.class);
            var fila=new FilaCatalogo(1L,modulo.columnas().stream().map(c->"Dato").toList(),"Pendiente","aviso");
            when(consulta.consultar(eq(modulo),any())).thenAnswer(i->{FiltroCatalogo f=i.getArgument(1);return new ResultadoCatalogo(modulo,modulo.columnas(),new PageImpl<>(List.of(fila),PageRequest.of(f.pagina(),100),101));});
            var filtro=new FiltroCatalogo("Capitan America","TODOS",null,0,25,null,null,"REGISTRO","TODOS",null,"ANTICIPO-TEST-05");
            var salida=new ByteArrayOutputStream();new ExcelCatalogoService(consulta).exportar(modulo,filtro,salida);
            try(var libro=new XSSFWorkbook(new ByteArrayInputStream(salida.toByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(2);}
            verify(consulta,times(2)).consultar(eq(modulo),argThat(f->f.q().equals("Capitan America")&&f.conceptoCobro().equals("ANTICIPO-TEST-05")&&f.tamanio()==100));
        }
    }
}
