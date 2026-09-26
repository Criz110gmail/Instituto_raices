package escuela.tutor.service.impl;

import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.tutor.entity.TipoIdentificacionTutor;
import escuela.tutor.entity.Tutor;
import escuela.tutor.entity.TutorIdentificacion;
import escuela.tutor.repository.TutorIdentificacionRepository;
import escuela.tutor.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IdentificacionTutorServiceImplTest {
    private final TutorRepository tutorRepository = mock(TutorRepository.class);
    private final TutorIdentificacionRepository identificacionRepository =
            mock(TutorIdentificacionRepository.class);
    private final ArchivoRepository archivoRepository = mock(ArchivoRepository.class);
    private final AlmacenamientoArchivo almacenamiento = mock(AlmacenamientoArchivo.class);
    private final IdentificacionTutorServiceImpl service = new IdentificacionTutorServiceImpl(
            tutorRepository, identificacionRepository, archivoRepository, almacenamiento);
    private Tutor tutor;

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion(); institucion.setId(1L);
        tutor = new Tutor(); tutor.setId(10L); tutor.setInstitucion(institucion); tutor.setActivo(true);
        when(tutorRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(tutor));
        when(archivoRepository.saveAndFlush(any(Archivo.class))).thenAnswer(invocacion -> {
            Archivo archivo = invocacion.getArgument(0); archivo.setId(20L); return archivo;
        });
        when(identificacionRepository.saveAndFlush(any(TutorIdentificacion.class)))
                .thenAnswer(invocacion -> {
                    TutorIdentificacion identificacion = invocacion.getArgument(0);
                    identificacion.setId(30L);
                    identificacion.setCreadoEn(Instant.parse("2026-09-26T12:00:00Z"));
                    return identificacion;
                });
        when(identificacionRepository.findFirstByTutorIdAndRetiradaEnIsNull(10L))
                .thenReturn(Optional.empty());
    }

    @Test
    void guardaPdfPrivadoConTipoYMetadatos() {
        MockMultipartFile pdf = pdf("ine-christian.pdf");

        var respuesta = service.asignar(10L, TipoIdentificacionTutor.INE, pdf);

        var archivo = org.mockito.ArgumentCaptor.forClass(Archivo.class);
        verify(archivoRepository).saveAndFlush(archivo.capture());
        assertThat(archivo.getValue().getTipoMime()).isEqualTo("application/pdf");
        assertThat(archivo.getValue().getNombreOriginal()).isEqualTo("ine-christian.pdf");
        assertThat(archivo.getValue().getChecksumSha256()).hasSize(64);
        assertThat(respuesta.tipo()).isEqualTo(TipoIdentificacionTutor.INE);
        assertThat(respuesta.actual()).isTrue();
        verify(almacenamiento).guardar(org.mockito.ArgumentMatchers.contains(
                "/tutores/10/identificaciones/"), any());
    }

    @Test
    void rechazaUnArchivoQueSoloDeclaraSerPdf() {
        MockMultipartFile falso = new MockMultipartFile("archivo", "ine.pdf",
                "application/pdf", "no es un pdf".getBytes());

        assertThatThrownBy(() -> service.asignar(10L, TipoIdentificacionTutor.INE, falso))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("PDF, JPEG o PNG válido");
        verify(almacenamiento, never()).guardar(any(), any());
    }

    @Test
    void rechazaUnaIdentificacionMayorA10Mb() {
        byte[] contenido = new byte[(int) IdentificacionTutorServiceImpl.TAMANO_MAXIMO + 1];
        MockMultipartFile grande = new MockMultipartFile("archivo", "ine.pdf",
                "application/pdf", contenido);

        assertThatThrownBy(() -> service.asignar(10L, TipoIdentificacionTutor.INE, grande))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("10 MB");
        verify(archivoRepository, never()).saveAndFlush(any());
    }

    @Test
    void reemplazaLaIdentificacionConservandoElHistorial() {
        TutorIdentificacion anterior = new TutorIdentificacion();
        when(identificacionRepository.findFirstByTutorIdAndRetiradaEnIsNull(10L))
                .thenReturn(Optional.of(anterior));

        service.asignar(10L, TipoIdentificacionTutor.PASAPORTE, pdf("pasaporte.pdf"));

        assertThat(anterior.getRetiradaEn()).isNotNull();
        verify(almacenamiento, never()).eliminarSiExiste(any());
    }

    @Test
    void retiraLaVigenteSinBorrarElArchivoPrivado() {
        TutorIdentificacion actual = new TutorIdentificacion();
        when(identificacionRepository.findFirstByTutorIdAndRetiradaEnIsNull(10L))
                .thenReturn(Optional.of(actual));

        service.retirar(10L);

        assertThat(actual.getRetiradaEn()).isNotNull();
        verify(almacenamiento, never()).eliminarSiExiste(any());
    }

    @Test
    void impideDescargarUnaIdentificacionBloqueada() {
        when(tutorRepository.findById(10L)).thenReturn(Optional.of(tutor));
        Archivo archivo = new Archivo(); archivo.setEstado(EstadoArchivo.BLOQUEADO);
        TutorIdentificacion identificacion = new TutorIdentificacion(); identificacion.setArchivo(archivo);
        when(identificacionRepository.findByIdAndTutorId(30L, 10L))
                .thenReturn(Optional.of(identificacion));

        assertThatThrownBy(() -> service.descargar(10L, 30L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no está disponible");
        verify(almacenamiento, never()).abrir(any());
    }

    private MockMultipartFile pdf(String nombre) {
        return new MockMultipartFile("archivo", nombre, "application/pdf",
                "%PDF-1.4\ncontenido".getBytes());
    }
}
