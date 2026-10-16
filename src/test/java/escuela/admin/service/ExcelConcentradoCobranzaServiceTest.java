package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.institucion.service.*;
import escuela.institucion.dto.response.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcelConcentradoCobranzaServiceTest {
    private final ReporteFinancieroConsultaService consulta=mock(ReporteFinancieroConsultaService.class);
    private final InstitucionService instituciones=mock(InstitucionService.class);
    private final PlantelService planteles=mock(PlantelService.class);
    private final ExcelConcentradoCobranzaService servicio=new ExcelConcentradoCobranzaService(consulta,instituciones,planteles);
    private final ResumenEstadoCuenta resumen=new ResumenEstadoCuenta(3,new BigDecimal("2100"),new BigDecimal("500"),new BigDecimal("1600"),BigDecimal.ZERO,"MXN");

    private void preparar() {
        when(consulta.normalizar(any(FiltroConcentradoCobranza.class))).thenAnswer(i->i.getArgument(0));
        var institucion=mock(InstitucionResponse.class);when(institucion.nombre()).thenReturn("Instituto prueba");when(instituciones.obtener(1L)).thenReturn(institucion);
        var plantel=mock(PlantelResponse.class);when(plantel.nombre()).thenReturn("Plantel prueba");when(planteles.obtener(3L)).thenReturn(plantel);
    }
    private FiltroConcentradoCobranza filtro(String agrupacion){return new FiltroConcentradoCobranza(1L,3L,agrupacion,LocalDate.of(2026,10,16),4,25);}
    private ConcentradoCobranzaFila fila(String nombre,long cargos,String importe,String cobrado,String saldo){return new ConcentradoCobranzaFila(nombre,cargos,new BigDecimal(importe),new BigDecimal(cobrado),new BigDecimal(saldo),BigDecimal.ZERO,"MXN");}

    @Test void tresAgrupacionesConservanResumenFechaNumerosYFiltros() throws Exception {
        preparar();
        for(String grupo:List.of("CONCEPTO","PLANTEL","CICLO")) {
            var filas=grupo.equals("CONCEPTO")?List.of(fila("ANTICIPO-TEST-02 · Colegiatura prueba de cancelación de anticipo",2,"1600","0","1600"),fila("MATERIAL · Material utilizado",1,"500","500","0")):
                    List.of(fila(grupo.equals("PLANTEL")?"Plantel prueba":"Ciclo Escolar 2026-2027",3,"2100","500","1600"));
            when(consulta.concentradoCobranza(any())).thenReturn(new ResultadoConcentradoCobranza(new PageImpl<>(filas),resumen));
            var bytes=new ByteArrayOutputStream();servicio.exportar(filtro(grupo),bytes);
            try(var libro=WorkbookFactory.create(new ByteArrayInputStream(bytes.toByteArray()))) {
                assertThat(libro.getNumberOfSheets()).isEqualTo(2);assertThat(libro.getActiveSheetIndex()).isZero();
                var r=libro.getSheet("Resumen");var formato=new DataFormatter(Locale.US);
                assertThat(formato.formatCellValue(r.getRow(2).getCell(1))).isEqualTo("16/10/2026");
                assertThat(DateUtil.isCellDateFormatted(r.getRow(2).getCell(1))).isTrue();
                assertThat(r.getRow(3).getCell(1).getStringCellValue()).isEqualTo("Instituto prueba");
                assertThat(r.getRow(4).getCell(1).getStringCellValue()).isEqualTo("Plantel prueba");
                assertThat(r.getRow(8).getCell(1).getNumericCellValue()).isEqualTo(3);
                assertThat(r.getRow(9).getCell(1).getNumericCellValue()).isEqualTo(2100);
                assertThat(formato.formatCellValue(r.getRow(9).getCell(1))).isEqualTo("$2,100.00");
                assertThat(r.getRow(10).getCell(1).getNumericCellValue()).isEqualTo(500);
                assertThat(r.getRow(11).getCell(1).getNumericCellValue()).isEqualTo(1600);
                assertThat(r.getRow(12).getCell(1).getNumericCellValue()).isZero();
                var d=libro.getSheet("Concentrado");assertThat(d.getLastRowNum()).isEqualTo(filas.size());
                assertThat(d.getRow(0).getCell(0).getStringCellValue()).isEqualTo(grupo.equals("CONCEPTO")?"Concepto de cobro":grupo.equals("PLANTEL")?"Plantel":"Ciclo escolar");
                assertThat(d.getRow(1).getCell(2).getCellType()).isEqualTo(CellType.NUMERIC);
            }
            var carpeta=Path.of("target/qa-concentrado");Files.createDirectories(carpeta);Files.write(carpeta.resolve(grupo.toLowerCase()+".xlsx"),bytes.toByteArray());
            verify(consulta).concentradoCobranza(argThat(f->f.agrupacion().equals(grupo)&&f.institucionId().equals(1L)&&f.plantelId().equals(3L)&&f.fechaCorte().equals(LocalDate.of(2026,10,16))&&f.pagina()==0&&f.tamanio()==100));
            clearInvocations(consulta);
        }
    }
    @Test void descargaTodasLasPaginasSinRepetirResumenNiCambiarAlcance() throws Exception {
        preparar();
        when(consulta.concentradoCobranza(any())).thenAnswer(i->{FiltroConcentradoCobranza f=i.getArgument(0);
            var filas=java.util.stream.IntStream.range(f.pagina()*100,Math.min(101,(f.pagina()+1)*100)).mapToObj(n->fila("Grupo "+n,1,"1","0","1")).toList();
            return new ResultadoConcentradoCobranza(new PageImpl<>(filas,PageRequest.of(f.pagina(),100),101),resumen);
        });
        var bytes=new ByteArrayOutputStream();servicio.exportar(filtro("CONCEPTO"),bytes);
        try(var libro=WorkbookFactory.create(new ByteArrayInputStream(bytes.toByteArray()))) {
            var d=libro.getSheet("Concentrado");assertThat(d.getLastRowNum()).isEqualTo(101);
            assertThat(d.getRow(1).getCell(0).getStringCellValue()).isEqualTo("Grupo 0");
            assertThat(d.getRow(101).getCell(0).getStringCellValue()).isEqualTo("Grupo 100");
            assertThat(libro.getSheet("Resumen").getRow(9).getCell(1).getNumericCellValue()).isEqualTo(2100);
        }
        verify(consulta,times(2)).concentradoCobranza(argThat(f->f.institucionId().equals(1L)&&f.plantelId().equals(3L)&&f.tamanio()==100));
    }
    @Test void sinFilasConservaResumenYMensajeDeVacio() throws Exception {
        preparar();when(consulta.concentradoCobranza(any())).thenReturn(new ResultadoConcentradoCobranza(Page.empty(),ResumenEstadoCuenta.vacio("MXN")));
        var bytes=new ByteArrayOutputStream();servicio.exportar(new FiltroConcentradoCobranza(1L,null,"CICLO",LocalDate.of(2026,10,16),0,100),bytes);
        try(var libro=WorkbookFactory.create(new ByteArrayInputStream(bytes.toByteArray()))) {
            assertThat(libro.getSheet("Resumen").getRow(4).getCell(1).getStringCellValue()).isEqualTo("Todos autorizados");
            assertThat(libro.getSheet("Resumen").getRow(8).getCell(1).getNumericCellValue()).isZero();
            assertThat(libro.getSheet("Concentrado").getRow(1).getCell(0).getStringCellValue()).contains("Sin cartera");
        }
        verifyNoInteractions(planteles);
    }
    @Test void alcanceDenegadoNoEscribeDatosNiConsultaNombres() {
        when(consulta.normalizar(any(FiltroConcentradoCobranza.class))).thenAnswer(i->i.getArgument(0));
        when(consulta.concentradoCobranza(any())).thenThrow(new AccessDeniedException("Plantel no autorizado"));
        var bytes=new ByteArrayOutputStream();assertThatThrownBy(()->servicio.exportar(filtro("PLANTEL"),bytes)).isInstanceOf(AccessDeniedException.class);
        assertThat(bytes.size()).isZero();verifyNoInteractions(instituciones,planteles);
    }
}
