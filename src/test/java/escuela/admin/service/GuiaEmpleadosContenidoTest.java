package escuela.admin.service;

import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaEmpleadosContenidoTest {
    private String sql() throws Exception {return Files.readString(Path.of("src/main/resources/db/migration/V113__guias_empleados_planteles.sql"));}
    @Test void soloContenidoEditorialYPermisosExistentesSinOtorgarlos()throws Exception{
        var texto=sql();
        assertThat(Pattern.compile("(?im)^INSERT INTO (\\w+)").matcher(texto).results().map(m->m.group(1)).toList())
                .hasSize(5).allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(texto).contains("empleado-administrativo-sin-perfil-docente","empleado-planteles-cambio-principal",
                "'Personal'","DATE '2026-10-20'","'EMPLEADO_LEER'","'EMPLEADO_LABORAL_ADMINISTRAR'","'EMPLEADO_ADMINISTRAR'");
        assertThat(texto).doesNotContain("UPDATE ","DELETE FROM","ALTER TABLE","INSERT INTO empleado ","INSERT INTO rol_permiso");
    }
    @Test void sieteYOchoEtapasConCuatroAccionesCadaUna()throws Exception{
        var patron=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$");
        var numeros=new ArrayList<Integer>();var filas=patron.matcher(sql());
        while(filas.find()){
            int n=Integer.parseInt(filas.group(1));numeros.add(n);
            var paso=new GuiaProcesosService.Paso(n,filas.group(2),"Administración",filas.group(3),filas.group(4),"e","r","p","/admin/empleados");
            assertThat(paso.acciones()).hasSize(4);assertThat(paso.modulo().length()).isLessThanOrEqualTo(120);
        }
        assertThat(numeros).containsExactly(1,2,3,4,5,6,7,1,2,3,4,5,6,7,8);
    }
    @Test void documentaFechasAvisoYLimitesDeLoConfirmado()throws Exception{
        assertThat(sql()).contains("Registrar empleado","Guardar fotografía","Guardar datos personales","Exportar Excel",
                "Sin cuenta vinculada al portal de maestros","Guardar información laboral","Agregar asignación laboral",
                "Cambio de principal programado","Visualizar cambio de principal","Confirmar cambio de principal","Cancelar",
                "21/10/2026","no significa que haya fallado", "todavía no está vigente","No cambies el reloj","20/10/2026",
                "EMPLEADO_PRINCIPAL_CAMBIADO","observación del día siguiente no se da por confirmada",
                "Baja laboral, continuidad desmarcada","sin declarar terminada nómina");
    }
}
