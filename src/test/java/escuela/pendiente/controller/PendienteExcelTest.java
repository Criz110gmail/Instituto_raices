package escuela.pendiente.controller;

import escuela.pendiente.dto.*;
import escuela.pendiente.entity.EstadoPendiente;
import escuela.pendiente.service.PendienteService;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import org.springframework.data.domain.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class PendienteExcelTest {
 @Test void exportaMismosFiltrosPorBloquesSinFormulasNiFechasTexto()throws Exception{
  var s=mock(PendienteService.class);var p=new UsuarioPrincipal(7L,1L,Set.of(),true,false,"qa","hash",List.of());var f=new PendienteFiltro("Preparar","REALIZADO",LocalDate.of(2026,10,1),LocalDate.of(2026,10,31),4,25);
  var v=new PendienteVista(3L,"=Nota privada","Descripción",LocalDate.of(2026,10,20),EstadoPendiente.REALIZADO,0L,null,false);
  when(s.listar(eq(p),eq(false),any())).thenAnswer(a->{PendienteFiltro actual=a.getArgument(2);assertThat(actual.q()).isEqualTo("Preparar");assertThat(actual.estado()).isEqualTo("REALIZADO");assertThat(actual.desde()).isEqualTo(f.desde());assertThat(actual.hasta()).isEqualTo(f.hasta());assertThat(actual.tamanio()).isEqualTo(100);return new PageImpl<>(actual.pagina()==0?Collections.nCopies(100,v):List.of(v),PageRequest.of(actual.pagina(),100),101);});
  var request=new MockHttpServletRequest("GET","/admin/pendientes/excel");var response=new MockHttpServletResponse();new PendienteController(s).excel(p,f,request,response);
  try(var l=new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()))){var h=l.getSheetAt(0);assertThat(h.getLastRowNum()).isEqualTo(101);assertThat(h.getRow(1).getCell(0).getCellType()).isEqualTo(CellType.STRING);assertThat(h.getRow(1).getCell(2).getCellType()).isEqualTo(CellType.NUMERIC);assertThat(h.getRow(1).getCell(2).getCellStyle().getDataFormatString()).isEqualTo("dd/mm/yyyy");assertThat(h.getRow(101).getCell(3).getStringCellValue()).isEqualTo("Realizado");}
  verify(s,times(2)).listar(eq(p),eq(false),any());assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store");
 }
}
