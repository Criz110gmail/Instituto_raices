package escuela.admin.dto;

import escuela.finanzas.dto.request.CuentaFinancieraRequest;
import escuela.finanzas.dto.response.CuentaFinancieraResponse;
import escuela.finanzas.entity.TipoCuentaFinanciera;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CuentaFinancieraForm {
    @NotNull private Long institucionId;
    private Long plantelId;
    @NotBlank @Size(max = 50) private String codigo;
    @NotBlank @Size(max = 150) private String nombre;
    @NotNull private TipoCuentaFinanciera tipo = TipoCuentaFinanciera.BANCO;
    @Size(max = 120) private String bancoNombre;
    @Size(max = 150) private String titular;
    @Size(max = 34) private String numeroCuenta;
    @Pattern(regexp = "^$|[0-9]{18}", message = "La CLABE debe contener exactamente 18 dígitos")
    private String clabe;
    @NotBlank @Size(min = 3, max = 3) private String moneda;
    @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO.setScale(2);
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaSaldoInicial = LocalDate.now();
    private boolean activo = true;
    private Long version;

    public CuentaFinancieraRequest request() {
        boolean caja = tipo == TipoCuentaFinanciera.CAJA;
        return new CuentaFinancieraRequest(institucionId, plantelId, codigo, nombre, tipo,
                caja ? null : bancoNombre, titular, caja ? null : numeroCuenta,
                caja ? null : clabe, moneda, saldoInicial, fechaSaldoInicial, activo, version);
    }

    public static CuentaFinancieraForm desde(CuentaFinancieraResponse respuesta) {
        CuentaFinancieraForm form = new CuentaFinancieraForm();
        form.institucionId = respuesta.institucionId();
        form.plantelId = respuesta.plantelId();
        form.codigo = respuesta.codigo();
        form.nombre = respuesta.nombre();
        form.tipo = respuesta.tipo();
        form.bancoNombre = respuesta.bancoNombre();
        form.titular = respuesta.titular();
        form.numeroCuenta = respuesta.numeroCuenta();
        form.clabe = respuesta.clabe();
        form.moneda = respuesta.moneda();
        form.saldoInicial = respuesta.saldoInicial();
        form.fechaSaldoInicial = respuesta.fechaSaldoInicial();
        form.activo = respuesta.activo();
        form.version = respuesta.auditoria().version();
        return form;
    }
}
