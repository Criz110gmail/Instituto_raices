package escuela.finanzas.mapper;

import escuela.finanzas.dto.request.CuentaFinancieraRequest;
import escuela.finanzas.dto.response.CuentaFinancieraResponse;
import escuela.finanzas.entity.CuentaFinanciera;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

import static escuela.common.mapper.AuditoriaMapper.desde;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Component
public class CuentaFinancieraMapper {

    public CuentaFinanciera nueva(CuentaFinancieraRequest request, Institucion institucion, Plantel plantel) {
        CuentaFinanciera cuenta = new CuentaFinanciera();
        cuenta.setInstitucion(institucion);
        actualizar(cuenta, request, plantel);
        return cuenta;
    }

    public void actualizar(CuentaFinanciera cuenta, CuentaFinancieraRequest request, Plantel plantel) {
        cuenta.setPlantel(plantel);
        cuenta.setCodigo(codigo(request.codigo()));
        cuenta.setNombre(limpiar(request.nombre()));
        cuenta.setTipo(request.tipo());
        cuenta.setBancoNombre(limpiar(request.bancoNombre()));
        cuenta.setTitular(limpiar(request.titular()));
        cuenta.setNumeroCuenta(limpiar(request.numeroCuenta()));
        cuenta.setClabe(limpiar(request.clabe()));
        cuenta.setMoneda(codigo(request.moneda()));
        cuenta.setSaldoInicial(request.saldoInicial().setScale(2, RoundingMode.UNNECESSARY));
        cuenta.setFechaSaldoInicial(request.fechaSaldoInicial());
        cuenta.setActivo(request.activo());
    }

    public CuentaFinancieraResponse respuesta(CuentaFinanciera cuenta) {
        Plantel plantel = cuenta.getPlantel();
        return new CuentaFinancieraResponse(cuenta.getId(), cuenta.getInstitucion().getId(),
                cuenta.getInstitucion().getNombre(), plantel == null ? null : plantel.getId(),
                plantel == null ? null : plantel.getNombre(), cuenta.getCodigo(), cuenta.getNombre(),
                cuenta.getTipo(), cuenta.getBancoNombre(), cuenta.getTitular(), cuenta.getNumeroCuenta(),
                cuenta.getClabe(), cuenta.getMoneda(), cuenta.getSaldoInicial(),
                cuenta.getFechaSaldoInicial(), cuenta.isActivo(), desde(cuenta));
    }
}
