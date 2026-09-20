package escuela.comunicacion.entity;

import escuela.academico.entity.*;
import escuela.alumno.entity.Alumno;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name = "destinatario_evento")
public class DestinatarioEvento extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false) private EventoEscolar evento;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "nivel_educativo_id") private NivelEducativo nivelEducativo;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "grado_id") private Grado grado;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "grupo_id") private Grupo grupo;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "alumno_id") private Alumno alumno;
}
