package escuela.cobranza.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.tutor.entity.Tutor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Getter @Setter @Entity @Table(name="convenio_pago")
public class ConvenioPago extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="institucion_id") private Institucion institucion;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="tutor_id") private Tutor tutor;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="concepto_cobro_id") private ConceptoCobro conceptoCobro;
    @Column(nullable=false,length=40,unique=true) private String folio;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30,updatable=false) private ModalidadConvenioPago modalidad=ModalidadConvenioPago.MONTO_ACORDADO;
    @Column(name="autorizado_por_id",updatable=false) private Long autorizadoPorId;
    @Column(name="autorizado_en",updatable=false) private Instant autorizadoEn;
    @Column(name="autorizado_por_nombre",length=180,updatable=false) private String autorizadoPorNombre;
    @Column(name="fecha_acuerdo",nullable=false) private LocalDate fechaAcuerdo;
    @Column(name="fecha_vencimiento",nullable=false) private LocalDate fechaVencimiento;
    @Column(nullable=false,length=250) private String descripcion;
    @Column(nullable=false,columnDefinition="text") private String motivo;
    @Column(columnDefinition="text") private String condiciones;
    @Column(nullable=false,length=3) private String moneda;
    @Column(name="saldo_original_total",nullable=false,precision=14,scale=2) private BigDecimal saldoOriginalTotal;
    @Column(name="monto_acordado_total",nullable=false,precision=14,scale=2) private BigDecimal montoAcordadoTotal;
    @Column(name="monto_condonado_total",nullable=false,precision=14,scale=2) private BigDecimal montoCondonadoTotal;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=15) private EstadoConvenioPago estado=EstadoConvenioPago.VIGENTE;
    @Column(name="cancelado_en") private Instant canceladoEn;
    @Column(name="motivo_cancelacion",columnDefinition="text") private String motivoCancelacion;
    @OneToMany(mappedBy="convenio",fetch=FetchType.LAZY) @OrderBy("id ASC") private List<ConvenioPagoCargoOriginal> cargosOriginales=new ArrayList<>();
    @OneToMany(mappedBy="convenio",fetch=FetchType.LAZY) @OrderBy("id ASC") private List<ConvenioPagoCargoNuevo> cargosNuevos=new ArrayList<>();
}
