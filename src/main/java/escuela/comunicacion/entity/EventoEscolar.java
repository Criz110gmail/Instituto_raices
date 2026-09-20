package escuela.comunicacion.entity;

import escuela.academico.entity.CicloEscolar;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.Instant;
import java.util.*;

@Getter @Setter @Entity @Table(name = "evento_escolar")
public class EventoEscolar extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false) private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ciclo_escolar_id", nullable = false) private CicloEscolar cicloEscolar;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantel_id") private Plantel plantel;
    @Column(nullable = false, length = 200) private String titulo;
    @Column(nullable = false, length = 250) private String ubicacion;
    @Column(columnDefinition = "text") private String descripcion;
    @Column(name = "inicio_en", nullable = false) private Instant inicioEn;
    @Column(name = "fin_en", nullable = false) private Instant finEn;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 15) private TipoEventoEscolar tipo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12)
    private EstadoEventoEscolar estado = EstadoEventoEscolar.BORRADOR;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12) private AlcanceEventoEscolar alcance;
    @Column(name = "publicado_en") private Instant publicadoEn;
    @Column(name = "cancelado_en") private Instant canceladoEn;
    @Column(name = "motivo_cancelacion", length = 2000) private String motivoCancelacion;
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DestinatarioEvento> destinatarios = new ArrayList<>();
    @Formula("(select count(*) from destinatario_evento de where de.evento_id = id)")
    private long cantidadDestinatarios;

    public void reemplazarDestinatarios(Collection<DestinatarioEvento> nuevos) {
        destinatarios.clear();
        nuevos.forEach(destino -> { destino.setEvento(this); destinatarios.add(destino); });
    }
}
