package escuela.comunicacion.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter @Entity @Table(name = "aviso")
public class Aviso extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false) private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantel_id") private Plantel plantel;
    @Column(nullable = false, length = 200) private String titulo;
    @Column(nullable = false, columnDefinition = "text") private String contenido;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10)
    private EstadoAviso estado = EstadoAviso.BORRADOR;
    @Column(name = "publicado_en") private Instant publicadoEn;
    @Column(name = "expira_en") private Instant expiraEn;
    @Column(name = "retirado_en") private Instant retiradoEn;
    @Column(name = "motivo_retiro", length = 1000) private String motivoRetiro;
}
