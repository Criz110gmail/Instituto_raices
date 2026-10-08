package escuela.cobranza.support;

import escuela.cobranza.entity.Cargo;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.*;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/** Compila Criteria con Hibernate/PostgreSQL real; intercepta antes de acceder a datos. */
class SituacionAdeudoSqlTest {
    private static LocalContainerEntityManagerFactoryBean factory;
    private static EntityManagerFactory emf;
    private static String sql;

    @BeforeAll static void preparar() {
        factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(mock(DataSource.class));
        factory.setPackagesToScan("escuela");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaPropertyMap(Map.of(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect",
                "hibernate.boot.allow_jdbc_metadata_access", "false",
                "hibernate.hbm2ddl.auto", "none",
                "hibernate.session_factory.statement_inspector", (StatementInspector) statement -> {
                    sql = statement; throw new SqlCapturado();
                }));
        factory.afterPropertiesSet(); emf = factory.getObject();
    }
    @AfterAll static void cerrar() { if (factory != null) factory.destroy(); }

    @Test void filtrosSeCompilanConSubconsultasCorrelacionadasAntesDePaginar() {
        for (String filtro : new String[]{"CON_SALDO", "PENDIENTE", "PARCIAL", "VENCIDO", "LIQUIDADO"}) {
            capturar(filtro, false);
            assertThat(sql).contains("ajuste_cargo", "aplicacion_pago", "coalesce", "case", "cargo_id", "estado_registro")
                    .contains("offset ? rows fetch first ? rows only");
            // No JOIN sobre colecciones que pudiera multiplicar los montos.
            assertThat(sql).doesNotContain("join ajuste_cargo", "join aplicacion_pago");
            capturar(filtro, true);
            assertThat(sql).contains("count(", "ajuste_cargo", "aplicacion_pago")
                    .doesNotContain("fetch first", "offset ?");
        }
    }

    @Test void estadosAdministrativosYTodosNoRequierenSumarMovimientos() {
        for (String filtro : new String[]{"TODOS", "CANCELADO", "CONVENIDO", "EMITIDO", "NO_VALIDO"}) {
            capturar(filtro, true);
            assertThat(sql).doesNotContain("ajuste_cargo", "aplicacion_pago");
        }
    }

    private void capturar(String filtro, boolean conteo) {
        sql = null;
        try (var em = emf.createEntityManager()) {
            var cb = em.getCriteriaBuilder();
            if (conteo) {
                var q = cb.createQuery(Long.class); var root = q.from(Cargo.class);
                q.select(cb.count(root)).where(SituacionAdeudo.filtro(filtro, LocalDate.of(2026,10,8)).toPredicate(root,q,cb));
                try { em.createQuery(q).getSingleResult(); } catch (RuntimeException esperado) { assertThat(sql).isNotNull(); }
            } else {
                var q = cb.createQuery(Long.class); var root = q.from(Cargo.class);
                q.select(root.get("id")).where(SituacionAdeudo.filtro(filtro, LocalDate.of(2026,10,8)).toPredicate(root,q,cb));
                q.orderBy(cb.desc(root.get("id")));
                try { em.createQuery(q).setFirstResult(10).setMaxResults(10).getResultList(); }
                catch (RuntimeException esperado) { assertThat(sql).isNotNull(); }
            }
        }
        assertThat(sql).isNotNull();
    }
    private static class SqlCapturado extends RuntimeException { }

    @Test void rangoDeCuotasPagosYCargosSeCompilaAntesDePaginar() {
        var f=new escuela.admin.dto.FiltroCatalogo("","TODOS",null,0,10,LocalDate.of(2026,10,1),LocalDate.of(2026,10,31),"REGISTRO");
        rango(Cargo.class,escuela.admin.support.RangoFechasCatalogo.cargos(f));
        assertThat(sql).contains("fecha_emision>=", "fecha_emision<=", "fetch first");
        rango(escuela.cobranza.entity.CuotaAlumno.class,escuela.admin.support.RangoFechasCatalogo.cuotas(f));
        assertThat(sql).contains("cuota_vencimiento_en_rango", "fecha_vencimiento_unico>=", "fecha_vencimiento_unico<=");
        rango(escuela.finanzas.entity.Pago.class,escuela.admin.support.RangoFechasCatalogo.pagos(f));
        assertThat(sql).contains("timezone(","zona_horaria", "fecha_pago", "date(").doesNotContain("validado_en");
    }
    private <T> void rango(Class<T> tipo,org.springframework.data.jpa.domain.Specification<T> filtro) {
        sql=null;
        try(var em=emf.createEntityManager()) {
            var cb=em.getCriteriaBuilder(); var q=cb.createQuery(Long.class); var root=q.from(tipo);
            q.select(root.get("id")).where(filtro.toPredicate(root,q,cb));
            try { em.createQuery(q).setMaxResults(10).getResultList(); } catch(RuntimeException esperado) { assertThat(sql).isNotNull(); }
        }
        assertThat(sql).isNotNull();
    }
}
