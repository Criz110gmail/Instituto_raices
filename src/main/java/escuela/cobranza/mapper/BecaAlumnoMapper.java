package escuela.cobranza.mapper;

import escuela.cobranza.dto.request.BecaAlumnoRequest;
import escuela.cobranza.dto.response.BecaAlumnoResponse;
import escuela.cobranza.entity.*;
import escuela.inscripcion.entity.Inscripcion;
import escuela.seguridad.entity.Usuario;
import org.springframework.stereotype.Component;
import java.util.stream.*;
import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.*;

@Component
public class BecaAlumnoMapper {
    public BecaAlumno nueva(BecaAlumnoRequest r, Inscripcion i, TipoBeca t,
                            ConceptoCobro c, Usuario autorizado) {
        BecaAlumno b = new BecaAlumno(); b.setInscripcion(i); b.setTipoBeca(t);
        b.setConceptoCobro(c); b.setAutorizadoPor(autorizado); actualizar(b, r); return b;
    }
    public void actualizar(BecaAlumno b, BecaAlumnoRequest r) {
        b.setModalidad(r.modalidad()); b.setPorcentaje(r.porcentaje()); b.setMontoFijo(r.montoFijo());
        b.setMoneda(codigo(r.moneda())); b.setFechaInicio(r.fechaInicio()); b.setFechaFin(r.fechaFin());
        b.setMotivo(limpiar(r.motivo())); b.setEstado(r.estado());
    }
    public BecaAlumnoResponse respuesta(BecaAlumno b) {
        var i=b.getInscripcion(); var a=i.getAlumno();
        return new BecaAlumnoResponse(b.getId(), i.getId(), a.getInstitucion().getId(), i.getPlantel().getId(),
                a.getMatricula(), nombre(a.getNombres(),a.getPrimerApellido(),a.getSegundoApellido()),
                i.getNumeroInscripcion(), b.getTipoBeca().getId(), b.getTipoBeca().getNombre(),
                b.getConceptoCobro().getId(), b.getConceptoCobro().getNombre(), b.getModalidad(),
                b.getPorcentaje(), b.getMontoFijo(), b.getMoneda(), b.getFechaInicio(), b.getFechaFin(),
                b.getMotivo(), b.getEstado(), b.getAutorizadoPor()==null ? "Acceso de recuperación"
                : b.getAutorizadoPor().getUsername(), desde(b));
    }
    private String nombre(String... p){ return Stream.of(p).filter(v->v!=null&&!v.isBlank()).collect(Collectors.joining(" ")); }
}
