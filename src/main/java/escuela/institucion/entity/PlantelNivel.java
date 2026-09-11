package escuela.institucion.entity;

import escuela.academico.entity.NivelEducativo;
import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "plantel_nivel")
public class PlantelNivel extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plantel_id", nullable = false)
    private Plantel plantel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nivel_educativo_id", nullable = false)
    private NivelEducativo nivelEducativo;

    @Column(name = "clave_centro_trabajo", length = 30)
    private String claveCentroTrabajo;

    @Column(nullable = false)
    private boolean activo;
}
