package escuela.cobranza.entity;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.*;
import escuela.tutor.entity.Tutor;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
@Getter @Setter @Entity @Table(name="acuerdo_anticipado")
public class AcuerdoAnticipado extends EntidadAuditable {
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="institucion_id",nullable=false) private Institucion institucion;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="plantel_id",nullable=false) private Plantel plantel;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tutor_id",nullable=false) private Tutor tutor;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="autorizado_por_id",nullable=false) private Usuario autorizadoPor;
 @Column(length=40,nullable=false) private String folio;
 private LocalDate fechaLimite;
 @Column(length=15) private String politicaBeca;
 @Column(length=15) private String tipoBeneficio;
 @Column(precision=16,scale=4) private BigDecimal valorBeneficio;
 private Long cargoBonificadoId;
 @Column(length=2000) private String motivo;
 @Column(length=3) private String moneda;
 @Column(precision=14,scale=2) private BigDecimal totalBase;
 @Column(precision=14,scale=2) private BigDecimal totalBeneficio;
 @Column(precision=14,scale=2) private BigDecimal totalPagar;
 @Column(length=15) private String estado="PROPUESTO";
 private Instant aplicadoEn;
 private Instant canceladoEn;
 @Column(length=2000) private String motivoCancelacion;
 @OneToMany(mappedBy="acuerdo") @OrderBy("id ASC") private List<AcuerdoAnticipadoCargo> cargos=new ArrayList<>();
}
