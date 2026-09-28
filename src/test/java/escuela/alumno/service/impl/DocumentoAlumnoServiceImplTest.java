package escuela.alumno.service.impl;

import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.AlumnoDocumento;
import escuela.alumno.entity.TipoDocumentoAlumno;
import escuela.alumno.repository.AlumnoDocumentoRepository;
import escuela.alumno.repository.AlumnoRepository;
import escuela.archivo.entity.Archivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentoAlumnoServiceImplTest {
    private final AlumnoRepository alumnoRepository = mock(AlumnoRepository.class);
    private final AlumnoDocumentoRepository documentoRepository = mock(AlumnoDocumentoRepository.class);
    private final ArchivoRepository archivoRepository = mock(ArchivoRepository.class);
    private final AlmacenamientoArchivo almacenamiento = mock(AlmacenamientoArchivo.class);
    private final DocumentoAlumnoServiceImpl service = new DocumentoAlumnoServiceImpl(
            alumnoRepository, documentoRepository, archivoRepository, almacenamiento);
    private Alumno alumno;

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion(); institucion.setId(1L);
        alumno = new Alumno(); alumno.setId(10L); alumno.setInstitucion(institucion); alumno.setActivo(true);
        when(alumnoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(alumno));
        when(archivoRepository.saveAndFlush(any(Archivo.class))).thenAnswer(invocacion -> {
            Archivo archivo = invocacion.getArgument(0); archivo.setId(20L); return archivo;
        });
        when(documentoRepository.saveAndFlush(any(AlumnoDocumento.class))).thenAnswer(invocacion -> {
            AlumnoDocumento documento = invocacion.getArgument(0);
            documento.setId(30L); documento.setVersion(0L);
            documento.setCreadoEn(Instant.parse("2026-09-27T12:00:00Z"));
            return documento;
        });
    }

    @Test
    void guardaPdfPrivadoConClasificacionYVigencia() {
        MockMultipartFile pdf = pdf("acta.pdf");

        var respuesta = service.agregar(10L, TipoDocumentoAlumno.ACTA_NACIMIENTO,
                "Copia certificada", LocalDate.of(2026, 9, 1), null, pdf);

        var documento = org.mockito.ArgumentCaptor.forClass(AlumnoDocumento.class);
        verify(documentoRepository).saveAndFlush(documento.capture());
        assertThat(documento.getValue().getDescripcion()).isEqualTo("Copia certificada");
        assertThat(respuesta.tipoEtiqueta()).isEqualTo("Acta de nacimiento");
        assertThat(respuesta.vigente()).isTrue();
        verify(almacenamiento).guardar(org.mockito.ArgumentMatchers.contains(
                "/alumnos/10/documentos/"), any());
    }

    @Test
    void rechazaArchivoQueSoloDeclaraSerPdf() {
        MockMultipartFile falso = new MockMultipartFile("archivo", "acta.pdf",
                "application/pdf", "contenido falso".getBytes());

        assertThatThrownBy(() -> service.agregar(10L, TipoDocumentoAlumno.ACTA_NACIMIENTO,
                null, null, null, falso))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("PDF, JPEG o PNG válido");
        verify(almacenamiento, never()).guardar(any(), any());
    }

    @Test
    void rechazaVigenciaAnteriorAFechaDocumento() {
        assertThatThrownBy(() -> service.agregar(10L, TipoDocumentoAlumno.AUTORIZACION,
                null, LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 1), pdf("permiso.pdf")))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("vigencia");
        verify(almacenamiento, never()).guardar(any(), any());
    }

    @Test
    void retiraDocumentoSinBorrarArchivo() {
        AlumnoDocumento documento = new AlumnoDocumento();
        documento.setId(30L); documento.setVersion(2L);
        when(documentoRepository.findByIdAndAlumnoId(30L, 10L)).thenReturn(Optional.of(documento));

        service.retirar(10L, 30L, 2L);

        assertThat(documento.getRetiradoEn()).isNotNull();
        verify(almacenamiento, never()).eliminarSiExiste(any());
    }

    private MockMultipartFile pdf(String nombre) {
        return new MockMultipartFile("archivo", nombre, "application/pdf",
                "%PDF-1.4\ncontenido".getBytes());
    }
}

