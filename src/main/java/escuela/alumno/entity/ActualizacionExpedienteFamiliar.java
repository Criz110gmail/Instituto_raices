package escuela.alumno.entity;

import escuela.archivo.entity.Archivo;
import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.Usuario;
import escuela.tutor.entity.Tutor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "actualizacion_expediente_familiar")
public class ActualizacionExpedienteFamiliar extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private TipoActualizacionExpediente tipo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EstadoActualizacionExpediente estado = EstadoActualizacionExpediente.ENVIADA;
    @Column(name = "mensaje_tutor", length = 2000) private String mensajeTutor;
    @Column(name = "consentimiento_version", nullable = false, length = 50) private String consentimientoVersion;
    @Column(name = "consentimiento_texto", nullable = false, length = 1000) private String consentimientoTexto;
    @Column(name = "consentimiento_en", nullable = false) private Instant consentimientoEn;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "archivo_id") private Archivo archivo;
    @Enumerated(EnumType.STRING) @Column(name = "tipo_documento", length = 40) private TipoDocumentoAlumno tipoDocumento;
    @Column(name = "descripcion_documento", length = 500) private String descripcionDocumento;
    @Column(name = "fecha_documento") private LocalDate fechaDocumento;
    @Column(name = "vigente_hasta") private LocalDate vigenteHasta;

    @Enumerated(EnumType.STRING) @Column(name = "tipo_sanguineo", length = 20) private TipoSanguineo tipoSanguineo;
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
    @Column(name = "observaciones_medicas", columnDefinition = "text") private String observacionesMedicas;
    @Column(name = "autoriza_atencion_emergencia") private Boolean autorizaAtencionEmergencia;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "revisado_por_id") private Usuario revisadoPor;
    @Column(name = "revisado_en") private Instant revisadoEn;
    @Column(name = "respuesta_admin", length = 2000) private String respuestaAdmin;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "documento_aplicado_id") private AlumnoDocumento documentoAplicado;
    @Column(name = "ficha_medica_version_aplicada") private Long fichaMedicaVersionAplicada;
}
