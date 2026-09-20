package escuela.admin.dto;

import escuela.comunicacion.dto.request.*;
import escuela.comunicacion.dto.response.EventoEscolarResponse;
import escuela.comunicacion.entity.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.*;

@Getter @Setter
public class EventoEscolarForm {
    @NotNull private Long institucionId;
    @NotNull private Long cicloEscolarId;
    private Long plantelId;
    @NotBlank @Size(max = 200) private String titulo;
    @NotBlank @Size(max = 250) private String ubicacion;
    @Size(max = 5000) private String descripcion;
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") private LocalDateTime inicioLocal;
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") private LocalDateTime finLocal;
    @NotNull private TipoEventoEscolar tipo = TipoEventoEscolar.ACTIVIDAD;
    @NotNull private AlcanceEventoEscolar alcance = AlcanceEventoEscolar.INSTITUCION;
    private List<String> destinatarios = new ArrayList<>();
    private Long version;

    public EventoEscolarRequest request() {
        List<DestinatarioEventoRequest> destinos = destinatarios == null ? List.of()
                : destinatarios.stream().filter(Objects::nonNull).map(EventoEscolarForm::destino).toList();
        return new EventoEscolarRequest(institucionId, cicloEscolarId, plantelId, titulo,
                ubicacion, descripcion, inicioLocal, finLocal, tipo, alcance, destinos, version);
    }

    public static EventoEscolarForm desde(EventoEscolarResponse r) {
        EventoEscolarForm f = new EventoEscolarForm();
        f.institucionId = r.institucionId(); f.cicloEscolarId = r.cicloEscolarId(); f.plantelId = r.plantelId();
        f.titulo = r.titulo(); f.ubicacion = r.ubicacion(); f.descripcion = r.descripcion();
        f.inicioLocal = r.inicioLocal(); f.finLocal = r.finLocal(); f.tipo = r.tipo(); f.alcance = r.alcance();
        f.destinatarios = r.destinatarios().stream().map(d -> d.tipo().name() + ":" + d.id()).toList();
        f.version = r.auditoria().version(); return f;
    }

    public static DestinatarioEventoRequest destino(String clave) {
        if (clave == null || !clave.matches("^(NIVEL|GRADO|GRUPO|ALUMNO):[1-9][0-9]*$"))
            throw new IllegalArgumentException("Destinatario de evento inválido");
        String[] partes = clave.split(":", 2);
        return new DestinatarioEventoRequest(TipoDestinatarioEvento.valueOf(partes[0]), Long.valueOf(partes[1]));
    }
}
