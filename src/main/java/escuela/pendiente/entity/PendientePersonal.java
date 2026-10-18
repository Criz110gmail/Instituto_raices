package escuela.pendiente.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.*;

@Entity @Table(name="pendiente_personal") @Getter @Setter
public class PendientePersonal extends EntidadAuditable {
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="usuario_id",nullable=false,updatable=false)
 private Usuario usuario;
 @Column(nullable=false,length=180) private String titulo;
 @Column(length=4000) private String descripcion;
 @Column(name="fecha_compromiso",nullable=false) private LocalDate fechaCompromiso;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EstadoPendiente estado=EstadoPendiente.PENDIENTE;
 @Column(name="realizado_en") private Instant realizadoEn;
}
