package escuela.personal;

import java.util.List;
import java.util.Set;
import escuela.admin.service.GuiaProcesosService;
import escuela.seguridad.service.UsuarioPrincipal;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.security.access.AccessDeniedException;

/** Sólo PostgreSQL17 efímero y sintético; jamás apuntar a la base restaurada. */
@EnabledIfEnvironmentVariable(named="QA_GUIA_PRESTAMO_CERO_JDBC", matches="jdbc:postgresql://127.0.0.1:5432/nexo_guia159_qa")
class GuiaPrestamoNetoCeroPostgresIT {
    @Test void ampliaLaGuiaSinCambiarHistoriaAnteriorPermisosNiDatosOperativos() {
        var ds = new DriverManagerDataSource(System.getenv("QA_GUIA_PRESTAMO_CERO_JDBC"), "postgres", "qa-sintetica");
        Flyway.configure().dataSource(ds).locations("classpath:db/migration").target("158").load().migrate();
        var j = new JdbcTemplate(ds);
        var previos = j.queryForList("SELECT s.* FROM guia_proceso_paso s JOIN guia_proceso g ON g.id=s.guia_id WHERE g.slug='prestamos-programados-nomina' AND s.numero<8 ORDER BY s.numero");
        var permisos = j.queryForList("SELECT * FROM guia_proceso_permiso ORDER BY guia_id,permiso_id");
        var roles = j.queryForList("SELECT * FROM rol_permiso ORDER BY rol_id,permiso_id");
        var otrasGuias = j.queryForList("SELECT * FROM guia_proceso WHERE slug<>'prestamos-programados-nomina' ORDER BY id");
        Flyway.configure().dataSource(ds).locations("classpath:db/migration").target("159").load().migrate();
        assertThat(j.queryForList("SELECT s.* FROM guia_proceso_paso s JOIN guia_proceso g ON g.id=s.guia_id WHERE g.slug='prestamos-programados-nomina' AND s.numero<8 ORDER BY s.numero")).isEqualTo(previos);
        assertThat(j.queryForList("SELECT * FROM guia_proceso_permiso ORDER BY guia_id,permiso_id")).isEqualTo(permisos);
        assertThat(j.queryForList("SELECT * FROM rol_permiso ORDER BY rol_id,permiso_id")).isEqualTo(roles);
        assertThat(j.queryForList("SELECT * FROM guia_proceso WHERE slug<>'prestamos-programados-nomina' ORDER BY id")).isEqualTo(otrasGuias);
        var autoridades = j.queryForList("SELECT codigo FROM permiso ORDER BY codigo", String.class)
                .stream().map(SimpleGrantedAuthority::new).toList();
        var principal = new UsuarioPrincipal(1L, 1L, Set.of(), true, false, "qa-guia", "no-credencial-real", autoridades);
        var servicio = new GuiaProcesosService(new NamedParameterJdbcTemplate(ds));
        var detalle = servicio.obtener(principal, "prestamos-programados-nomina");
        assertThat(detalle.guia().estado()).isEqualTo("CONFIRMADA");
        assertThat(detalle.guia().versionContenido()).isEqualTo(5);
        assertThat(detalle.guia().ejemplo()).contains("Tomás", "PRE-000001", "BOR-000017", "Sofía", "PRE-000002");
        assertThat(detalle.guia().requisitos()).doesNotContain("Neto cero con préstamo y comprobaciones manuales complementarias se prueban por separado");
        assertThat(detalle.pasos()).hasSize(13);
        for (int i = 8; i < 13; i++) {
            assertThat(detalle.pasos().get(i).numero()).isEqualTo(i + 1);
            assertThat(detalle.pasos().get(i).acciones()).hasSizeGreaterThanOrEqualTo(4);
        }
        var filtro = new GuiaProcesosService.Filtro("Sofía", "Personal", "CONFIRMADA", 0, 25);
        assertThat(servicio.listar(principal, filtro).getContent()).extracting(GuiaProcesosService.Guia::slug)
                .contains("prestamos-programados-nomina");
        var sinPermisos = new UsuarioPrincipal(1L, 1L, Set.of(), true, false, "qa-sin-guia", "no-credencial-real", List.of());
        assertThatThrownBy(() -> servicio.obtener(sinPermisos, "prestamos-programados-nomina"))
                .isInstanceOf(AccessDeniedException.class);
        for (var tabla : List.of("movimiento_financiero", "prestamo_empleado", "prestamo_cuota_cero", "nomina_pago_intento")) {
            assertThat(j.queryForObject("SELECT count(*) FROM " + tabla, Long.class)).as(tabla).isZero();
        }
    }
}
