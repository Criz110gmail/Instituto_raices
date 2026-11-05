package escuela.personal;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import static org.assertj.core.api.Assertions.assertThat;

/** Ejecutar únicamente en PostgreSQL efímero de permisos, no junto al IT de negocio. */
@EnabledIfEnvironmentVariable(named="QA_PRESTAMO_PERMISOS_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_flujo_nomina_qa")
class PrestamoPermisosPostgresIT {
 @Test void reduceCuatroADosSinElevarLectoresNiCambiarDinero(){
  var ds=new DriverManagerDataSource(System.getenv("QA_PRESTAMO_PERMISOS_JDBC"),"postgres","qa-sintetica");
  Flyway.configure().dataSource(ds).locations("classpath:db/migration").target("155").load().migrate();
  var j=new JdbcTemplate(ds);
  j.update("INSERT INTO institucion(id,codigo,nombre) VALUES(660000,'QA-PERM-PREST','Ficticia')");
  j.update("INSERT INTO rol(id,institucion_id,codigo,nombre) VALUES(660000,660000,'QA-PREST-ADMIN','Admin ficticio'),(660001,660000,'QA-PREST-LEER','Lector ficticio'),(660002,660000,'QA-PREST-ENT','Especializado ficticio')");
  j.update("INSERT INTO rol_permiso(rol_id,permiso_id) SELECT 660000,id FROM permiso WHERE codigo LIKE 'PRESTAMO_%'");
  j.update("INSERT INTO rol_permiso(rol_id,permiso_id) SELECT 660001,id FROM permiso WHERE codigo='PRESTAMO_LEER'");
  j.update("INSERT INTO rol_permiso(rol_id,permiso_id) SELECT 660002,id FROM permiso WHERE codigo='PRESTAMO_ENTREGAR'");
  Flyway.configure().dataSource(ds).locations("classpath:db/migration").load().migrate();
  assertThat(j.queryForList("SELECT codigo FROM permiso WHERE codigo LIKE 'PRESTAMO_%' ORDER BY codigo",String.class)).containsExactly("PRESTAMO_ADMINISTRAR","PRESTAMO_LEER");
  assertThat(j.queryForList("SELECT p.codigo FROM rol_permiso rp JOIN permiso p ON p.id=rp.permiso_id WHERE rp.rol_id=660000 ORDER BY p.codigo",String.class)).containsExactly("PRESTAMO_ADMINISTRAR","PRESTAMO_LEER");
  assertThat(j.queryForList("SELECT p.codigo FROM rol_permiso rp JOIN permiso p ON p.id=rp.permiso_id WHERE rp.rol_id=660001",String.class)).containsExactly("PRESTAMO_LEER");
  assertThat(j.queryForObject("SELECT count(*) FROM rol_permiso WHERE rol_id=660002",Long.class)).isZero();
  assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();
  assertThat(j.queryForObject("SELECT count(*) FROM prestamo_empleado",Long.class)).isZero();
  assertThat(j.queryForObject("SELECT requisitos FROM guia_proceso WHERE slug='prestamos-programados-nomina'",String.class)).contains("Dos permisos").doesNotContain("Entregar y Reestructurar");
  assertThat(j.queryForObject("SELECT count(*) FROM guia_proceso_permiso gp JOIN permiso p ON p.id=gp.permiso_id JOIN guia_proceso g ON g.id=gp.guia_id WHERE g.slug='prestamos-programados-nomina' AND p.codigo='PRESTAMO_LEER'",Long.class)).isZero();
 }
}
