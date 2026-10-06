package escuela.portal.support;

import escuela.admin.dto.*;
import escuela.admin.repository.JdbcReporteFinancieroRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PortalCargosPendientesConsultaTest {
    @Test void filtraSaldoAntesDeContarYPaginarConAlcanceDelAlumno() {
        var jdbc = mock(NamedParameterJdbcTemplate.class);
        when(jdbc.queryForObject(anyString(), any(SqlParameterSource.class), eq(Long.class))).thenReturn(11L);
        var repo = new JdbcReporteFinancieroRepository(jdbc);
        var hoy = LocalDate.of(2026,10,6);
        var filtro = new FiltroEstadoCuentaAlumno(1L,20L,"Ana",null,"POR_PAGAR",hoy,1,10).normalizado(hoy);
        assertThat(filtro.situacion()).isEqualTo("POR_PAGAR");
        repo.estadoCuenta(filtro, new AlcanceReporteFinanciero(true, Set.of()), Instant.parse("2026-10-07T06:00:00Z"));
        var sql = ArgumentCaptor.forClass(String.class);
        var parametros = ArgumentCaptor.forClass(SqlParameterSource.class);
        verify(jdbc).queryForObject(sql.capture(), parametros.capture(), eq(Long.class));
        assertThat(sql.getValue()).contains("(:situacion = 'POR_PAGAR' AND saldo > 0)",
                "estado_registro IN ('CANCELADO','CONVENIDO') THEN 0", "a.id = :alumnoId",
                "a.institucion_id = :institucionId").doesNotContain("LIMIT");
        verify(jdbc).query(sql.capture(), parametros.capture(), any(RowMapper.class));
        assertThat(sql.getValue()).contains("(:situacion = 'POR_PAGAR' AND saldo > 0)", "LIMIT :limite OFFSET :offset");
        assertThat(parametros.getValue().getValue("situacion")).isEqualTo("POR_PAGAR");
        assertThat(parametros.getValue().getValue("alumnoId")).isEqualTo(20L);
        assertThat(parametros.getValue().getValue("institucionId")).isEqualTo(1L);
        assertThat(parametros.getValue().getValue("offset")).isEqualTo(10L);
    }
}
