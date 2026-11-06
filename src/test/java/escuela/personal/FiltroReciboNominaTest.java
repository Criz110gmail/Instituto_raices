package escuela.personal;

import escuela.personal.dto.FiltroReciboNomina;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class FiltroReciboNominaTest {
    @Test void normalizaConservaFiltrosYNoAceptaRangoInvertido(){
        var f=new FiltroReciboNomina(" Sofía ","NETO_CERO",2L,"EMP · Sofía",8L,"NOV-2026",LocalDate.of(2026,11,4),LocalDate.of(2026,11,3),3,25).normalizado();
        assertThatThrownBy(f::validar).hasMessageContaining("posterior");
        assertThat(f.url("/admin/recibos-nomina")).contains("empleadoId=2","periodoId=8","estado=NETO_CERO","pagina=3","desde=2026-11-04");
        assertThat(f.pagina(0).empleadoId()).isEqualTo(2);
        assertThat(new FiltroReciboNomina(null,"ALTERADO",null,null,null,null,null,null,-1,999).normalizado().estado()).isEqualTo("TODOS");
    }
}
