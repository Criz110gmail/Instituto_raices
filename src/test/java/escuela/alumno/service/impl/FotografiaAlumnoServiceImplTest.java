package escuela.alumno.service.impl;

import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.AlumnoFotografia;
import escuela.alumno.repository.AlumnoFotografiaRepository;
import escuela.alumno.repository.AlumnoRepository;
import escuela.archivo.entity.Archivo;
import escuela.archivo.entity.EstadoArchivo;
import escuela.archivo.imagen.ImagenOptimizada;
import escuela.archivo.imagen.ProcesadorFotografia;
import escuela.archivo.repository.ArchivoRepository;
import escuela.archivo.storage.AlmacenamientoArchivo;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
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

class FotografiaAlumnoServiceImplTest {

    private final AlumnoRepository alumnoRepository = mock(AlumnoRepository.class);
    private final AlumnoFotografiaRepository fotografiaRepository = mock(AlumnoFotografiaRepository.class);
    private final ArchivoRepository archivoRepository = mock(ArchivoRepository.class);
    private final AlmacenamientoArchivo almacenamiento = mock(AlmacenamientoArchivo.class);
    private final ProcesadorFotografia procesadorFotografia = mock(ProcesadorFotografia.class);
    private final FotografiaAlumnoServiceImpl service = new FotografiaAlumnoServiceImpl(
            alumnoRepository, fotografiaRepository, archivoRepository, almacenamiento, procesadorFotografia);
    private Alumno alumno;

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        alumno = new Alumno();
        alumno.setId(10L);
        alumno.setInstitucion(institucion);
        alumno.setActivo(true);
        when(alumnoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(alumno));
        when(archivoRepository.saveAndFlush(any(Archivo.class))).thenAnswer(invocacion -> {
            Archivo archivo = invocacion.getArgument(0);
            archivo.setId(20L);
            return archivo;
        });
        when(fotografiaRepository.saveAndFlush(any(AlumnoFotografia.class))).thenAnswer(invocacion -> {
            AlumnoFotografia fotografia = invocacion.getArgument(0);
            fotografia.setId(30L);
            fotografia.setCreadoEn(Instant.parse("2026-09-13T00:00:00Z"));
            return fotografia;
        });
        when(fotografiaRepository.findFirstByAlumnoIdAndRetiradaEnIsNull(10L))
                .thenReturn(Optional.empty());
        when(procesadorFotografia.procesar(any())).thenReturn(new ImagenOptimizada(
                new byte[]{1, 2, 3}, "foto alumna.jpg", "image/jpeg", "a".repeat(64), 2_000_000));
    }

    @Test
    void guardaVersionOptimizadaYMetadatos() throws Exception {
        MockMultipartFile carga = png("foto alumna.png");

        var respuesta = service.asignar(10L, carga);

        var archivo = org.mockito.ArgumentCaptor.forClass(Archivo.class);
        verify(archivoRepository).saveAndFlush(archivo.capture());
        assertThat(archivo.getValue().getTipoMime()).isEqualTo("image/jpeg");
        assertThat(archivo.getValue().getNombreOriginal()).isEqualTo("foto alumna.jpg");
        assertThat(archivo.getValue().getTamanoBytes()).isEqualTo(3L);
        assertThat(archivo.getValue().getChecksumSha256()).hasSize(64);
        assertThat(alumno.getFotografiaArchivo()).isSameAs(archivo.getValue());
        assertThat(respuesta.actual()).isTrue();
        verify(almacenamiento).guardar(any(), any());
    }

    @Test
    void reemplazaLaActualConservandoSuRelacionHistorica() throws Exception {
        AlumnoFotografia anterior = new AlumnoFotografia();
        when(fotografiaRepository.findFirstByAlumnoIdAndRetiradaEnIsNull(10L))
                .thenReturn(Optional.of(anterior));

        service.asignar(10L, png("nueva.png"));

        assertThat(anterior.getRetiradaEn()).isNotNull();
        verify(fotografiaRepository).saveAndFlush(anterior);
        verify(almacenamiento, never()).eliminarSiExiste(any());
    }

    @Test
    void retiraLaActualSinBorrarElArchivoPrivado() {
        Archivo archivo = new Archivo();
        alumno.setFotografiaArchivo(archivo);
        AlumnoFotografia actual = new AlumnoFotografia();
        actual.setArchivo(archivo);
        when(fotografiaRepository.findFirstByAlumnoIdAndRetiradaEnIsNull(10L))
                .thenReturn(Optional.of(actual));

        service.retirar(10L);

        assertThat(actual.getRetiradaEn()).isNotNull();
        assertThat(alumno.getFotografiaArchivo()).isNull();
        verify(almacenamiento, never()).eliminarSiExiste(any());
    }

    @Test
    void impideDescargarUnArchivoBloqueado() {
        when(alumnoRepository.findById(10L)).thenReturn(Optional.of(alumno));
        Archivo archivo = new Archivo();
        archivo.setEstado(EstadoArchivo.BLOQUEADO);
        AlumnoFotografia fotografia = new AlumnoFotografia();
        fotografia.setArchivo(archivo);
        when(fotografiaRepository.findByIdAndAlumnoId(30L, 10L))
                .thenReturn(Optional.of(fotografia));

        assertThatThrownBy(() -> service.descargar(10L, 30L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no está disponible");
        verify(almacenamiento, never()).abrir(any());
    }

    private MockMultipartFile png(String nombre) {
        return new MockMultipartFile("archivo", nombre, "image/png", new byte[]{1, 2});
    }
}
