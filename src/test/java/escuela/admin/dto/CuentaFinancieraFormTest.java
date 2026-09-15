package escuela.admin.dto;

import escuela.finanzas.entity.TipoCuentaFinanciera;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CuentaFinancieraFormTest {

    @Test
    void descartaDatosBancariosCuandoElTipoEsCaja() {
        CuentaFinancieraForm form = new CuentaFinancieraForm();
        form.setTipo(TipoCuentaFinanciera.CAJA);
        form.setBancoNombre("valor visual anterior");
        form.setNumeroCuenta("1234");
        form.setClabe("012345678901234567");

        var request = form.request();

        assertThat(request.bancoNombre()).isNull();
        assertThat(request.numeroCuenta()).isNull();
        assertThat(request.clabe()).isNull();
    }
}
