package escuela.personal;

import java.util.List;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named="QA_GUIAS_CIERRE_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_guias160_qa")
class GuiasCierreConsultaPostgresIT {
    @Test void migraGuiasSinTocarRolesHistoricosNiDinero() {
        var ds=new DriverManagerDataSource(System.getenv("QA_GUIAS_CIERRE_JDBC"),"postgres","qa-sintetica");
        Flyway.configure().dataSource(ds).locations("classpath:db/migration").target("159").load().migrate();
        var j=new JdbcTemplate(ds);
        var permisos=j.queryForList("SELECT * FROM rol_permiso ORDER BY rol_id,permiso_id");
        var prestamosAntes=j.queryForList("SELECT s.* FROM guia_proceso_paso s JOIN guia_proceso g ON g.id=s.guia_id WHERE g.slug='prestamos-programados-nomina' AND s.numero<13 ORDER BY s.numero");
        var otrasGuias=j.queryForList("SELECT * FROM guia_proceso WHERE slug<>'prestamos-programados-nomina' ORDER BY id");
        Flyway.configure().dataSource(ds).locations("classpath:db/migration").target("160").load().migrate();
        assertThat(j.queryForList("SELECT * FROM rol_permiso ORDER BY rol_id,permiso_id")).isEqualTo(permisos);
        assertThat(j.queryForList("SELECT s.* FROM guia_proceso_paso s JOIN guia_proceso g ON g.id=s.guia_id WHERE g.slug='prestamos-programados-nomina' AND s.numero<13 ORDER BY s.numero")).isEqualTo(prestamosAntes);
        assertThat(j.queryForList("SELECT * FROM guia_proceso WHERE slug NOT IN ('prestamos-programados-nomina','consulta-cheques-cuenta-confirmada') ORDER BY id")).isEqualTo(otrasGuias);
        assertThat(j.queryForObject("SELECT version_contenido FROM guia_proceso WHERE slug='prestamos-programados-nomina'",Integer.class)).isEqualTo(6);
        assertThat(j.queryForObject("SELECT requisitos FROM guia_proceso WHERE slug='prestamos-programados-nomina'",String.class)).contains("rol sólo consulta confirmada").doesNotContain("sigue pendiente");
        for(var dato:List.of(new Object[]{"prestamos-programados-nomina",14L},new Object[]{"consulta-cheques-cuenta-confirmada",5L})){
            assertThat(j.queryForObject("SELECT count(*) FROM guia_proceso_paso s JOIN guia_proceso g ON g.id=s.guia_id WHERE g.slug=? AND g.estado='CONFIRMADA'",Long.class,dato[0])).isEqualTo(dato[1]);
        }
        assertThat(j.queryForList("SELECT p.codigo FROM guia_proceso_permiso gp JOIN guia_proceso g ON g.id=gp.guia_id JOIN permiso p ON p.id=gp.permiso_id WHERE g.slug='consulta-cheques-cuenta-confirmada' ORDER BY p.codigo",String.class)).containsExactly("CHEQUERA_LEER","CUENTA_FINANCIERA_LEER");
        for(var tabla:List.of("movimiento_financiero","prestamo_empleado","nomina_pago_intento","cheque_nomina")){
            assertThat(j.queryForObject("SELECT count(*) FROM "+tabla,Long.class)).as(tabla).isZero();
        }
    }
}
