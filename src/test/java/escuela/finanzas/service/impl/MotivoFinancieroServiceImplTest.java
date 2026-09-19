package escuela.finanzas.service.impl;

import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.MotivoFinancieroRequest;
import escuela.finanzas.entity.MotivoFinanciero;
import escuela.finanzas.entity.NaturalezaMotivoFinanciero;
import escuela.finanzas.mapper.MotivoFinancieroMapper;
import escuela.finanzas.repository.MotivoFinancieroRepository;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MotivoFinancieroServiceImplTest {
    private final MotivoFinancieroRepository motivos = mock(MotivoFinancieroRepository.class);
    private final InstitucionRepository instituciones = mock(InstitucionRepository.class);
    private final MotivoFinancieroServiceImpl service = new MotivoFinancieroServiceImpl(
            motivos, instituciones, new MotivoFinancieroMapper());

    @Test
    void creaMotivoNormalizandoCodigoYCategoria() {
        Institucion institucion = institucion();
        when(instituciones.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));
        when(motivos.saveAndFlush(any())).thenAnswer(i -> { MotivoFinanciero m=i.getArgument(0);m.setId(3L);return m; });
        var respuesta = service.crear(request(" servicios ", NaturalezaMotivoFinanciero.EGRESO, true, null));
        assertThat(respuesta.codigo()).isEqualTo("SERVICIOS");
        assertThat(respuesta.categoria()).isEqualTo("OPERACION");
    }

    @Test
    void noPermiteDesactivarMotivoReservadoDeCobros() {
        MotivoFinanciero motivo = motivoReservado();
        when(motivos.findById(5L)).thenReturn(Optional.of(motivo));
        assertThatThrownBy(() -> service.desactivar(5L, 0L))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("reservado");
    }

    @Test
    void noPermiteConvertirMotivoReservadoEnEgreso() {
        MotivoFinanciero motivo = motivoReservado();
        when(motivos.findById(5L)).thenReturn(Optional.of(motivo));
        assertThatThrownBy(() -> service.actualizar(5L,
                request("COBROS_ESCOLARES", NaturalezaMotivoFinanciero.EGRESO, true, 0L)))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("reservado");
    }

    @Test
    void noPermiteDesactivarMotivoReservadoDeTraspasos() {
        MotivoFinanciero motivo = motivoReservado();
        motivo.setCodigo("TRASPASO_INTERNO");
        motivo.setNaturaleza(NaturalezaMotivoFinanciero.AMBOS);
        when(motivos.findById(5L)).thenReturn(Optional.of(motivo));
        assertThatThrownBy(() -> service.desactivar(5L, 0L))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("reservado");
    }

    @Test
    void noPermiteCambiarNaturalezaDelMotivoReservadoDeDevoluciones() {
        MotivoFinanciero motivo = motivoReservado();
        motivo.setCodigo("DEVOLUCION_PAGO");
        motivo.setNaturaleza(NaturalezaMotivoFinanciero.EGRESO);
        when(motivos.findById(5L)).thenReturn(Optional.of(motivo));
        assertThatThrownBy(() -> service.actualizar(5L,
                request("DEVOLUCION_PAGO", NaturalezaMotivoFinanciero.INGRESO, true, 0L)))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("reservado");
    }

    private Institucion institucion() {
        Institucion institucion = new Institucion(); institucion.setId(1L); institucion.setNombre("Raíces");
        institucion.setActivo(true); return institucion;
    }

    private MotivoFinanciero motivoReservado() {
        MotivoFinanciero motivo = new MotivoFinanciero(); motivo.setId(5L); motivo.setVersion(0L);
        motivo.setInstitucion(institucion()); motivo.setCodigo("COBROS_ESCOLARES");
        motivo.setNombre("Cobros escolares"); motivo.setNaturaleza(NaturalezaMotivoFinanciero.INGRESO);
        motivo.setCategoria("COBRANZA"); motivo.setActivo(true); return motivo;
    }

    private MotivoFinancieroRequest request(String codigo, NaturalezaMotivoFinanciero naturaleza,
                                             boolean activo, Long version) {
        return new MotivoFinancieroRequest(1L, codigo, "Servicios", naturaleza,
                " operacion ", activo, version);
    }
}
