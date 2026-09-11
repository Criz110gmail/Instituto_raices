package escuela.academico.mapper;

import escuela.academico.dto.request.NivelEducativoRequest;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.academico.entity.NivelEducativo;
import escuela.institucion.entity.Institucion;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class NivelEducativoMapper {

    public NivelEducativo nuevo(NivelEducativoRequest dto, Institucion institucion) {
        NivelEducativo entidad = new NivelEducativo();
        actualizar(entidad, dto, institucion);
        return entidad;
    }

    public void actualizar(NivelEducativo entidad, NivelEducativoRequest dto, Institucion institucion) {
        entidad.setInstitucion(institucion);
        entidad.setCodigo(codigo(dto.codigo()));
        entidad.setNombre(limpiar(dto.nombre()));
        entidad.setDescripcion(limpiar(dto.descripcion()));
        entidad.setOrden(dto.orden());
        entidad.setActivo(dto.activo());
    }

    public NivelEducativoResponse respuesta(NivelEducativo e) {
        return new NivelEducativoResponse(e.getId(), e.getInstitucion().getId(), e.getCodigo(), e.getNombre(),
                e.getDescripcion(), e.getOrden(), e.isActivo(), desde(e));
    }
}
