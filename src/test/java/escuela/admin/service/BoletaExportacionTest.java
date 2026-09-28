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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BoletaExportacionTest {
    private final BoletaConsultaService consulta = mock(BoletaConsultaService.class);
    private final FiltroBoleta filtro = new FiltroBoleta(1L, 2L, null, null, "", "", 0, 100);

    @Test
    void excelYpdfUsanLosMismosResultadosPublicados() throws Exception {
        BoletaDetalle boleta = new BoletaDetalle(9L, 1L, "Instituto Raíces", "INS-2026-01",
                "IRA-001", "María García López", "Plantel Centro", "2026-2027", "Primero",
                "1 A · MATUTINO", List.of(
                new BoletaCalificacionFila("Primer periodo", "Matemáticas", "9.5", "0.0–10.0 · mínima 6.0", ""),
                new BoletaCalificacionFila("Primer periodo", "Arte", "Destacado", "Evaluación cualitativa", "Excelente trabajo")));
        var pagina = new PageImpl<>(List.of(boleta), PageRequest.of(0, 100), 1);
        when(consulta.exigirExportacion(any(FiltroBoleta.class))).thenReturn(filtro);
        when(consulta.bloqueDetallado(any(FiltroBoleta.class))).thenReturn(pagina);
        when(consulta.detalle(9L)).thenReturn(boleta);

        ByteArrayOutputStream xlsx = new ByteArrayOutputStream();
        new ExcelBoletaService(consulta).exportar(filtro, xlsx);
        assertThat(xlsx.toByteArray()).startsWith((byte) 0x50, (byte) 0x4b);
        try (var libro = WorkbookFactory.create(new ByteArrayInputStream(xlsx.toByteArray()))) {
            assertThat(libro.getSheet("Boletas").getRow(1).getCell(2).getStringCellValue()).isEqualTo("María García López");
            assertThat(libro.getSheet("Boletas").getRow(2).getCell(10).getStringCellValue()).isEqualTo("Destacado");
        }

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        new JasperBoletaService(consulta).individual(9L, pdf);
        assertThat(pdf.toByteArray()).startsWith((byte) '%', (byte) 'P', (byte) 'D', (byte) 'F');
        try (var documento = Loader.loadPDF(pdf.toByteArray())) {
            String texto = new PDFTextStripper().getText(documento);
            assertThat(texto).contains("BOLETA DE CALIFICACIONES", "María García López",
                    "Matemáticas", "9.5", "Arte", "Destacado");
            Path carpeta = Path.of("target", "jasper-qa"); Files.createDirectories(carpeta);
            Files.write(carpeta.resolve("boleta.pdf"), pdf.toByteArray());
            ImageIO.write(new PDFRenderer(documento).renderImageWithDPI(0, 144), "png",
                    carpeta.resolve("boleta-1.png").toFile());
        }
    }
}
