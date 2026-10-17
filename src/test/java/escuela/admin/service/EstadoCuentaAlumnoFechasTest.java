package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.common.support.FormatoMoneda;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class EstadoCuentaAlumnoFechasTest {
    static FiltroEstadoCuentaAlumno filtro() {
        return new FiltroEstadoCuentaAlumno(1L,2L,"Martha Prueba",null,"TODOS",LocalDate.of(2026,10,16),0,25);
    }
    static ResultadoEstadoCuentaAlumno resultado() {
        var filas=List.of(
            new EstadoCuentaCargoFila(3L,"Plantel prueba","2026-2027","Material","Material",LocalDate.of(2026,10,16),LocalDate.of(2026,10,20),new BigDecimal("500"),BigDecimal.ZERO,new BigDecimal("500"),new BigDecimal("500"),BigDecimal.ZERO,"PAGADO","MXN"),
            new EstadoCuentaCargoFila(2L,"Plantel prueba","2026-2027","Colegiatura","Noviembre",LocalDate.of(2026,10,10),LocalDate.of(2026,11,20),new BigDecimal("1000"),new BigDecimal("-200"),new BigDecimal("800"),BigDecimal.ZERO,new BigDecimal("800"),"PENDIENTE","MXN"),
            new EstadoCuentaCargoFila(1L,"Plantel prueba","2026-2027","Colegiatura","Diciembre",LocalDate.of(2026,10,10),LocalDate.of(2026,12,20),new BigDecimal("1000"),new BigDecimal("-200"),new BigDecimal("800"),BigDecimal.ZERO,new BigDecimal("800"),"PENDIENTE","MXN"));
        return new ResultadoEstadoCuentaAlumno(2L,"Martha Prueba","TEST-002",new PageImpl<>(filas),
                new ResumenEstadoCuenta(3,new BigDecimal("2100"),new BigDecimal("500"),new BigDecimal("1600"),BigDecimal.ZERO,"MXN"));
    }

    @Test void excelGuardaFechasRealesConDiaMesAnioEnCadaBloqueSinCambiarImportes() throws Exception {
        var consulta=mock(ReporteFinancieroConsultaService.class);
        when(consulta.estadoCuenta(any())).thenAnswer(i->{
            FiltroEstadoCuentaAlumno f=i.getArgument(0);var r=resultado();
            return new ResultadoEstadoCuentaAlumno(r.alumnoId(),r.alumno(),r.matricula(),
                    new PageImpl<>(r.pagina().getContent(),PageRequest.of(f.pagina(),100),101),r.resumen());
        });
        var salida=new ByteArrayOutputStream();new ExcelReporteFinancieroService(consulta).estadoCuenta(filtro(),salida);
        var carpetaExcel=Path.of("target/qa-logo-financiero");Files.createDirectories(carpetaExcel);
        Files.write(carpetaExcel.resolve("estado-alumno.xlsx"),salida.toByteArray());
        try(var libro=WorkbookFactory.create(new ByteArrayInputStream(salida.toByteArray()))) {
            assertThat(libro.getAllPictures()).hasSize(1);
            var hoja=libro.getSheetAt(0);var formato=new DataFormatter(Locale.US);
            assertThat(hoja.getLastRowNum()).isEqualTo(6+MarcaReporteFinanciero.FILAS);
            for(int inicio:new int[]{1,4}) {
                var fila=hoja.getRow(inicio+MarcaReporteFinanciero.FILAS);
                assertThat(DateUtil.isCellDateFormatted(fila.getCell(5))).isTrue();
                assertThat(fila.getCell(5).getLocalDateTimeCellValue().toLocalDate()).isEqualTo(LocalDate.of(2026,10,16));
                assertThat(formato.formatCellValue(fila.getCell(5))).isEqualTo("16/10/2026");
                assertThat(formato.formatCellValue(fila.getCell(6))).isEqualTo("20/10/2026");
                assertThat(formato.formatCellValue(hoja.getRow(inicio+1+MarcaReporteFinanciero.FILAS).getCell(6))).isEqualTo("20/11/2026");
                assertThat(formato.formatCellValue(hoja.getRow(inicio+2+MarcaReporteFinanciero.FILAS).getCell(6))).isEqualTo("20/12/2026");
                assertThat(fila.getCell(10).getNumericCellValue()).isEqualTo(500);
                assertThat(fila.getCell(11).getNumericCellValue()).isZero();
            }
        }
        verify(consulta,times(2)).estadoCuenta(argThat(f->f.alumnoId().equals(2L)&&f.tamanio()==100));
    }

    @Test void pantallaFormateaFechasPeroConservaIsoEnInputYEnlaces() throws Exception {
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();
        var context=mvc.getDispatcherServlet().getWebApplicationContext();
        var registrar=context.getClass().getDeclaredMethod("addBean",String.class,Object.class);
        registrar.setAccessible(true);registrar.invoke(context,"formatoMoneda",new FormatoMoneda());
        var respuesta=mvc.perform(get("/prueba/fechas-alumno")).andReturn().getResponse();
        assertThat(respuesta.getStatus()).isEqualTo(200);
        assertThat(respuesta.getContentAsString()).contains("20/10/2026","20/11/2026","20/12/2026","Emitido: 16/10/2026","Emitido: 10/10/2026","fechaCorte=2026-10-16","value=\"2026-10-16\"");
        assertThat(respuesta.getContentAsString()).doesNotContain(">2026-10-20<",">16/10/26<");
    }

    @Test void pdfConservaFechasDeCuatroDigitosYSaldosDelCaso() throws Exception {
        var consulta=mock(ReporteFinancieroConsultaService.class);var instituciones=mock(InstitucionService.class);
        when(consulta.normalizar(any(FiltroEstadoCuentaAlumno.class))).thenAnswer(i->i.getArgument(0));
        when(consulta.estadoCuenta(any())).thenReturn(resultado());
        when(instituciones.obtener(1L)).thenReturn(new InstitucionResponse(1L,"TEST","Instituto prueba",null,null,null,null,null,null,null,null,null,null,"México",null,"America/Mexico_City","MXN",true,null));
        var salida=new ByteArrayOutputStream();
        new JasperReporteFinancieroService(consulta,mock(EstadoCuentaCuentaService.class),instituciones).estadoAlumno(filtro(),salida);
        try(var pdf=Loader.loadPDF(salida.toByteArray())) {
            assertThat(pdf.getPage(0).getResources().getXObjectNames()).isNotEmpty();
            assertThat(new PDFTextStripper().getText(pdf)).contains("16/10/2026","20/10/2026","20/11/2026","20/12/2026","$2,100.00","$500.00","$1,600.00");
            var carpeta=Path.of("target/qa-fechas-alumno");Files.createDirectories(carpeta);
            ImageIO.write(new PDFRenderer(pdf).renderImageWithDPI(0,120),"png",carpeta.resolve("pdf-fechas.png").toFile());
        }
    }

    @Controller static class Pantalla {
        @GetMapping("/prueba/fechas-alumno") String mostrar(Model model) {
            model.addAttribute("filtro",filtro());model.addAttribute("resultado",resultado());
            model.addAttribute("instituciones",List.of());model.addAttribute("planteles",List.of());
            model.addAttribute("situaciones",List.of());model.addAttribute("modulos",List.of());
            return "admin/estado-cuenta-alumno";
        }
    }
}
