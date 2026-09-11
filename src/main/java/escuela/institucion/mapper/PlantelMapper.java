package escuela.institucion.mapper;

import escuela.institucion.dto.request.PlantelRequest;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.email;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class PlantelMapper {

    public Plantel nuevo(PlantelRequest dto, Institucion institucion) {
        Plantel entidad = new Plantel();
        actualizar(entidad, dto, institucion);
        return entidad;
    }

    public void actualizar(Plantel entidad, PlantelRequest dto, Institucion institucion) {
        entidad.setInstitucion(institucion);
        entidad.setCodigo(codigo(dto.codigo()));
        entidad.setNombre(limpiar(dto.nombre()));
        entidad.setTelefono(limpiar(dto.telefono()));
        entidad.setEmail(email(dto.email()));
        entidad.setCalle(limpiar(dto.calle()));
        entidad.setNumeroExterior(limpiar(dto.numeroExterior()));
        entidad.setNumeroInterior(limpiar(dto.numeroInterior()));
        entidad.setColonia(limpiar(dto.colonia()));
        entidad.setCiudad(limpiar(dto.ciudad()));
        entidad.setEstado(limpiar(dto.estado()));
        entidad.setCodigoPostal(limpiar(dto.codigoPostal()));
        entidad.setPais(codigo(dto.pais()));
        entidad.setActivo(dto.activo());
    }

    public PlantelResponse respuesta(Plantel e) {
        return new PlantelResponse(e.getId(), e.getInstitucion().getId(), e.getCodigo(), e.getNombre(),
                e.getTelefono(), e.getEmail(), e.getCalle(), e.getNumeroExterior(), e.getNumeroInterior(),
                e.getColonia(), e.getCiudad(), e.getEstado(), e.getCodigoPostal(), e.getPais(), e.isActivo(), desde(e));
    }
}
