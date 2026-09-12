package escuela.seguridad.mapper;

import escuela.institucion.entity.Institucion;
import escuela.seguridad.dto.request.RolRequest;
import escuela.seguridad.dto.response.RolResponse;
import escuela.seguridad.entity.Rol;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class RolMapper {

    public Rol nuevo(RolRequest dto, Institucion institucion) {
        Rol entidad = new Rol();
        actualizar(entidad, dto, institucion);
        return entidad;
    }

    public void actualizar(Rol entidad, RolRequest dto, Institucion institucion) {
        entidad.setInstitucion(institucion);
        entidad.setCodigo(codigo(dto.codigo()));
        entidad.setNombre(limpiar(dto.nombre()));
        entidad.setDescripcion(limpiar(dto.descripcion()));
        entidad.setActivo(dto.activo());
    }

    public RolResponse respuesta(Rol entidad) {
        return new RolResponse(entidad.getId(), entidad.getInstitucion().getId(), entidad.getCodigo(),
                entidad.getNombre(), entidad.getDescripcion(), entidad.isActivo(), desde(entidad));
    }
}
