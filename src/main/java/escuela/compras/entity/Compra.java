package escuela.compras.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.finanzas.entity.*;
import escuela.institucion.entity.*;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Getter @Setter @Entity @Table(name="compra")
public class Compra extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="institucion_id",nullable=false) private Institucion institucion;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="plantel_id") private Plantel plantel;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="proveedor_id",nullable=false) private Proveedor proveedor;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="cuenta_id",nullable=false) private CuentaFinanciera cuenta;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="motivo_financiero_id",nullable=false) private MotivoFinanciero motivoFinanciero;
    @Column(nullable=false,length=40) private String folio;
    @Column(name="documento_referencia",length=150) private String documentoReferencia;
    @Column(name="fecha_operacion",nullable=false) private Instant fechaOperacion;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EstadoCompra estado=EstadoCompra.BORRADOR;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal subtotal;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal impuesto;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal total;
    @Column(length=2000) private String observaciones;
    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="movimiento_egreso_id") private MovimientoFinanciero movimientoEgreso;
    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="reversion_financiera_id") private ReversionFinanciera reversionFinanciera;
    @Column(name="cancelada_en") private Instant canceladaEn;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cancelada_por_id") private Usuario canceladaPor;
    @Column(name="motivo_cancelacion",length=2000) private String motivoCancelacion;
    @OneToMany(mappedBy="compra",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("orden ASC,id ASC") private List<CompraDetalle> partidas=new ArrayList<>();
}
