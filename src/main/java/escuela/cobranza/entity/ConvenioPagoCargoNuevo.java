package escuela.cobranza.entity;
import escuela.inscripcion.entity.Inscripcion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Getter @Setter @Entity @Table(name="convenio_pago_cargo_nuevo")
public class ConvenioPagoCargoNuevo {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="convenio_pago_id") private ConvenioPago convenio;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="cargo_id") private Cargo cargo;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="inscripcion_id") private Inscripcion inscripcion;
 @Column(name="saldo_original_grupo",nullable=false,precision=14,scale=2) private BigDecimal saldoOriginalGrupo;
 @Column(name="monto_acordado",nullable=false,precision=14,scale=2) private BigDecimal montoAcordado;
}
