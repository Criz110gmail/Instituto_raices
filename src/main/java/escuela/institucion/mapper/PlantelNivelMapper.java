package escuela.institucion.mapper;

import escuela.academico.entity.NivelEducativo;
import escuela.institucion.dto.request.PlantelNivelRequest;
import escuela.institucion.dto.response.PlantelNivelResponse;
import escuela.institucion.entity.Plantel;
import escuela.institucion.entity.PlantelNivel;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class PlantelNivelMapper {

    public PlantelNivel nuevo(PlantelNivelRequest dto, Plantel plantel, NivelEducativo nivel) {
        PlantelNivel entidad = new PlantelNivel();
        actualizar(entidad, dto, plantel, nivel);
        return entidad;
    }

    public void actualizar(PlantelNivel entidad, PlantelNivelRequest dto, Plantel plantel, NivelEducativo nivel) {
        entidad.setPlantel(plantel);
        entidad.setNivelEducativo(nivel);
        entidad.setClaveCentroTrabajo(limpiar(dto.claveCentroTrabajo()));
        entidad.setActivo(dto.activo());
    }

    public PlantelNivelResponse respuesta(PlantelNivel e) {
        return new PlantelNivelResponse(e.getId(), e.getPlantel().getId(), e.getNivelEducativo().getId(),
                e.getClaveCentroTrabajo(), e.isActivo(), desde(e));
    }
}
