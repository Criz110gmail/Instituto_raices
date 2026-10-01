package escuela.compras.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @Entity @Table(name="compra_detalle")
public class CompraDetalle {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="compra_id",nullable=false) private Compra compra;
    @Column(nullable=false,length=300) private String descripcion;
    @Column(nullable=false,precision=12,scale=3) private BigDecimal cantidad;
    @Column(name="precio_unitario",nullable=false,precision=19,scale=2) private BigDecimal precioUnitario;
    @Column(name="porcentaje_impuesto",nullable=false,precision=5,scale=2) private BigDecimal porcentajeImpuesto;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal subtotal;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal impuesto;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal total;
    @Column(nullable=false) private Integer orden;
}
