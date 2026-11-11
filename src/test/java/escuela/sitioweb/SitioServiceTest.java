package escuela.sitioweb;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class SitioServiceTest {
 @Test void rechazaAccesoAnonimoAntesDeConsultarDatos(){var db=mock(org.springframework.jdbc.core.JdbcTemplate.class);var service=new SitioService(db);assertThatThrownBy(()->service.borrador(null)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);verifyNoInteractions(db);}
 @Test void compruebaContrasteDeBotones(){assertThatCode(()->SitioService.contraste("#2b5031")).doesNotThrowAnyException();assertThatThrownBy(()->SitioService.contraste("#ffffff")).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);}
}
