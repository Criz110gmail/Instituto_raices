package escuela.portal.support;
import escuela.admin.dto.PlantelForm;
import escuela.institucion.entity.*;
import escuela.institucion.mapper.PlantelMapper;
import escuela.cobranza.repository.CargoRepository;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
class TransferenciaVencidaContratoTest {
    @Test void plantelConservaConfiguracionEnFormularioYMapper() {
        var form=new PlantelForm();form.setInstitucionId(1L);
        assertThat(form.isPermitirTransferenciasVencidas()).isFalse();
        var institucion=new Institucion();institucion.setId(1L);form.setPermitirTransferenciasVencidas(true);
        var mapper=new PlantelMapper();var plantel=mapper.nuevo(form.request(),institucion);
        assertThat(plantel.isPermitirTransferenciasVencidas()).isTrue();
        var response=mapper.respuesta(plantel);
        assertThat(PlantelForm.desde(response).request().permitirTransferenciasVencidas()).isTrue();
    }
    @Test void consultaFiltraAntesDePaginarYUsaLaMismaReglaParaPostYCuentas() {
        assertThat(CargoRepository.PORTAL_BASE).contains("permitir_transferencias_vencidas",
                "transferencia_vencida_autorizada","PENDIENTE_VALIDACION","GREATEST",
                "aplicacion_pago","ajuste_cargo","c.estado_registro='EMITIDO'",
                "t.institucion_id=:institucionId","v.puede_ver_finanzas=true","a.activo=true");
        assertThat(CargoRepository.PORTAL_HOY).contains("inst.zona_horaria");
    }
    @Test void migracionNoActivaPermisosNiAutorizacionesExistentes() throws Exception {
        var sql=Files.readString(Path.of("src/main/resources/db/migration/V61__transferencias_vencidas_portal.sql"));
        assertThat(sql).contains("DEFAULT false").doesNotContain("UPDATE cargo", "UPDATE plantel", "INSERT INTO pago", "rol_permiso");
    }
}
