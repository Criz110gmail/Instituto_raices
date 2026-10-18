package escuela.pendiente.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;

@Entity @Table(name="pendiente_personal_cambio") @Getter
public class PendienteCambio {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="pendiente_id",nullable=false) private PendientePersonal pendiente;
 @Column(nullable=false,length=30) private String accion;
 @Column(nullable=false,length=20) private String estado;
 @Column(name="ocurrido_en",nullable=false) private Instant ocurridoEn;
 @Column(name="version_pendiente",nullable=false) private Long versionPendiente;
 protected PendienteCambio(){}
 public PendienteCambio(PendientePersonal p,String accion){this.pendiente=p;this.accion=accion;this.estado=p.getEstado().name();this.ocurridoEn=Instant.now();this.versionPendiente=p.getVersion();}
}
