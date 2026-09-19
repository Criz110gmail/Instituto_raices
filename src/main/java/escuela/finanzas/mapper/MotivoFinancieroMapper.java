package escuela.finanzas.mapper;

import escuela.finanzas.dto.request.MotivoFinancieroRequest;
import escuela.finanzas.dto.response.MotivoFinancieroResponse;
import escuela.finanzas.entity.MotivoFinanciero;
import escuela.institucion.entity.Institucion;
import org.springframework.stereotype.Component;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class MotivoFinancieroMapper {
    public MotivoFinanciero nuevo(MotivoFinancieroRequest request, Institucion institucion) {
        MotivoFinanciero motivo = new MotivoFinanciero();
        motivo.setInstitucion(institucion);
        actualizar(motivo, request);
        return motivo;
    }

    public void actualizar(MotivoFinanciero motivo, MotivoFinancieroRequest request) {
        motivo.setCodigo(codigo(request.codigo()));
        motivo.setNombre(limpiar(request.nombre()));
        motivo.setNaturaleza(request.naturaleza());
        motivo.setCategoria(codigo(request.categoria()));
        motivo.setActivo(request.activo());
    }

    public MotivoFinancieroResponse respuesta(MotivoFinanciero motivo) {
        return new MotivoFinancieroResponse(motivo.getId(), motivo.getInstitucion().getId(),
                motivo.getInstitucion().getNombre(), motivo.getCodigo(), motivo.getNombre(),
                motivo.getNaturaleza(), motivo.getCategoria(), motivo.isActivo(), desde(motivo));
    }
}
