package escuela.common.mapper;

import escuela.common.dto.response.AuditoriaResponse;
import escuela.config.audit.EntidadAuditable;

public final class AuditoriaMapper {

    private AuditoriaMapper() {
    }

    public static AuditoriaResponse desde(EntidadAuditable entidad) {
        return new AuditoriaResponse(
                entidad.getCreadoEn(),
                entidad.getCreadoPorId(),
                entidad.getActualizadoEn(),
                entidad.getActualizadoPorId(),
                entidad.getVersion());
    }
}
