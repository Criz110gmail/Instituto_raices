package escuela.finanzas.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "reversion_financiera")
public class ReversionFinanciera extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false) private Institucion institucion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20) private TipoReversionFinanciera tipo;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_origen_id") private MovimientoFinanciero movimientoOrigen;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferencia_origen_id") private TransferenciaCuenta transferenciaOrigen;
    @Column(nullable = false) private Instant fecha;
    @Column(nullable = false, length = 2000) private String motivo;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autorizado_por_id", nullable = false) private Usuario autorizadoPor;
    @Column(name = "clave_idempotencia", nullable = false, length = 120) private String claveIdempotencia;
    @OneToMany(mappedBy = "reversionFinanciera", fetch = FetchType.LAZY)
    @OrderBy("id ASC") private List<MovimientoFinanciero> movimientos = new ArrayList<>();
}
