package escuela.comunicacion.mapper;

import escuela.comunicacion.dto.response.AvisoResponse;
import escuela.comunicacion.entity.Aviso;
import org.springframework.stereotype.Component;

import java.time.*;

import static escuela.common.mapper.AuditoriaMapper.desde;

@Component
public class AvisoMapper {
    public AvisoResponse respuesta(Aviso a) {
        ZoneId zona = ZoneId.of(a.getInstitucion().getZonaHoraria());
        return new AvisoResponse(a.getId(), a.getInstitucion().getId(), a.getInstitucion().getNombre(),
                a.getPlantel() == null ? null : a.getPlantel().getId(),
                a.getPlantel() == null ? "Toda la institución" : a.getPlantel().getNombre(),
                a.getTitulo(), a.getContenido(), a.getEstado(), a.getPublicadoEn(),
                a.getExpiraEn() == null ? null : LocalDateTime.ofInstant(a.getExpiraEn(), zona),
                a.getRetiradoEn(), a.getMotivoRetiro(), zona.getId(), desde(a));
    }
}
