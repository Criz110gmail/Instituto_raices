package escuela.academico.mapper;

import escuela.academico.dto.request.GradoRequest;
import escuela.academico.dto.response.GradoResponse;
import escuela.academico.entity.Grado;
import escuela.academico.entity.NivelEducativo;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class GradoMapper {

    public Grado nuevo(GradoRequest dto, NivelEducativo nivel) {
        Grado entidad = new Grado();
        actualizar(entidad, dto, nivel);
        return entidad;
    }

    public void actualizar(Grado entidad, GradoRequest dto, NivelEducativo nivel) {
        entidad.setNivelEducativo(nivel);
        entidad.setCodigo(codigo(dto.codigo()));
        entidad.setNombre(limpiar(dto.nombre()));
        entidad.setOrden(dto.orden());
        entidad.setActivo(dto.activo());
    }

    public GradoResponse respuesta(Grado e) {
        return new GradoResponse(e.getId(), e.getNivelEducativo().getId(), e.getCodigo(), e.getNombre(),
                e.getOrden(), e.isActivo(), desde(e));
    }
}
