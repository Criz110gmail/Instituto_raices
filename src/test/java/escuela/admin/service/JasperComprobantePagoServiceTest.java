package escuela.admin.service;

import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.AplicacionPagoResponse;
import escuela.finanzas.dto.response.MovimientoFinancieroResponse;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.EstadoPago;
import escuela.finanzas.entity.MetodoPago;
import escuela.finanzas.entity.OrigenRegistroPago;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JasperComprobantePagoServiceTest {
    private final InstitucionRepository instituciones = mock(InstitucionRepository.class);
    private final ArchivoRepository archivos = mock(ArchivoRepository.class);
    private final AlmacenamientoArchivo almacenamiento = mock(AlmacenamientoArchivo.class);
    private final JasperComprobantePagoService servicio =
            new JasperComprobantePagoService(instituciones, archivos, almacenamiento);

    @Test
    void generaUnPdfLegibleConIdentidadAplicacionesYTotales() throws Exception {
        Institucion institucion = institucion();
        when(instituciones.findById(1L)).thenReturn(Optional.of(institucion));
        PagoResponse pago = pago(EstadoPago.VALIDADO);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        servicio.exportar(pago, salida);

        assertThat(salida.toByteArray()).startsWith((byte) '%', (byte) 'P', (byte) 'D', (byte) 'F');
        try (var documento = Loader.loadPDF(salida.toByteArray())) {
            String texto = new PDFTextStripper().getText(documento);
            assertThat(texto).contains("COMPROBANTE DE PAGO", "Instituto Raíces",
                    "PAG-2026-0042", "María García López", "IRA-001",
                    "Colegiatura septiembre", "$2,500.00 MXN", "PAGO VALIDADO");
            assertThat(documento.getNumberOfPages()).isEqualTo(1);
            Path carpeta = Path.of("target", "jasper-qa");
            Files.createDirectories(carpeta);
            Files.write(carpeta.resolve("comprobante-pago.pdf"), salida.toByteArray());
            ImageIO.write(new PDFRenderer(documento).renderImageWithDPI(0, 144), "png",
                    carpeta.resolve("comprobante-pago-1.png").toFile());
        }
    }

    @Test
    void impideEmitirComprobanteOficialAntesDeValidarElPago() {
        assertThatThrownBy(() -> servicio.exportar(pago(EstadoPago.PENDIENTE_VALIDACION),
                new ByteArrayOutputStream()))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("sólo está disponible para pagos validados");
    }

    private Institucion institucion() {
        Institucion i = new Institucion();
        i.setId(1L);
        i.setCodigo("IRA");
        i.setNombre("Instituto Raíces");
        i.setRazonSocial("Instituto Raíces Educativas A.C.");
        i.setRfc("IRE260101AB1");
        i.setDomicilioFiscal("Av. del Aprendizaje 125");
        i.setCiudad("Guadalajara");
        i.setEstado("Jalisco");
        i.setCodigoPostal("44100");
        i.setZonaHoraria("America/Mexico_City");
        return i;
    }

    private PagoResponse pago(EstadoPago estado) {
        Instant fechaPago = Instant.parse("2026-09-18T16:30:00Z");
        Instant fechaValidacion = Instant.parse("2026-09-18T17:00:00Z");
        return new PagoResponse(42L, 1L, "Instituto Raíces", 2L, "Plantel Centro",
                7L, "Carlos García", "Carlos García", "PAG-2026-0042", fechaPago,
                new BigDecimal("2500.00"), "MXN", MetodoPago.TRANSFERENCIA, estado,
                OrigenRegistroPago.PORTAL_FAMILIAR, "carlos.garcia", 8L, "Cuenta BBVA",
                8L, "Cuenta BBVA terminación 1234", "SPEI-778899", "Colegiatura",
                estado == EstadoPago.VALIDADO ? fechaValidacion : null,
                estado == EstadoPago.VALIDADO ? "administrador" : null, null,
                List.of(), List.of(), List.of(new AplicacionPagoResponse(3L, 10L,
                        "María García López", "IRA-001", "Colegiatura",
                        "Colegiatura septiembre", new BigDecimal("2500.00"), "MXN",
                        fechaValidacion)), new BigDecimal("2500.00"), BigDecimal.ZERO,
                new BigDecimal("2500.00"), BigDecimal.ZERO, BigDecimal.ZERO,
                new MovimientoFinancieroResponse(20L, 8L, "Cuenta BBVA", fechaValidacion,
                        15L, new BigDecimal("2500.00"), "MXN", new BigDecimal("10000.00"),
                        new BigDecimal("12500.00")),
                new AuditoriaResponse(fechaPago, 7L, fechaValidacion, 1L, 1L));
    }
}
