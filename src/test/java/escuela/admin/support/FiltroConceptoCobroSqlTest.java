package escuela.admin.support;

import escuela.admin.dto.FiltroCatalogo;
import escuela.cobranza.entity.*;
import escuela.finanzas.entity.Pago;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.*;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

/** Compila SQL PostgreSQL con Hibernate; interrumpe antes de cualquier conexión. */
class FiltroConceptoCobroSqlTest {
    static LocalContainerEntityManagerFactoryBean factory;
    static EntityManagerFactory emf;
    static String sql;
    static class Capturado extends RuntimeException {}
    @BeforeAll static void preparar() {
        factory=new LocalContainerEntityManagerFactoryBean();factory.setDataSource(mock(DataSource.class));
        factory.setPackagesToScan("escuela");factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaPropertyMap(Map.of("hibernate.dialect","org.hibernate.dialect.PostgreSQLDialect",
            "hibernate.boot.allow_jdbc_metadata_access","false","hibernate.hbm2ddl.auto","none",
            "hibernate.session_factory.statement_inspector",(StatementInspector)statement->{sql=statement;throw new Capturado();}));
        factory.afterPropertiesSet();emf=factory.getObject();
    }
    @AfterAll static void cerrar(){if(factory!=null)factory.destroy();}
    @Test void codigoNombreFiltranAntesDePaginarYContarEnLosTresModulos() {
        for(var clase:new Class[]{Cargo.class,CuotaAlumno.class,Pago.class}) {
            for(boolean conteo:new boolean[]{false,true}) {
                sql=null;
                try(var em=emf.createEntityManager()) {
                    var cb=em.getCriteriaBuilder();var q=cb.createQuery(Long.class);var root=q.from(clase);
                    var filtro=new FiltroCatalogo("Alumno","TODOS",null,0,10,null,null,"REGISTRO","TODOS",null,"ANTICIPO-TEST-05");
                    var spec=clase==Pago.class?FiltroConceptoPago.especificacion(filtro):FiltroConceptoCobro.especificacion(filtro);
                    q.select(conteo?cb.count(root):root.get("id")).where(spec.toPredicate(root,q,cb));
                    try {var consulta=em.createQuery(q);if(!conteo)consulta.setFirstResult(10).setMaxResults(10);consulta.getResultList();fail("El SQL debe interceptarse antes de conectar");}
                    catch(RuntimeException esperado){assertThat(sql).isNotNull();}
                    assertThat(sql).contains("concepto_cobro","codigo","nombre","lower(","like");
                    if(conteo)assertThat(sql).contains("count(").doesNotContain("fetch first");
                    else assertThat(sql).contains("offset ? rows fetch first ? rows only");
                    if(clase==Pago.class)assertThat(sql).contains("exists","solicitud_aplicacion_pago","aplicacion_pago","acuerdo_anticipado_cargo");
                }
            }
        }
    }
}
