package escuela.admin.service;

import escuela.alumno.dto.response.AlumnoResponse;
import escuela.alumno.dto.response.FichaMedicaAlumnoResponse;
import escuela.alumno.entity.TipoSanguineo;
import escuela.alumno.service.AlumnoService;
import escuela.alumno.service.FichaMedicaAlumnoService;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

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

class JasperExpedienteAlumnoServiceTest {

    private final AlumnoService alumnos = mock(AlumnoService.class);
    private final FichaMedicaAlumnoService fichas = mock(FichaMedicaAlumnoService.class);
    private final InstitucionRepository instituciones = mock(InstitucionRepository.class);
    private final ArchivoRepository archivos = mock(ArchivoRepository.class);
    private final AlmacenamientoArchivo almacenamiento = mock(AlmacenamientoArchivo.class);
    private final JasperExpedienteAlumnoService service = new JasperExpedienteAlumnoService(
            alumnos, fichas, instituciones, archivos, almacenamiento);

    @Test
    void generaFichaGeneralYMedicaLegiblesConLogoInstitucional() throws Exception {
        when(alumnos.obtener(10L)).thenReturn(alumno());
        when(fichas.obtener(10L)).thenReturn(fichaMedica());
        when(instituciones.findById(1L)).thenReturn(Optional.of(institucion()));
        ByteArrayOutputStream ficha = new ByteArrayOutputStream();
        ByteArrayOutputStream medica = new ByteArrayOutputStream();

        service.exportarFicha(10L, ficha);
        service.exportarFichaMedica(10L, medica);

        validarPdf(ficha.toByteArray(), "ficha-alumno.pdf", "FICHA DEL ALUMNO",
                "Ana Sofía Pérez López", "ALU-001", "IDENTIDAD ESCOLAR",
                "CONTACTO Y DOMICILIO", "FIN DE LAS NOTAS");
        validarPdf(medica.toByteArray(), "ficha-medica-alumno.pdf", "FICHA MÉDICA DEL ALUMNO",
                "DATOS PRIVADOS", "A+", "Alergia a la penicilina",
                "CONTACTO Y ACTUACIÓN EN EMERGENCIA", "Laura Pérez");
    }

    private void validarPdf(byte[] contenido, String nombre, String... textos) throws Exception {
        assertThat(contenido).startsWith((byte) '%', (byte) 'P', (byte) 'D', (byte) 'F');
        Path carpeta = Path.of("target", "jasper-qa");
        Files.createDirectories(carpeta);
        Files.write(carpeta.resolve(nombre), contenido);
        try (var documento = Loader.loadPDF(contenido)) {
            String texto = new PDFTextStripper().getText(documento).replaceAll("\\s+", " ");
            assertThat(texto).contains(textos);
            assertThat(documento.getNumberOfPages()).isGreaterThanOrEqualTo(1);
            PDFRenderer renderer = new PDFRenderer(documento);
            for (int pagina = 0; pagina < documento.getNumberOfPages(); pagina++) {
                ImageIO.write(renderer.renderImageWithDPI(pagina, 144), "png",
                        carpeta.resolve(nombre.replace(".pdf", "-" + (pagina + 1) + ".png")).toFile());
            }
        }
    }

    private AlumnoResponse alumno() {
        Instant ahora = Instant.parse("2026-10-02T18:00:00Z");
        String notas = "Seguimiento administrativo sin incidencias. ".repeat(18)
                + "FIN DE LAS NOTAS";
        return new AlumnoResponse(10L, 1L, "ALU-001", "Ana Sofía", "Pérez", "López",
                "PELA180101MJCRPN01", LocalDate.of(2018, 1, 1), "Femenino",
                "Guadalajara, Jalisco", "Mexicana", "33 1234 5678", "familia@example.com",
                "Av. del Aprendizaje", "125", "2", "Centro", "Guadalajara", "Jalisco",
                "44100", "MX", LocalDate.of(2024, 8, 20), notas, true, null,
                new AuditoriaResponse(ahora, 1L, ahora, 1L, 4L));
    }

    private FichaMedicaAlumnoResponse fichaMedica() {
        return new FichaMedicaAlumnoResponse(TipoSanguineo.A_POSITIVO,
                "Alergia a la penicilina", "Asma leve controlada", "Inhalador según indicación",
                "No requiere apoyos adicionales", "Evitar esfuerzo intenso durante crisis",
                "Sin restricciones alimentarias", "IMSS", "12345678901", "Dra. Elena Ruiz",
                "Laura Pérez", "33 9876 5432", "Avisar de inmediato a la familia", true, 2L);
    }

    private Institucion institucion() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        institucion.setNombre("Instituto Raíces");
        institucion.setZonaHoraria("America/Mexico_City");
        return institucion;
    }
}
