package escuela.admin.dto;

import escuela.institucion.dto.request.PlantelNivelRequest;
import escuela.institucion.dto.response.PlantelNivelResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlantelNivelForm {
    @NotNull private Long plantelId;
    @NotNull private Long nivelEducativoId;
    @Size(max = 30) private String claveCentroTrabajo;
    private boolean activo = true;
    private Long version;

    public PlantelNivelRequest request() {
        return new PlantelNivelRequest(plantelId, nivelEducativoId, claveCentroTrabajo,
                activo, version);
    }

    public static PlantelNivelForm desde(PlantelNivelResponse r) {
        PlantelNivelForm f = new PlantelNivelForm();
        f.plantelId = r.plantelId();
        f.nivelEducativoId = r.nivelEducativoId();
        f.claveCentroTrabajo = r.claveCentroTrabajo();
        f.activo = r.activo();
        f.version = r.auditoria().version();
        return f;
    }
}
