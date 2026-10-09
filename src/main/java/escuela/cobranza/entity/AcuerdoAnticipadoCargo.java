package escuela.cobranza.entity;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Getter @Setter @Entity @Table(name="acuerdo_anticipado_cargo")
public class AcuerdoAnticipadoCargo extends EntidadAuditable {
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="acuerdo_id",nullable=false) private AcuerdoAnticipado acuerdo;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cargo_id",nullable=false) private Cargo cargo;
 private boolean activo=true;
 @Column(precision=14,scale=2) private BigDecimal original;
 @Column(precision=14,scale=2) private BigDecimal totalActual;
 @Column(precision=14,scale=2) private BigDecimal beca;
 @Column(precision=14,scale=2) private BigDecimal base;
 @Column(precision=14,scale=2) private BigDecimal beneficio;
 @Column(precision=14,scale=2) private BigDecimal pagar;
}
