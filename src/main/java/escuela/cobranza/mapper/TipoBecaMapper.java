package escuela.cobranza.mapper;

import escuela.cobranza.dto.request.TipoBecaRequest;
import escuela.cobranza.dto.response.TipoBecaResponse;
import escuela.cobranza.entity.TipoBeca;
import escuela.institucion.entity.Institucion;
import org.springframework.stereotype.Component;
import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.*;

@Component
public class TipoBecaMapper {
    public TipoBeca nuevo(TipoBecaRequest r, Institucion institucion) {
        TipoBeca t = new TipoBeca(); t.setInstitucion(institucion); actualizar(t, r); return t;
    }
    public void actualizar(TipoBeca t, TipoBecaRequest r) {
        t.setCodigo(codigo(r.codigo())); t.setNombre(limpiar(r.nombre()));
        t.setDescripcion(limpiar(r.descripcion())); t.setActivo(r.activo());
    }
    public TipoBecaResponse respuesta(TipoBeca t) {
        return new TipoBecaResponse(t.getId(), t.getInstitucion().getId(), t.getInstitucion().getNombre(),
                t.getCodigo(), t.getNombre(), t.getDescripcion(), t.isActivo(), desde(t));
    }
}
