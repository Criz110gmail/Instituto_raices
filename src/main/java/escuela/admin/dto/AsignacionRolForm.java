package escuela.admin.dto;

import escuela.seguridad.dto.request.AsignacionRolRequest;
import escuela.seguridad.entity.AlcanceRol;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignacionRolForm {
    @NotNull private Long rolId;
    @NotNull private AlcanceRol alcance = AlcanceRol.INSTITUCION;
    private Long plantelId;

    public AsignacionRolRequest request(Long usuarioId) {
        return new AsignacionRolRequest(usuarioId, rolId, alcance, plantelId);
    }
}
