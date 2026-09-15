package escuela.cobranza.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @Entity @Table(name = "ajuste_cargo")
public class AjusteCargo extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cargo_id", nullable = false) private Cargo cargo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12) private TipoAjusteCargo tipo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 11) private EfectoAjusteCargo efecto;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal monto;
    @Column(name = "base_calculo", precision = 14, scale = 2) private BigDecimal baseCalculo;
    @Column(name = "porcentaje_aplicado", precision = 7, scale = 4) private BigDecimal porcentajeAplicado;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "beca_alumno_id") private BecaAlumno becaAlumno;
    @Column(nullable = false, length = 2000) private String motivo;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "autorizado_por_id") private Usuario autorizadoPor;
    @Column(name = "fecha_efectiva", nullable = false) private LocalDate fechaEfectiva;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reversa_de_id", unique = true) private AjusteCargo reversaDe;
    @OneToOne(mappedBy = "reversaDe", fetch = FetchType.LAZY) private AjusteCargo reversa;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "politica_recargo_id") private PoliticaRecargo politicaRecargo;
    @Column(name = "clave_generacion", length = 180) private String claveGeneracion;
}
