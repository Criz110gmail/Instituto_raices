package escuela.seguridad.service.impl;

import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.dto.request.RolRequest;
import escuela.seguridad.entity.Permiso;
import escuela.seguridad.entity.Rol;
import escuela.seguridad.entity.RolPermiso;
import escuela.seguridad.mapper.RolMapper;
import escuela.seguridad.repository.PermisoRepository;
import escuela.seguridad.repository.RolPermisoRepository;
import escuela.seguridad.repository.RolRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RolServiceImplTest {

    private final RolRepository repository = mock(RolRepository.class);
    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private final PermisoRepository permisoRepository = mock(PermisoRepository.class);
    private final RolPermisoRepository rolPermisoRepository = mock(RolPermisoRepository.class);
    private final RolServiceImpl service = new RolServiceImpl(repository, institucionRepository,
            permisoRepository, rolPermisoRepository, new RolMapper());

    @Test
    void conservaHistorialAlCambiarPermisos() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        institucion.setActivo(true);
        Rol rol = new Rol();
        rol.setId(4L);
        rol.setVersion(3L);
        rol.setInstitucion(institucion);
        rol.setActivo(true);
        Permiso anterior = permiso(10L);
        Permiso nuevo = permiso(11L);
        RolPermiso relacionAnterior = new RolPermiso();
        relacionAnterior.setRol(rol);
        relacionAnterior.setPermiso(anterior);
        relacionAnterior.setActivo(true);
        List<RolPermiso> relaciones = new ArrayList<>(List.of(relacionAnterior));
        when(repository.findById(4L)).thenReturn(Optional.of(rol));
        when(repository.saveAndFlush(any(Rol.class))).thenAnswer(i -> i.getArgument(0));
        when(permisoRepository.findAllById(Set.of(11L))).thenReturn(List.of(nuevo));
        when(rolPermisoRepository.findAllByRolIdOrderByPermisoCodigoAsc(4L)).thenReturn(relaciones);

        service.actualizar(4L, new RolRequest(1L, "CAJERO", "Cajero", null, true, 3L), Set.of(11L));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RolPermiso>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolPermisoRepository).saveAllAndFlush(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue()).anySatisfy(r -> {
            assertThat(r.getPermiso().getId()).isEqualTo(10L);
            assertThat(r.isActivo()).isFalse();
        });
        assertThat(captor.getValue()).anySatisfy(r -> {
            assertThat(r.getPermiso().getId()).isEqualTo(11L);
            assertThat(r.isActivo()).isTrue();
        });
    }

    private Permiso permiso(Long id) {
        Permiso permiso = new Permiso();
        permiso.setId(id);
        permiso.setCodigo("P" + id);
        return permiso;
    }
}
