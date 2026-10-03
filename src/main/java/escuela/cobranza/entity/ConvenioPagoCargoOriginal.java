package escuela.cobranza.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Getter @Setter @Entity @Table(name="convenio_pago_cargo_original")
public class ConvenioPagoCargoOriginal {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="convenio_pago_id") private ConvenioPago convenio;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="cargo_id") private Cargo cargo;
 @Column(name="importe_total_snapshot",nullable=false,precision=14,scale=2) private BigDecimal importeTotalSnapshot;
 @Column(name="monto_aplicado_snapshot",nullable=false,precision=14,scale=2) private BigDecimal montoAplicadoSnapshot;
 @Column(name="saldo_incluido",nullable=false,precision=14,scale=2) private BigDecimal saldoIncluido;
}
