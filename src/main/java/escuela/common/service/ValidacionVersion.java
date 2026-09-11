package escuela.common.service;

import escuela.common.exception.ConflictoVersionException;
import escuela.config.audit.EntidadAuditable;

import java.util.Objects;

public final class ValidacionVersion {

    private ValidacionVersion() {
    }

    public static void verificar(EntidadAuditable entidad, Long version, String recurso) {
        if (version == null || !Objects.equals(entidad.getVersion(), version)) {
            throw new ConflictoVersionException(recurso, entidad.getId());
        }
    }
}
