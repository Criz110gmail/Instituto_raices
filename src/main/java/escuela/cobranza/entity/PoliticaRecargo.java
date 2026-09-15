package escuela.cobranza.entity;

import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @Entity @Table(name="politica_recargo")
public class PoliticaRecargo extends EntidadAuditable {
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="concepto_cobro_id",nullable=false,unique=true) private ConceptoCobro conceptoCobro;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=12) private ModalidadBeca modalidad;
 @Column(precision=7,scale=4) private BigDecimal porcentaje;
 @Column(name="monto_fijo",precision=14,scale=2) private BigDecimal montoFijo;
 @Column(length=3) private String moneda;
 @Column(name="dias_gracia",nullable=false) private int diasGracia;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) private PeriodicidadRecargo periodicidad;
 @Enumerated(EnumType.STRING) @Column(name="tipo_limite",nullable=false,length=20) private TipoLimiteRecargo tipoLimite;
 @Column(name="valor_limite",precision=14,scale=4) private BigDecimal valorLimite;
 @Column(name="generacion_automatica",nullable=false) private boolean generacionAutomatica=true;
 @Column(nullable=false) private boolean activo=true;
}
