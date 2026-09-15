package escuela.finanzas.entity;

import escuela.archivo.entity.Archivo;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comprobante_pago")
public class ComprobantePago extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "archivo_id", nullable = false)
    private Archivo archivo;

    @Column(length = 1000)
    private String observaciones;
}
