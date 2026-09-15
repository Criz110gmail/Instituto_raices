package escuela.finanzas.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.CuentaFinancieraRequest;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.CuentaFinancieraMapper;
import escuela.finanzas.repository.CuentaFinancieraRepository;
import escuela.finanzas.repository.MovimientoFinancieroRepository;
import escuela.institucion.entity.*;
import escuela.institucion.repository.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CuentaFinancieraServiceImplTest {

    private final CuentaFinancieraRepository repository = mock(CuentaFinancieraRepository.class);
    private final InstitucionRepository instituciones = mock(InstitucionRepository.class);
    private final PlantelRepository planteles = mock(PlantelRepository.class);
    private final MovimientoFinancieroRepository movimientos = mock(MovimientoFinancieroRepository.class);
    private final CuentaFinancieraServiceImpl service = new CuentaFinancieraServiceImpl(
            repository, instituciones, planteles, movimientos, new CuentaFinancieraMapper());

    @Test
    void creaCuentaBancariaNormalizadaYLigadaAlPlantel() {
        Institucion institucion = institucion(1L);
        Plantel plantel = plantel(2L, institucion, true);
        when(instituciones.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));
        when(planteles.findById(2L)).thenReturn(Optional.of(plantel));
        when(repository.saveAndFlush(any())).thenAnswer(invocacion -> {
            CuentaFinanciera cuenta = invocacion.getArgument(0);
            cuenta.setId(10L);
            cuenta.setVersion(0L);
            return cuenta;
        });

        var respuesta = service.crear(request(1L, 2L, " bbva-cobros ", TipoCuentaFinanciera.BANCO,
                " BBVA ", "0123456789", "012345678901234567", "mxn", LocalDate.now()));

        assertThat(respuesta.id()).isEqualTo(10L);
        assertThat(respuesta.codigo()).isEqualTo("BBVA-COBROS");
        assertThat(respuesta.bancoNombre()).isEqualTo("BBVA");
        assertThat(respuesta.plantelId()).isEqualTo(2L);
        assertThat(respuesta.saldoInicial()).isEqualByComparingTo("1500.00");
    }

    @Test
    void rechazaCodigoDuplicadoDentroDeLaInstitucion() {
        Institucion institucion = institucion(1L);
        when(instituciones.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));
        when(repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(1L, "CAJA", 0L))
                .thenReturn(true);

        assertThatThrownBy(() -> service.crear(request(1L, null, "caja",
                TipoCuentaFinanciera.CAJA, null, null, null, "MXN", LocalDate.now())))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("ese código");
    }

    @Test
    void rechazaPlantelDeOtraInstitucion() {
        Institucion institucion = institucion(1L);
        when(instituciones.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));
        when(planteles.findById(2L)).thenReturn(Optional.of(plantel(2L, institucion(9L), true)));

        assertThatThrownBy(() -> service.crear(request(1L, 2L, "BANCO",
                TipoCuentaFinanciera.BANCO, "Banco", "1234", null, "MXN", LocalDate.now())))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no pertenece");
    }

    @Test
    void rechazaDatosBancariosEnCaja() {
        Institucion institucion = institucion(1L);
        when(instituciones.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));

        assertThatThrownBy(() -> service.crear(request(1L, null, "CAJA",
                TipoCuentaFinanciera.CAJA, "Banco", "1234", null, "MXN", LocalDate.now())))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("caja no debe");
    }

    @Test
    void exigeBancoEIdentificadorParaCuentaBancaria() {
        Institucion institucion = institucion(1L);
        when(instituciones.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));

        assertThatThrownBy(() -> service.crear(request(1L, null, "BANCO",
                TipoCuentaFinanciera.BANCO, null, null, null, "MXN", LocalDate.now())))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("requiere banco");
    }

    @Test
    void protegeMonedaYFechaInicial() {
        Institucion institucion = institucion(1L);
        when(instituciones.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));

        assertThatThrownBy(() -> service.crear(request(1L, null, "BANCO",
                TipoCuentaFinanciera.BANCO, "Banco", "1234", null, "USD", LocalDate.now())))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("moneda predeterminada");

        assertThatThrownBy(() -> service.crear(request(1L, null, "BANCO",
                TipoCuentaFinanciera.BANCO, "Banco", "1234", null, "MXN", LocalDate.now().plusDays(1))))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("futuro");
    }

    @Test
    void desactivaConBloqueoYVersion() {
        CuentaFinanciera cuenta = new CuentaFinanciera();
        cuenta.setId(10L);
        cuenta.setVersion(3L);
        cuenta.setActivo(true);
        when(repository.findByIdForUpdate(10L)).thenReturn(Optional.of(cuenta));

        service.desactivar(10L, 3L);

        assertThat(cuenta.isActivo()).isFalse();
    }

    private CuentaFinancieraRequest request(Long institucionId, Long plantelId, String codigo,
                                             TipoCuentaFinanciera tipo, String banco,
                                             String numero, String clabe, String moneda,
                                             LocalDate fecha) {
        return new CuentaFinancieraRequest(institucionId, plantelId, codigo, " Cuenta principal ",
                tipo, banco, " Instituto Raíces ", numero, clabe, moneda,
                new BigDecimal("1500.00"), fecha, true, null);
    }

    private Institucion institucion(Long id) {
        Institucion institucion = new Institucion();
        institucion.setId(id);
        institucion.setNombre("Instituto Raíces");
        institucion.setMonedaPredeterminada("MXN");
        institucion.setActivo(true);
        return institucion;
    }

    private Plantel plantel(Long id, Institucion institucion, boolean activo) {
        Plantel plantel = new Plantel();
        plantel.setId(id);
        plantel.setNombre("Centro");
        plantel.setInstitucion(institucion);
        plantel.setActivo(activo);
        return plantel;
    }
}
