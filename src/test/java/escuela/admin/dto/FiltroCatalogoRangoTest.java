package escuela.admin.dto;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class FiltroCatalogoRangoTest {
    @Test void conservaRangoAlNormalizarYPaginar() {
        var f=new FiltroCatalogo(" Ana ","parcial",null,-1,500,LocalDate.of(2026,10,1),LocalDate.of(2026,10,31),"REGISTRO").normalizado();
        assertThat(f.q()).isEqualTo("Ana"); assertThat(f.estado()).isEqualTo("PARCIAL");
        assertThat(f.pagina()).isZero(); assertThat(f.tamanio()).isEqualTo(100);
        assertThat(f.conPagina(2).desde()).isEqualTo(f.desde());
        assertThat(f.conPagina(2).hasta()).isEqualTo(f.hasta());
        assertThat(f.conPagina(2).tipoFecha()).isEqualTo("REGISTRO");
    }
    @Test void aceptaLimitesAbiertosYDiaUnicoPeroRechazaRangoInvertido() {
        var d=LocalDate.of(2026,10,8);
        for(var f:java.util.List.of(new FiltroCatalogo("","TODOS",null,0,25,d,null,"VENCIMIENTO"),
                new FiltroCatalogo("","TODOS",null,0,25,null,d,"VENCIMIENTO"),
                new FiltroCatalogo("","TODOS",null,0,25,d,d,"VENCIMIENTO")))
            assertThatCode(f::validarRango).doesNotThrowAnyException();
        assertThatThrownBy(()->new FiltroCatalogo("","TODOS",null,0,25,d.plusDays(1),d,"VENCIMIENTO").validarRango())
                .hasMessageContaining("Desde no puede ser posterior a Hasta");
    }
}
