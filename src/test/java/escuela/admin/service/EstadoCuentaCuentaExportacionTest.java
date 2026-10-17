package escuela.admin.service;

import escuela.admin.dto.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EstadoCuentaCuentaExportacionTest {
    private final EstadoCuentaCuentaService consulta = mock(EstadoCuentaCuentaService.class);
    private final ReporteFinancieroConsultaService reportes = mock(ReporteFinancieroConsultaService.class);
    private final InstitucionService instituciones = mock(InstitucionService.class);
    private final FiltroEstadoCuentaCuenta filtro = new FiltroEstadoCuentaCuenta(1L, 2L,
            "CTA · Caja", "MENSUAL", 2026, 9, 0, 100);

    @Test
    void cuentaSinMovimientosConservaResumenVisibleEnExcelYPdf() throws Exception {
        var filtroVacio=new FiltroEstadoCuentaCuenta(1L,3L,"CAJA-SOBRANTE-TEST","MENSUAL",2026,10,0,100);
        var cuenta=new ResumenCuentaFinanciera(3L,"CAJA-SOBRANTE-TEST","CAJA","Centro","MXN",new BigDecimal("1150.00"),0);
        var resumen=new ResumenTesoreria(0,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
            BigDecimal.ZERO,BigDecimal.ZERO,new BigDecimal("1150.00"),true,"MXN");
        var resultado=new ResultadoEstadoCuentaCuenta(cuenta,resumen,org.springframework.data.domain.Page.empty());
        when(consulta.normalizar(any())).thenReturn(filtroVacio);
        when(consulta.consultar(any())).thenReturn(resultado);
        when(instituciones.obtener(1L)).thenReturn(new InstitucionResponse(1L,"RAICES","Instituto Raíces",
            "Instituto Raíces","Instituto Raíces",null,null,null,null,null,null,null,null,"México",
            null,"America/Mexico_City","MXN",true,null));

        var xlsx=new ByteArrayOutputStream();
        new ExcelEstadoCuentaCuentaService(consulta).exportar(filtroVacio,xlsx);
        try(var libro=WorkbookFactory.create(new ByteArrayInputStream(xlsx.toByteArray()))) {
            assertThat(libro.getAllPictures()).hasSize(1);
            for(var hoja:libro)assertThat(((org.apache.poi.xssf.usermodel.XSSFSheet)hoja).getDrawingPatriarch().getShapes()).hasSize(1);
            assertThat(libro.getSheetAt(libro.getActiveSheetIndex()).getSheetName()).isEqualTo("Resumen");
            assertThat(libro.getSheet("Resumen").getRow(7+MarcaReporteFinanciero.FILAS).getCell(1).getNumericCellValue()).isEqualTo(1150.0);
            assertThat(libro.getSheet("Resumen").getRow(5+MarcaReporteFinanciero.FILAS).getCell(1).getNumericCellValue()).isZero();
            assertThat(libro.getSheet("Resumen").getRow(6+MarcaReporteFinanciero.FILAS).getCell(1).getNumericCellValue()).isZero();
            assertThat(libro.getSheet("Resumen").getRow(8+MarcaReporteFinanciero.FILAS).getCell(1).getNumericCellValue()).isEqualTo(1150.0);
            assertThat(libro.getSheet("Estado de cuenta").getRow(1+MarcaReporteFinanciero.FILAS).getCell(0).getStringCellValue()).contains("Sin movimientos");
            assertThat(libro.getSheet("Resumen").getRow(1+MarcaReporteFinanciero.FILAS).getCell(1).getStringCellValue()).isEqualTo("01/10/2026 al 31/10/2026");
        }
        var pdf=new ByteArrayOutputStream();
        new JasperReporteFinancieroService(reportes,consulta,instituciones).estadoCuenta(filtroVacio,pdf);
        try(var documento=Loader.loadPDF(pdf.toByteArray())) {
            assertThat(documento.getPage(0).getResources().getXObjectNames()).isNotEmpty();
            assertThat(documento.getNumberOfPages()).isEqualTo(1);
            assertThat(new PDFTextStripper().getText(documento)).contains("Estado de cuenta financiera",
                "CAJA-SOBRANTE-TEST","01/10/2026","31/10/2026","$1,150.00","$0.00",
                "Sin movimientos","Saldo inicial registrado en el periodo","No es un ingreso por movimiento");
            Path carpeta=Path.of("target","jasper-qa");Files.createDirectories(carpeta);
            Files.write(carpeta.resolve("estado-cuenta-sin-movimientos.pdf"),pdf.toByteArray());
            Files.write(carpeta.resolve("estado-cuenta-sin-movimientos.xlsx"),xlsx.toByteArray());
            ImageIO.write(new PDFRenderer(documento).renderImageWithDPI(0,120),"png",
                carpeta.resolve("estado-cuenta-sin-movimientos.png").toFile());
        }
    }

    @Test
    void excelYpdfUsanMismoPeriodoYDetalle() throws Exception {
        var cuenta = new ResumenCuentaFinanciera(2L, "CTA · Caja", "CAJA", "Centro", "MXN",
                new BigDecimal("1250.00"), 1);
        var resumen = new ResumenTesoreria(1, new BigDecimal("250.00"), BigDecimal.ZERO,
                new BigDecimal("250.00"), BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("1000.00"), new BigDecimal("1250.00"), true, "MXN");
        var movimiento = new MovimientoFinancieroFila(4L, "12 sep 2026 · 10:30", "CTA · Caja",
                "Centro", "INGRESO", "COBRO", "Colegiatura septiembre", "REF-1", "Tutor",
                new BigDecimal("250.00"), "MXN", new BigDecimal("1000.00"), new BigDecimal("1250.00"),
                "PAG-001", 1L, null, null, false);
        var pagina = new PageImpl<>(List.of(movimiento), PageRequest.of(0, 100), 1);
        var resultado = new ResultadoEstadoCuentaCuenta(cuenta, resumen, pagina);
        when(consulta.normalizar(any(FiltroEstadoCuentaCuenta.class))).thenReturn(filtro);
        when(consulta.consultar(any(FiltroEstadoCuentaCuenta.class))).thenReturn(resultado);
        when(instituciones.obtener(1L)).thenReturn(new InstitucionResponse(1L,"RAICES","Instituto Raíces",
                "Instituto Raíces","Instituto Raíces",null,null,null,null,null,null,null,null,"México",
                null,"America/Mexico_City","MXN",true,null));

        ByteArrayOutputStream xlsx = new ByteArrayOutputStream();
        new ExcelEstadoCuentaCuentaService(consulta).exportar(filtro, xlsx);
        assertThat(xlsx.toByteArray()).startsWith((byte) 0x50, (byte) 0x4b);
        try (var libro = WorkbookFactory.create(new ByteArrayInputStream(xlsx.toByteArray()))) {
            assertThat(libro.getSheet("Resumen").getRow(7+MarcaReporteFinanciero.FILAS).getCell(1).getNumericCellValue()).isEqualTo(1250.0);
            assertThat(libro.getSheet("Estado de cuenta").getRow(1+MarcaReporteFinanciero.FILAS).getCell(4).getStringCellValue())
                    .isEqualTo("Colegiatura septiembre");
        }

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        new JasperReporteFinancieroService(reportes, consulta, instituciones).estadoCuenta(filtro, pdf);
        assertThat(pdf.toByteArray()).startsWith((byte) '%', (byte) 'P', (byte) 'D', (byte) 'F');
        try (var documento = Loader.loadPDF(pdf.toByteArray())) {
            String texto = new PDFTextStripper().getText(documento);
            assertThat(texto).contains("Estado de cuenta financiera", "Colegiatura septiembre", "$1,250.00");
            Path carpeta=Path.of("target","jasper-qa");Files.createDirectories(carpeta);
            Files.write(carpeta.resolve("estado-cuenta.pdf"),pdf.toByteArray());
            ImageIO.write(new PDFRenderer(documento).renderImageWithDPI(0, 144), "png",
                    carpeta.resolve("estado-cuenta-1.png").toFile());
        }
    }
}
