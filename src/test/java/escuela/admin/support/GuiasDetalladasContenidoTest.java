package escuela.admin.support;
import escuela.admin.service.GuiaProcesosService.Paso;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;
class GuiasDetalladasContenidoTest {
    private String sql() throws Exception {return Files.readString(Path.of("src/main/resources/db/migration/V63__guias_recorridos_detallados.sql"));}
    @Test void actualizaLosTreintaPasosDeLasCuatroGuiasSinModificarOperaciones() throws Exception {
        var texto=sql();var patron=Pattern.compile("\\('([^']+)',(\\d+),\\$paso\\$([\\s\\S]*?)\\$paso\\$\\)");
        var matcher=patron.matcher(texto);var numeros=new LinkedHashMap<String,List<Integer>>();int filas=0;
        while(matcher.find()) {
            filas++;var slug=matcher.group(1);int numero=Integer.parseInt(matcher.group(2));
            numeros.computeIfAbsent(slug,k->new ArrayList<>()).add(numero);
            var instrucciones=matcher.group(3);
            assertThat(instrucciones).startsWith("1. ").containsAnyOf("Desde el menú lateral","Abre una ventana privada separada");
            var paso=new Paso(numero,"Prueba","Administración","Prueba",instrucciones,"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones().size()).isGreaterThanOrEqualTo(3);
            var marcas=Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(instrucciones);int esperado=1;
            while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).as(slug+" paso"+numero).isEqualTo(esperado++);
            assertThat(paso.acciones()).noneMatch(s->s.matches("\\d+\\. .*"));
        }
        assertThat(filas).isEqualTo(30);
        assertThat(numeros.get("transferencia-dos-hijos")).containsExactly(1,2,3,4,5,6,7,8);
        assertThat(numeros.get("beca-recargo-liquidacion")).containsExactly(1,2,3,4,5,6,7,8);
        assertThat(numeros.get("pagos-parciales-liquidacion")).containsExactly(1,2,3,4,5,6,7);
        assertThat(numeros.get("transferencia-vencida-500")).containsExactly(1,2,3,4,5,6,7);
        assertThat(texto).contains("version_contenido=version_contenido+1","DATE '2026-10-07'")
                .doesNotContain("UPDATE cargo", "UPDATE pago", "INSERT INTO cargo", "INSERT INTO pago", "ALTER TABLE", "rol_permiso");
    }
    @Test void habilitacionIndicaMenuBusquedaDetalleBotonMotivoYConfirmacion() throws Exception {
        var matcher=Pattern.compile("\\('transferencia-vencida-500',4,\\$paso\\$([\\s\\S]*?)\\$paso\\$\\)").matcher(sql());
        assertThat(matcher.find()).isTrue();assertThat(matcher.group(1)).contains(
                "Operación escolar → Cobranza escolar → Adeudos de alumnos","Buscar", "Aplicar filtros", "Acciones pulsa Ver",
                "Transferencia de cargo vencido","Motivo de autorización o revocación", "Guardar autorización",
                "Administración → Configuración escolar → Planteles", "Guardar cambios");
    }
    @Test void explicaCapturaParcialYValidacionComoAccionesDistintas() throws Exception {
        assertThat(sql()).contains("Registrar pago parcial","Registrar como pendiente","Validar y publicar",
                "la pestaña Comprobantes","la pestaña Distribución","la pestaña Gestión",
                "ni significa que el tutor pagó","no concede permisos","no se captura");
    }
}
