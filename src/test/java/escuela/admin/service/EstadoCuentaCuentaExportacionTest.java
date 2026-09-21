package escuela.admin.service;

import escuela.admin.dto.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
    private final FiltroEstadoCuentaCuenta filtro = new FiltroEstadoCuentaCuenta(1L, 2L,
            "CTA · Caja", "MENSUAL", 2026, 9, 0, 100);

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

        ByteArrayOutputStream xlsx = new ByteArrayOutputStream();
        new ExcelEstadoCuentaCuentaService(consulta).exportar(filtro, xlsx);
        assertThat(xlsx.toByteArray()).startsWith((byte) 0x50, (byte) 0x4b);
        try (var libro = WorkbookFactory.create(new ByteArrayInputStream(xlsx.toByteArray()))) {
            assertThat(libro.getSheet("Resumen").getRow(7).getCell(1).getNumericCellValue()).isEqualTo(1250.0);
            assertThat(libro.getSheet("Estado de cuenta").getRow(1).getCell(4).getStringCellValue())
                    .isEqualTo("Colegiatura septiembre");
        }

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        new PdfEstadoCuentaCuentaService(consulta).exportar(filtro, pdf);
        assertThat(pdf.toByteArray()).startsWith((byte) '%', (byte) 'P', (byte) 'D', (byte) 'F');
        try (var documento = Loader.loadPDF(pdf.toByteArray())) {
            String texto = new PDFTextStripper().getText(documento);
            assertThat(texto).contains("Estado de cuenta interno", "Colegiatura septiembre", "1,250.00");
            Path destino = Path.of("target", "estado-cuenta-qa.png");
            Files.createDirectories(destino.getParent());
            ImageIO.write(new PDFRenderer(documento).renderImageWithDPI(0, 120), "png", destino.toFile());
        }
    }
}
