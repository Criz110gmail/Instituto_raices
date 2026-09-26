package escuela.tutor.entity;

import escuela.archivo.entity.Archivo;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tutor_identificacion")
public class TutorIdentificacion extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "archivo_id", nullable = false, unique = true)
    private Archivo archivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoIdentificacionTutor tipo;

    @Column(name = "retirada_en")
    private Instant retiradaEn;
}
