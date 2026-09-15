package escuela.cobranza.mapper;

import escuela.cobranza.dto.response.AjusteCargoResponse;
import escuela.cobranza.entity.AjusteCargo;
import org.springframework.stereotype.Component;
import java.util.stream.*;
import static escuela.common.mapper.AuditoriaMapper.desde;

@Component
public class AjusteCargoMapper {
    public AjusteCargoResponse respuesta(AjusteCargo a) {
        var cargo=a.getCargo(); var alumno=cargo.getInscripcion().getAlumno();
        return new AjusteCargoResponse(a.getId(), cargo.getId(), alumno.getInstitucion().getId(),
                cargo.getInscripcion().getPlantel().getId(), alumno.getMatricula(),
                Stream.of(alumno.getNombres(),alumno.getPrimerApellido(),alumno.getSegundoApellido())
                        .filter(v->v!=null&&!v.isBlank()).collect(Collectors.joining(" ")),
                cargo.getConceptoCobro().getNombre(), a.getTipo(), a.getEfecto(), a.getMonto(),
                a.getBaseCalculo(), a.getPorcentajeAplicado(),
                a.getBecaAlumno()==null?null:a.getBecaAlumno().getId(), a.getMotivo(),
                a.getAutorizadoPor()==null?"Acceso de recuperación":a.getAutorizadoPor().getUsername(),
                a.getFechaEfectiva(), a.getReversaDe()==null?null:a.getReversaDe().getId(),
                a.getReversa()!=null, desde(a));
    }
}
