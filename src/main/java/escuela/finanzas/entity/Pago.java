package escuela.finanzas.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.seguridad.entity.Usuario;
import escuela.tutor.entity.Tutor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pago")
public class Pago extends EntidadAuditable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plantel_registro_id", nullable = false)
    private Plantel plantelRegistro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @Column(name = "nombre_pagador", length = 180)
    private String nombrePagador;

    @Column(nullable = false, length = 50)
    private String folio;

    @Column(name = "clave_idempotencia", nullable = false, length = 100)
    private String claveIdempotencia;

    @Column(name = "fecha_pago", nullable = false)
    private Instant fechaPago;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPago estado = EstadoPago.PENDIENTE_VALIDACION;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_declarada_id")
    private CuentaFinanciera cuentaDeclarada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_destino_id")
    private CuentaFinanciera cuentaDestino;

    @Column(length = 150)
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validado_por_id")
    private Usuario validadoPor;

    @Column(name = "validado_en")
    private Instant validadoEn;

    @Column(name = "motivo_rechazo_cancelacion", length = 2000)
    private String motivoRechazoCancelacion;

    @Column(name = "cancelado_en")
    private Instant canceladoEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelado_por_id")
    private Usuario canceladoPor;

    @Column(length = 4000)
    private String observaciones;

    @OneToMany(mappedBy = "pago", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<ComprobantePago> comprobantes = new ArrayList<>();

    @OneToMany(mappedBy = "pago", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<SolicitudAplicacionPago> solicitudes = new ArrayList<>();

    @OneToMany(mappedBy = "pago", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<AplicacionPago> aplicaciones = new ArrayList<>();

    @OneToMany(mappedBy = "pago", fetch = FetchType.LAZY)
    @OrderBy("fecha DESC, id DESC")
    private List<DevolucionPago> devoluciones = new ArrayList<>();

    @OneToOne(mappedBy = "pago", fetch = FetchType.LAZY)
    private MovimientoFinanciero movimiento;
}
