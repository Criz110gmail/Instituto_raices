package escuela.academico.mapper;

import escuela.academico.dto.request.CicloEscolarRequest;
import escuela.academico.dto.response.CicloEscolarResponse;
import escuela.academico.entity.CicloEscolar;
import escuela.institucion.entity.Institucion;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class CicloEscolarMapper {

    public CicloEscolar nuevo(CicloEscolarRequest dto, Institucion institucion) {
        CicloEscolar entidad = new CicloEscolar();
        actualizar(entidad, dto, institucion);
        return entidad;
    }

    public void actualizar(CicloEscolar entidad, CicloEscolarRequest dto, Institucion institucion) {
        entidad.setInstitucion(institucion);
        entidad.setCodigo(codigo(dto.codigo()));
        entidad.setNombre(limpiar(dto.nombre()));
        entidad.setFechaInicio(dto.fechaInicio());
        entidad.setFechaFin(dto.fechaFin());
        entidad.setEstado(dto.estado());
        entidad.setPredeterminado(dto.predeterminado());
    }

    public CicloEscolarResponse respuesta(CicloEscolar e) {
        return new CicloEscolarResponse(e.getId(), e.getInstitucion().getId(), e.getCodigo(), e.getNombre(),
                e.getFechaInicio(), e.getFechaFin(), e.getEstado(), e.isPredeterminado(), desde(e));
    }
}
