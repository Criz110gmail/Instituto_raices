package escuela.alumno.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.tutor.entity.Tutor;
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

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "alumno_tutor")
public class AlumnoTutor extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParentescoTutor parentesco;

    @Column(name = "parentesco_otro", length = 100)
    private String parentescoOtro;

    @Column(name = "es_contacto_principal", nullable = false)
    private boolean contactoPrincipal;

    @Column(name = "es_responsable_financiero", nullable = false)
    private boolean responsableFinanciero;

    @Column(name = "puede_autorizar", nullable = false)
    private boolean puedeAutorizar;

    @Column(name = "puede_recoger", nullable = false)
    private boolean puedeRecoger;

    @Column(name = "puede_ver_finanzas", nullable = false)
    private boolean puedeVerFinanzas;

    @Column(name = "puede_recibir_notificaciones", nullable = false)
    private boolean puedeRecibirNotificaciones = true;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(length = 4000)
    private String observaciones;

    @Column(nullable = false)
    private boolean activo = true;
}
