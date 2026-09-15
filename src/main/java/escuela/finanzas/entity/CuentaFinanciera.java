package escuela.finanzas.entity;

import escuela.config.audit.EntidadAuditable;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "cuenta_financiera")
public class CuentaFinanciera extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantel_id")
    private Plantel plantel;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoCuentaFinanciera tipo;

    @Column(name = "banco_nombre", length = 120)
    private String bancoNombre;

    @Column(length = 150)
    private String titular;

    @Column(name = "numero_cuenta", length = 34)
    private String numeroCuenta;

    @Column(length = 18)
    private String clabe;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Column(name = "saldo_inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "fecha_saldo_inicial", nullable = false)
    private LocalDate fechaSaldoInicial;

    @Column(nullable = false)
    private boolean activo = true;
}
