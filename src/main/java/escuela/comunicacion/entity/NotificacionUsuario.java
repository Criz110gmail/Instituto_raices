package escuela.comunicacion.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.finanzas.entity.Pago;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter @Entity @Table(name="notificacion_usuario",
        uniqueConstraints=@UniqueConstraint(name="ux_notificacion_usuario_clave",columnNames={"usuario_id","clave_deduplicacion"}))
public class NotificacionUsuario extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="usuario_id",nullable=false) private Usuario usuario;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TipoNotificacion tipo;
    @Column(nullable=false,length=200) private String titulo;
    @Column(nullable=false,length=1000) private String mensaje;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="evento_id") private EventoEscolar evento;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="aviso_id") private Aviso aviso;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="pago_id") private Pago pago;
    @Column(name="leida_en") private Instant leidaEn;
    @Column(name="clave_deduplicacion",nullable=false,length=100) private String claveDeduplicacion;
}
