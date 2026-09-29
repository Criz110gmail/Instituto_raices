package escuela.docente.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter @Entity @Table(name="planeacion_historial")
public class PlaneacionHistorial extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="planeacion_id", nullable=false) private PlaneacionSemanal planeacion;
    @Enumerated(EnumType.STRING) @Column(name="estado_anterior", length=25) private EstadoPlaneacion estadoAnterior;
    @Enumerated(EnumType.STRING) @Column(name="estado_nuevo", nullable=false, length=25) private EstadoPlaneacion estadoNuevo;
    @Column(columnDefinition="text") private String motivo;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="actor_id") private Usuario actor;
    @Column(name="ocurrido_en", nullable=false) private Instant ocurridoEn;
}
