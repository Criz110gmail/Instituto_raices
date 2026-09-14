package escuela.cobranza.mapper;

import escuela.cobranza.dto.request.ConceptoCobroRequest;
import escuela.cobranza.dto.response.ConceptoCobroResponse;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.institucion.entity.Institucion;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class ConceptoCobroMapper {

    public ConceptoCobro nuevo(ConceptoCobroRequest request, Institucion institucion) {
        ConceptoCobro entidad = new ConceptoCobro();
        entidad.setInstitucion(institucion);
        actualizar(entidad, request);
        return entidad;
    }

    public void actualizar(ConceptoCobro entidad, ConceptoCobroRequest request) {
        entidad.setCodigo(codigo(request.codigo()));
        entidad.setNombre(limpiar(request.nombre()));
        entidad.setDescripcion(limpiar(request.descripcion()));
        entidad.setCategoria(request.categoria());
        entidad.setPermiteBeca(request.permiteBeca());
        entidad.setPermiteDescuento(request.permiteDescuento());
        entidad.setPermiteRecargo(request.permiteRecargo());
        entidad.setActivo(request.activo());
    }

    public ConceptoCobroResponse respuesta(ConceptoCobro entidad) {
        return new ConceptoCobroResponse(entidad.getId(), entidad.getInstitucion().getId(),
                entidad.getInstitucion().getNombre(), entidad.getCodigo(), entidad.getNombre(),
                entidad.getDescripcion(), entidad.getCategoria(), entidad.isPermiteBeca(),
                entidad.isPermiteDescuento(), entidad.isPermiteRecargo(), entidad.isActivo(),
                desde(entidad));
    }
}
