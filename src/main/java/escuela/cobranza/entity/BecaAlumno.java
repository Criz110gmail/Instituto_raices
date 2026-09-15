package escuela.cobranza.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.inscripcion.entity.Inscripcion;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @Entity @Table(name = "beca_alumno")
public class BecaAlumno extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inscripcion_id", nullable = false) private Inscripcion inscripcion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_beca_id", nullable = false) private TipoBeca tipoBeca;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concepto_cobro_id", nullable = false) private ConceptoCobro conceptoCobro;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12) private ModalidadBeca modalidad;
    @Column(precision = 7, scale = 4) private BigDecimal porcentaje;
    @Column(name = "monto_fijo", precision = 14, scale = 2) private BigDecimal montoFijo;
    @Column(length = 3) private String moneda;
    @Column(name = "fecha_inicio", nullable = false) private LocalDate fechaInicio;
    @Column(name = "fecha_fin", nullable = false) private LocalDate fechaFin;
    @Column(nullable = false, length = 2000) private String motivo;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "autorizado_por_id") private Usuario autorizadoPor;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12) private EstadoBeca estado = EstadoBeca.ACTIVA;
}
