package escuela.compras.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LectorCfdiCompraTest {
    static String xml() throws Exception{return Files.readString(Path.of("src/test/resources/compras/cfdi-prueba.xml"));}
    private LectorCfdiCompra.Cfdi leer(String s){return new LectorCfdiCompra().leer(s.getBytes(StandardCharsets.UTF_8));}
    private CompraComprobanteService service(){return new CompraComprobanteService(null,null,null,null,null,null,null,null);}
    @Test void leeVersionFolioConceptosImpuestosYRelacionesSinDependerDelPrefijo()throws Exception {
        var x=leer(xml().replace("cfdi:","otra:").replace("xmlns:cfdi=","xmlns:otra="));
        assertThat(x.uuid()).isEqualTo("AAAAAAAA-AAAA-4AAA-8AAA-AAAAAAAAAAAA");assertThat(x.conceptos()).hasSize(1);
        assertThat(x.impuestos()).hasSize(2);assertThat(x.impuestos().getFirst().concepto()).isEqualTo(1);
        assertThat(x.impuestos().getLast().concepto()).isNull();assertThat(x.relaciones()).hasSize(1);
        var p=service().partidas(x).getFirst();assertThat(p.getCantidad()).isEqualByComparingTo("2");
        assertThat(p.getPrecioUnitario()).isEqualByComparingTo("100");assertThat(p.getPorcentajeImpuesto()).isEqualByComparingTo("16");
    }
    @Test void rechazaDoctypeEntidadesEspacioDeNombresIncorrectoUuidFalsoYNumerosNoDecimales()throws Exception {
        for(String malo:new String[]{xml().replace("<cfdi:Comprobante","<!DOCTYPE x [<!ENTITY test SYSTEM 'file:///etc/passwd'>]><cfdi:Comprobante"),
            xml().replace("http://www.sat.gob.mx/cfd/4","https://falso.invalid/cfd/4"),xml().replace("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa","no-es-un-uuid"),
            xml().replace("Cantidad=\"2.000\"","Cantidad=\"2e3\""),xml().replace("<cfdi:Receptor ","<cfdi:ReceptorX ")})
            assertThatThrownBy(()->leer(malo)).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);
    }
    @Test void conservaCfdiConDescuentoPeroNoOfreceImportacionQueCambieElTotal()throws Exception {
        var x=leer(xml().replace("SubTotal=\"200.00\"","SubTotal=\"200.00\" Descuento=\"10.00\""));
        assertThatThrownBy(()->service().partidas(x)).hasMessageContaining("manualmente");
        var inconsistente=leer(xml().replace("Total=\"232.00\"","Total=\"231.99\""));
        assertThatThrownBy(()->service().partidas(inconsistente)).hasMessageContaining("manualmente");
    }
    @Test void servicioNiegaAccesoAntesDeConsultarArchivos(){
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        assertThatThrownBy(()->service().abrir(1L,1L)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}
