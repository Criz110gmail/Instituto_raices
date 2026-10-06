package escuela.admin.service;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.service.GuiaProcesosService.*;
import escuela.seguridad.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuiaProcesosServiceTest {
    private final NamedParameterJdbcTemplate jdbc=mock(NamedParameterJdbcTemplate.class);
    private final GuiaProcesosService service=new GuiaProcesosService(jdbc);
    @Test void deniegaSinPermisoSinUsuarioYRecuperacionAntesDeConsultar() {
        assertThatThrownBy(()->service.listar(null,new Filtro(null,null,null,0,25))).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(()->service.listar(principal(false),new Filtro(null,null,null,0,25))).isInstanceOf(AccessDeniedException.class);
        var recuperacion=new UsuarioPrincipal(null,null,Set.of(),true,true,"bootstrap","",List.of());
        assertThatThrownBy(()->service.obtener(recuperacion,"transferencia-dos-hijos")).isInstanceOf(AccessDeniedException.class);
        verifyNoInteractions(jdbc);
    }
    @Test void listaFiltraPermisosAntesDeContarYPaginarYSinCargarPasos() {
        when(jdbc.queryForObject(anyString(),any(SqlParameterSource.class),eq(Long.class))).thenReturn(0L);
        service.listar(principal(true),new Filtro("  comprobante  ","Cobranza","CONFIRMADA",2,10));
        var sql=ArgumentCaptor.forClass(String.class);var p=ArgumentCaptor.forClass(SqlParameterSource.class);
        verify(jdbc).queryForObject(sql.capture(),p.capture(),eq(Long.class));
        assertThat(sql.getValue()).contains("NOT EXISTS", "p.codigo NOT IN (:permisos)","g.categoria=:categoria", "g.estado=:estado");
        verify(jdbc).query(sql.capture(),p.capture(),any(RowMapper.class));
        assertThat(sql.getValue()).contains("LIMIT :limite OFFSET :offset");
        assertThat(p.getValue().getValue("q")).isEqualTo("comprobante");
        assertThat(p.getValue().getValue("offset")).isEqualTo(20L);
        assertThat(p.getValue().getValue("permisos")).isEqualTo(List.of("GUIA_PROCESOS_CONSULTAR"));
    }
    @Test void accesoDirectoAUnaGuiaNoDisponibleNoConsultaSusPasos() {
        assertThatThrownBy(()->service.obtener(principal(true),"otra-guia")).isInstanceOf(AccessDeniedException.class);
        verify(jdbc,times(1)).query(anyString(),any(SqlParameterSource.class),any(RowMapper.class));
    }
    @Test void filtrosAcotadosYModuloSinConcederPermisosDeCobranza() {
        var f=new Filtro("x".repeat(300),null,"INVALIDO",-5,10000).normalizado();
        assertThat(f.q()).hasSize(200);assertThat(f.pagina()).isZero();assertThat(f.tamanio()).isEqualTo(25);
        assertThat(f.estado()).isEqualTo("TODOS");
        assertThat(ModuloCatalogo.GUIAS.visibleCon(Set.of())).isFalse();
        assertThat(ModuloCatalogo.GUIAS.visibleCon(Set.of("GUIA_PROCESOS_CONSULTAR"))).isTrue();
        assertThat(ModuloCatalogo.GUIAS.rutaListado()).isEqualTo("/admin/guias");
        assertThat(ModuloPermiso.expandir(Set.of("GUIA_PROCESOS_CONSULTAR"))).containsExactly("GUIA_PROCESOS_CONSULTAR");
    }
    @Test void instruccionesSeSeparanEnPasosLegiblesSinDividirDecimales() {
        var paso=new Paso(1,"Prueba","Admin","Cuotas","1. Captura 600.00. 2. Guarda.","","","",null);
        assertThat(paso.acciones()).containsExactly("Captura 600.00.","Guarda.");
    }
    private UsuarioPrincipal principal(boolean permiso) {
        return new UsuarioPrincipal(1L,2L,Set.of(),true,false,"admin","",
                permiso?List.of(new SimpleGrantedAuthority("GUIA_PROCESOS_CONSULTAR")):List.of());
    }
}
