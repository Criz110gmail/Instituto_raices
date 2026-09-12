package escuela.seguridad.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Plantel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario_rol")
public class UsuarioRol extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private AlcanceRol alcance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantel_id")
    private Plantel plantel;

    @Column(nullable = false)
    private boolean activo = true;
}
