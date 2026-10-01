package escuela.cobranza.service;

import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.cobranza.dto.request.CuotaAlumnoRequest;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.dto.response.CuotaAlumnoResponse;
import escuela.cobranza.mapper.CargoMapper;
import escuela.cobranza.mapper.CuotaAlumnoMapper;
import escuela.cobranza.repository.CargoRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.inscripcion.repository.InscripcionRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CobranzaInscripcionServiceTest {
    private final CuotaAlumnoService cuotaService = mock(CuotaAlumnoService.class);
    private final CargoService cargoService = mock(CargoService.class);
    private final CobranzaInscripcionService service = new CobranzaInscripcionService(
            mock(InscripcionRepository.class), mock(CuotaAlumnoRepository.class),
            mock(CargoRepository.class), mock(AlumnoTutorRepository.class),
            mock(CuotaAlumnoMapper.class), mock(CargoMapper.class), cuotaService, cargoService);

    @Test
    void creaCuotaYGeneraCargoEnUnSoloFlujoAsistido() {
        CuotaAlumnoRequest request = mock(CuotaAlumnoRequest.class);
        CuotaAlumnoResponse cuota = mock(CuotaAlumnoResponse.class);
        CargoResponse cargo = mock(CargoResponse.class);
        when(cuota.id()).thenReturn(44L);
        when(cuotaService.crear(request)).thenReturn(cuota);
        when(cargoService.generarCargoUnico(44L)).thenReturn(cargo);

        var resultado = service.crearCuota(request, true);

        assertThat(resultado.cuota()).isSameAs(cuota);
        assertThat(resultado.cargo()).isSameAs(cargo);
        verify(cargoService).generarCargoUnico(44L);
    }

    @Test
    void permitePrepararCuotaSinGenerarTodaviaElCargo() {
        CuotaAlumnoRequest request = mock(CuotaAlumnoRequest.class);
        CuotaAlumnoResponse cuota = mock(CuotaAlumnoResponse.class);
        when(cuotaService.crear(request)).thenReturn(cuota);

        var resultado = service.crearCuota(request, false);

        assertThat(resultado.cargo()).isNull();
        verifyNoInteractions(cargoService);
    }
}
