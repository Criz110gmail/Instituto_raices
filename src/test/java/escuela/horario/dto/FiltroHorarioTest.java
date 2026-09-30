package escuela.horario.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FiltroHorarioTest {
    @Test void normalizaPaginaYTamanioSinPerderFiltros(){var f=new FiltroHorario(4L,8L,2,null,true,-3,999).normalizado();assertEquals(0,f.pagina());assertEquals(25,f.tamanio());assertEquals(4L,f.maestroId());assertEquals(8L,f.grupoId());assertEquals(2,f.diaSemana());assertTrue(f.activo());}
}
