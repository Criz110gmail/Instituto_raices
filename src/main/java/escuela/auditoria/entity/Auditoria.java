package escuela.auditoria.entity;

import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Getter
@Entity
@Immutable
@Table(name = "auditoria")
public class Auditoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_actor_id")
    private Usuario usuarioActor;
    @Column(name = "actor_sistema", length = 80)
    private String actorSistema;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 60)
    private AccionAuditoria accion;
    @Column(name = "tipo_entidad", nullable = false, length = 80)
    private String tipoEntidad;
    @Column(name = "entidad_id", nullable = false, length = 100)
    private String entidadId;
    @Column(name = "ocurrido_en", nullable = false, updatable = false)
    private Instant ocurridoEn;
    @Column(columnDefinition = "text")
    private String motivo;
    @Column(columnDefinition = "text")
    private String cambios;
    @Column(name = "correlacion_id", nullable = false, length = 64)
    private String correlacionId;

    protected Auditoria() { }

    public static Auditoria crear(Institucion institucion, Usuario usuarioActor, String actorSistema,
                                  AccionAuditoria accion, String tipoEntidad, String entidadId,
                                  String motivo, String cambios, String correlacionId) {
        Auditoria auditoria = new Auditoria();
        auditoria.institucion = institucion;
        auditoria.usuarioActor = usuarioActor;
        auditoria.actorSistema = usuarioActor == null ? actorSistema : null;
        auditoria.accion = accion;
        auditoria.tipoEntidad = tipoEntidad;
        auditoria.entidadId = entidadId;
        auditoria.ocurridoEn = Instant.now();
        auditoria.motivo = motivo;
        auditoria.cambios = cambios;
        auditoria.correlacionId = correlacionId;
        return auditoria;
    }
}
