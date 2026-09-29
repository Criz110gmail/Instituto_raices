package escuela.docente.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.seguridad.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter @Entity @Table(name="planeacion_version_publicada")
public class PlaneacionVersionPublicada extends EntidadAuditable {
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="planeacion_id", nullable=false) private PlaneacionSemanal planeacion;
    @Column(name="numero_revision", nullable=false) private int numeroRevision;
    @Column(name="contenido_json", nullable=false, columnDefinition="text") private String contenidoJson;
    @Column(name="publicada_en", nullable=false) private Instant publicadaEn;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="publicada_por_id") private Usuario publicadaPor;
}
