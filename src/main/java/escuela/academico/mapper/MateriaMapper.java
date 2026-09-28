package escuela.academico.mapper;

import escuela.academico.dto.request.MateriaRequest;
import escuela.academico.dto.response.MateriaResponse;
import escuela.academico.entity.Materia;
import escuela.institucion.entity.Institucion;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class MateriaMapper {
    public Materia nueva(MateriaRequest dto, Institucion institucion) {
        Materia materia = new Materia();
        actualizar(materia, dto, institucion);
        return materia;
    }

    public void actualizar(Materia materia, MateriaRequest dto, Institucion institucion) {
        materia.setInstitucion(institucion);
        materia.setCodigo(codigo(dto.codigo()));
        materia.setNombre(limpiar(dto.nombre()));
        materia.setDescripcion(limpiar(dto.descripcion()));
        materia.setActivo(dto.activo());
    }

    public MateriaResponse respuesta(Materia materia, long planesActivos) {
        return new MateriaResponse(materia.getId(), materia.getInstitucion().getId(),
                materia.getCodigo(), materia.getNombre(), materia.getDescripcion(),
                materia.isActivo(), planesActivos, desde(materia));
    }
}

