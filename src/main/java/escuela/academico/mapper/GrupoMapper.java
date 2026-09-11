package escuela.academico.mapper;

import escuela.academico.dto.request.GrupoRequest;
import escuela.academico.dto.response.GrupoResponse;
import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.Grado;
import escuela.academico.entity.Grupo;
import escuela.institucion.entity.Plantel;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class GrupoMapper {

    public Grupo nuevo(GrupoRequest dto, Plantel plantel, CicloEscolar ciclo, Grado grado) {
        Grupo entidad = new Grupo();
        actualizar(entidad, dto, plantel, ciclo, grado);
        return entidad;
    }

    public void actualizar(Grupo entidad, GrupoRequest dto, Plantel plantel,
                           CicloEscolar ciclo, Grado grado) {
        entidad.setPlantel(plantel);
        entidad.setCicloEscolar(ciclo);
        entidad.setGrado(grado);
        entidad.setNombre(limpiar(dto.nombre()));
        entidad.setTurno(dto.turno());
        entidad.setCodigo(codigo(dto.codigo()));
        entidad.setAula(limpiar(dto.aula()));
        entidad.setCapacidad(dto.capacidad());
        entidad.setActivo(dto.activo());
    }

    public GrupoResponse respuesta(Grupo e) {
        return new GrupoResponse(e.getId(), e.getPlantel().getId(), e.getCicloEscolar().getId(),
                e.getGrado().getId(), e.getNombre(), e.getTurno(), e.getCodigo(), e.getAula(),
                e.getCapacidad(), e.isActivo(), desde(e));
    }
}
