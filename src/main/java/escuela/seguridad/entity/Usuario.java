package escuela.seguridad.entity;

import escuela.archivo.entity.Archivo;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @Column(nullable = false, length = 80)
    private String username;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUsuario estado = EstadoUsuario.INVITADO;

    @Column(name = "ultimo_acceso_en")
    private Instant ultimoAccesoEn;

    @Column(name = "bloqueo_hasta")
    private Instant bloqueoHasta;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fotografia_archivo_id")
    private Archivo fotografiaArchivo;
}
