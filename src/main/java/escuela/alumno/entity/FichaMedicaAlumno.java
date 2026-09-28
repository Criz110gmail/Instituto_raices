package escuela.alumno.entity;

import escuela.config.audit.EntidadAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ficha_medica_alumno")
public class FichaMedicaAlumno extends EntidadAuditable {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alumno_id", nullable = false, unique = true)
    private Alumno alumno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sanguineo", nullable = false, length = 20)
    private TipoSanguineo tipoSanguineo = TipoSanguineo.DESCONOCIDO;

    @Column(columnDefinition = "text") private String alergias;
    @Column(columnDefinition = "text") private String padecimientos;
    @Column(columnDefinition = "text") private String medicamentos;
    @Column(name = "discapacidad_necesidades", columnDefinition = "text") private String discapacidadNecesidades;
    @Column(name = "restricciones_fisicas", columnDefinition = "text") private String restriccionesFisicas;
    @Column(name = "restricciones_alimentarias", columnDefinition = "text") private String restriccionesAlimentarias;
    @Column(name = "servicio_medico", length = 150) private String servicioMedico;
    @Column(name = "numero_afiliacion", length = 100) private String numeroAfiliacion;
    @Column(name = "medico_tratante", length = 180) private String medicoTratante;
    @Column(name = "contacto_emergencia", length = 180) private String contactoEmergencia;
    @Column(name = "telefono_emergencia", length = 30) private String telefonoEmergencia;
    @Column(columnDefinition = "text") private String observaciones;
    @Column(name = "autoriza_atencion_emergencia", nullable = false) private boolean autorizaAtencionEmergencia;
}

