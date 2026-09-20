package escuela.comunicacion.mapper;

import escuela.comunicacion.dto.response.*;
import escuela.comunicacion.entity.*;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Comparator;

import static escuela.common.mapper.AuditoriaMapper.desde;

@Component
public class EventoEscolarMapper {
    public EventoEscolarResponse respuesta(EventoEscolar evento) {
        ZoneId zona = ZoneId.of(evento.getInstitucion().getZonaHoraria());
        return new EventoEscolarResponse(evento.getId(), evento.getInstitucion().getId(),
                evento.getInstitucion().getNombre(), evento.getCicloEscolar().getId(),
                evento.getCicloEscolar().getNombre(), evento.getPlantel() == null ? null : evento.getPlantel().getId(),
                evento.getPlantel() == null ? "Institucional" : evento.getPlantel().getNombre(),
                evento.getTitulo(), evento.getUbicacion(), evento.getDescripcion(),
                LocalDateTime.ofInstant(evento.getInicioEn(), zona),
                LocalDateTime.ofInstant(evento.getFinEn(), zona), zona.getId(), evento.getTipo(),
                evento.getEstado(), evento.getAlcance(), evento.getPublicadoEn(), evento.getCanceladoEn(),
                evento.getMotivoCancelacion(), evento.getDestinatarios().stream().map(this::destinatario)
                        .sorted(Comparator.comparing(d -> d.tipo().name() + d.titulo())).toList(), desde(evento));
    }

    private DestinatarioEventoResponse destinatario(DestinatarioEvento d) {
        if (d.getNivelEducativo() != null) return new DestinatarioEventoResponse(TipoDestinatarioEvento.NIVEL,
                d.getNivelEducativo().getId(), d.getNivelEducativo().getCodigo() + " · "
                + d.getNivelEducativo().getNombre(), "Nivel educativo");
        if (d.getGrado() != null) return new DestinatarioEventoResponse(TipoDestinatarioEvento.GRADO,
                d.getGrado().getId(), d.getGrado().getCodigo() + " · " + d.getGrado().getNombre(),
                d.getGrado().getNivelEducativo().getNombre());
        if (d.getGrupo() != null) return new DestinatarioEventoResponse(TipoDestinatarioEvento.GRUPO,
                d.getGrupo().getId(), d.getGrupo().getNombre() + " · " + d.getGrupo().getTurno(),
                d.getGrupo().getPlantel().getNombre());
        var a = d.getAlumno();
        String nombre = a.getNombres() + " " + a.getPrimerApellido()
                + (a.getSegundoApellido() == null ? "" : " " + a.getSegundoApellido());
        return new DestinatarioEventoResponse(TipoDestinatarioEvento.ALUMNO, a.getId(),
                a.getMatricula() + " · " + nombre, "Alumno");
    }
}
