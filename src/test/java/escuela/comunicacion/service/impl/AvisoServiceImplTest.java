package escuela.comunicacion.service.impl;

import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.comunicacion.entity.*;
import escuela.comunicacion.mapper.AvisoMapper;
import escuela.comunicacion.repository.AvisoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.*;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvisoServiceImplTest {
    private final AvisoRepository repository=mock(AvisoRepository.class);
    private final AlcanceDatosService alcance=mock(AlcanceDatosService.class);
    private final RegistroAuditoriaService auditoria=mock(RegistroAuditoriaService.class);
    private final AvisoServiceImpl service=new AvisoServiceImpl(repository,
            mock(InstitucionRepository.class),mock(PlantelRepository.class),mock(AvisoMapper.class),alcance,auditoria);

    @Test void publicaUnBorradorVigente(){Aviso a=aviso(EstadoAviso.BORRADOR);a.setExpiraEn(Instant.now().plusSeconds(3600));
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(a));when(repository.saveAndFlush(a)).thenReturn(a);
        service.publicar(1L,0L);
        assertThat(a.getEstado()).isEqualTo(EstadoAviso.PUBLICADO);assertThat(a.getPublicadoEn()).isNotNull();
    }

    @Test void impidePublicarUnAvisoVencido(){Aviso a=aviso(EstadoAviso.BORRADOR);a.setExpiraEn(Instant.now().minusSeconds(1));
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(a));
        assertThatThrownBy(()->service.publicar(1L,0L)).isInstanceOf(ReglaNegocioException.class).hasMessageContaining("vencimiento");
        verify(repository,never()).saveAndFlush(any());
    }

    @Test void retiroConservaMotivoYPublicacion(){Aviso a=aviso(EstadoAviso.PUBLICADO);a.setPublicadoEn(Instant.now().minusSeconds(60));
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(a));when(repository.saveAndFlush(a)).thenReturn(a);
        service.retirar(1L,0L,"  Información sustituida  ");
        assertThat(a.getEstado()).isEqualTo(EstadoAviso.RETIRADO);assertThat(a.getMotivoRetiro()).isEqualTo("Información sustituida");
        assertThat(a.getRetiradoEn()).isNotNull();assertThat(a.getPublicadoEn()).isNotNull();
    }

    private Aviso aviso(EstadoAviso estado){Institucion i=new Institucion();i.setId(10L);i.setZonaHoraria("America/Mexico_City");
        Aviso a=new Aviso();a.setId(1L);a.setVersion(0L);a.setInstitucion(i);a.setEstado(estado);a.setTitulo("Aviso");a.setContenido("Contenido");
        when(alcance.alcanceInstitucionalActual(10L)).thenReturn(true);return a;}
}
