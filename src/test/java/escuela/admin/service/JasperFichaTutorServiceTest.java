package escuela.admin.service;

import escuela.archivo.dto.ArchivoDescarga;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.tutor.dto.response.IdentificacionTutorResponse;
import escuela.tutor.dto.response.PortalTutorCuentaResponse;
import escuela.tutor.dto.response.TutorResponse;
import escuela.tutor.entity.TipoIdentificacionTutor;
import escuela.tutor.service.AccesoPortalTutorService;
import escuela.tutor.service.IdentificacionTutorService;
import escuela.tutor.service.TutorService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JasperFichaTutorServiceTest {

    private final TutorService tutores = mock(TutorService.class);
    private final IdentificacionTutorService identificaciones = mock(IdentificacionTutorService.class);
    private final AccesoPortalTutorService accesos = mock(AccesoPortalTutorService.class);
    private final InstitucionRepository instituciones = mock(InstitucionRepository.class);
    private final ArchivoRepository archivos = mock(ArchivoRepository.class);
    private final AlmacenamientoArchivo almacenamiento = mock(AlmacenamientoArchivo.class);
    private final JasperFichaTutorService service = new JasperFichaTutorService(
            tutores, identificaciones, accesos, instituciones, archivos, almacenamiento);

    @Test
    void generaFichaLegibleConLogoEIdentificacion() throws Exception {
        TutorResponse tutor = tutor();
        IdentificacionTutorResponse identificacion = identificacion();
        byte[] imagen = new ClassPathResource("reports/assets/logo-institucion-ejemplo.png")
                .getInputStream().readAllBytes();
        when(tutores.obtener(10L)).thenReturn(tutor);
        when(identificaciones.actual(10L)).thenReturn(identificacion);
        when(identificaciones.descargar(10L, 20L)).thenReturn(new ArchivoDescarga(
                new ByteArrayResource(imagen), "ine.png", "image/png", imagen.length));
        when(accesos.obtener(10L)).thenReturn(Optional.of(new PortalTutorCuentaResponse(
                30L, "maria.lopez", "maria@example.com", EstadoUsuario.ACTIVO, true, 2L)));
        when(instituciones.findById(1L)).thenReturn(Optional.of(institucion()));
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        service.exportar(10L, salida);

        byte[] contenido = salida.toByteArray();
        assertThat(contenido).startsWith((byte) '%', (byte) 'P', (byte) 'D', (byte) 'F');
        Path carpeta = Path.of("target", "jasper-qa");
        Files.createDirectories(carpeta);
        Files.write(carpeta.resolve("ficha-tutor.pdf"), contenido);
        try (var documento = Loader.loadPDF(contenido)) {
            String texto = new PDFTextStripper().getText(documento).replaceAll("\\s+", " ");
            assertThat(texto).contains("FICHA DEL TUTOR", "María Elena López García",
                    "IDENTIFICACIÓN OFICIAL", "INE", "CONTACTO Y DOMICILIO",
                    "INFORMACIÓN LABORAL", "PORTAL DE FAMILIAS", "maria.lopez");
            PDFRenderer renderer = new PDFRenderer(documento);
            for (int pagina = 0; pagina < documento.getNumberOfPages(); pagina++) {
                ImageIO.write(renderer.renderImageWithDPI(pagina, 144), "png",
                        carpeta.resolve("ficha-tutor-" + (pagina + 1) + ".png").toFile());
            }
        }
    }

    private TutorResponse tutor() {
        Instant ahora = Instant.parse("2026-10-02T18:00:00Z");
        return new TutorResponse(10L, 1L, null, null, "María Elena", "López", "García",
                "33 1234 5678", "33 8765 4321", "maria@example.com",
                LocalDate.of(1988, 6, 14), "Av. de las Familias", "125", "3",
                "Centro", "Guadalajara", "Jalisco", "44100", "MX",
                "Arquitecta", "Estudio Horizonte", "33 1111 2222", true,
                new AuditoriaResponse(ahora, 1L, ahora, 1L, 4L));
    }

    private IdentificacionTutorResponse identificacion() {
        return new IdentificacionTutorResponse(20L, TipoIdentificacionTutor.INE, "INE",
                "ine.png", "image/png", 125_000,
                Instant.parse("2026-09-15T16:30:00Z"), null, true);
    }

    private Institucion institucion() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        institucion.setNombre("Instituto Raíces");
        institucion.setZonaHoraria("America/Mexico_City");
        return institucion;
    }
}
