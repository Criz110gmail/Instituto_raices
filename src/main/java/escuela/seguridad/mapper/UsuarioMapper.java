package escuela.seguridad.mapper;

import escuela.institucion.entity.Institucion;
import escuela.seguridad.dto.request.UsuarioRequest;
import escuela.seguridad.dto.response.UsuarioResponse;
import escuela.seguridad.entity.Usuario;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.email;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class UsuarioMapper {

    public Usuario nuevo(UsuarioRequest dto, Institucion institucion) {
        Usuario entidad = new Usuario();
        actualizar(entidad, dto, institucion);
        return entidad;
    }

    public void actualizar(Usuario entidad, UsuarioRequest dto, Institucion institucion) {
        entidad.setInstitucion(institucion);
        entidad.setUsername(limpiar(dto.username()));
        entidad.setEmail(email(dto.email()));
    }

    public UsuarioResponse respuesta(Usuario entidad) {
        return new UsuarioResponse(entidad.getId(), entidad.getInstitucion().getId(), entidad.getUsername(),
                entidad.getEmail(), entidad.getEstado(), entidad.getPasswordHash() != null, desde(entidad));
    }
}
