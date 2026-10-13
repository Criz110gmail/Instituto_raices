package escuela.finanzas.service;
import escuela.finanzas.repository.PagoRepository;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.admin.dto.ModuloCatalogo;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class CoberturaAnualPagoServiceTest {
 @Test void loteVacioNoConsultaYDetalleRespetaAlcance(){
  var repo=mock(PagoRepository.class);var alcance=mock(AlcanceDatosService.class);var service=new CoberturaAnualPagoService(repo,alcance);
  assertThat(service.lote(List.of())).isEmpty();verifyNoInteractions(repo);
  when(repo.coberturasAnuales(List.of(4L))).thenReturn(List.of());assertThat(service.dePago(4L)).isEmpty();
  verify(alcance).validarRecurso(ModuloCatalogo.PAGOS,4L);verify(repo).coberturasAnuales(List.of(4L));
 }
 @Test void noConsultaOtroPagoSiNoHayPermiso(){
  var repo=mock(PagoRepository.class);var alcance=mock(AlcanceDatosService.class);var service=new CoberturaAnualPagoService(repo,alcance);
  doThrow(new org.springframework.security.access.AccessDeniedException("sin permiso")).when(alcance).validarRecurso(ModuloCatalogo.PAGOS,9L);
  assertThatThrownBy(()->service.dePago(9L)).hasMessageContaining("sin permiso");verifyNoInteractions(repo);
 }
}
