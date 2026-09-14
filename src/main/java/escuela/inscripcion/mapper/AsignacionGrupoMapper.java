package escuela.inscripcion.mapper;

import escuela.academico.entity.Grupo;
import escuela.inscripcion.dto.request.AsignacionGrupoRequest;
import escuela.inscripcion.dto.response.AsignacionGrupoResponse;
import escuela.inscripcion.entity.AsignacionGrupo;
import escuela.inscripcion.entity.Inscripcion;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class AsignacionGrupoMapper {

    public AsignacionGrupo nueva(AsignacionGrupoRequest dto, Inscripcion inscripcion, Grupo grupo) {
        AsignacionGrupo entidad = new AsignacionGrupo();
        entidad.setInscripcion(inscripcion);
        entidad.setGrupo(grupo);
        entidad.setFechaInicio(dto.fechaInicio());
        entidad.setMotivo(limpiar(dto.motivo()));
        return entidad;
    }

    public AsignacionGrupoResponse respuesta(AsignacionGrupo e) {
        Grupo grupo = e.getGrupo();
        return new AsignacionGrupoResponse(e.getId(), e.getInscripcion().getId(), grupo.getId(),
                grupo.getNombre(), grupo.getCodigo(), grupo.getTurno(), grupo.getAula(),
                e.getFechaInicio(), e.getFechaFin(), e.getMotivo(), desde(e));
    }
}
