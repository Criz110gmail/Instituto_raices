package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaFaltanteCajaContenidoTest {
    String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V98__guia_faltante_caja_y_pestanas.sql"));}
    @Test void ochoEtapasDeCuatroAccionesConMenusYResultadosExplicitos()throws Exception {
        var filas=Pattern.compile("(?m)^\\('corte-caja-faltante-1',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int n=0;while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++n);
            assertThat(filas.group(4)).isNotBlank();
            assertThat(new GuiaProcesosService.Paso(n,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"Ejemplo","Resultado","Precaución",null).acciones()).hasSize(4);
            assertThat(filas.group(5)).startsWith("1. ");
        }
        assertThat(n).isEqualTo(8);
    }
    @Test void actualizaSeisEtapasAnterioresConPestanasYModalSinEditarMigracionesAplicadas()throws Exception {
        var pasos=Pattern.compile("(?m)^\\((\\d+),\\$paso\\$(.*?)\\$paso\\$\\)").matcher(sql());
        var numeros=new java.util.ArrayList<Integer>();
        while(pasos.find()) {
            int numero=Integer.parseInt(pasos.group(1));numeros.add(numero);
            assertThat(new GuiaProcesosService.Paso(numero,"Título","Administración","Cortes",pasos.group(2),"Ejemplo","Resultado","Precaución",null).acciones()).hasSize(4);
        }
        assertThat(numeros).containsExactly(3,6,7,8,9,10);
        assertThat(sql()).contains("Confirmar y abrir corte","Cancelar dentro del modal","pestaña Resumen","Cierre y conciliación","Conteo y cierre","Apertura","fuera de las pestañas","version_contenido=version_contenido+1");
    }
    @Test void soloContenidoConservaFaltanteYSaldoSinAtribuirSobranteConfirmado()throws Exception {
assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-15'","Explica la diferencia entre el efectivo declarado y el saldo esperado","Estado ABIERTO","Sin movimientos registrados durante este corte","Diferencia = $1,149 - $1,150 = -$1.00","saldo actual de la cuenta $1,150","no genera un egreso de $1","saldo inicial DEL NUEVO CORTE $1,150","DE LA CUENTA permanece $1,000","Sobrante de $1 y reportes financieros siguen pendientes","No reejecuta los cortes");
        var escrituras=Pattern.compile("(?:INSERT INTO|UPDATE) (\\w+)").matcher(sql());
        assertThat(escrituras.results().map(m->m.group(1)).toList()).isNotEmpty().allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(sql()).doesNotContain("DELETE FROM","ALTER TABLE","rol_permiso");
    }
}
