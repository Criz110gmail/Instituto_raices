package escuela.personal;

import escuela.personal.dto.*;
import escuela.personal.service.ProgramacionNominaService;
import jakarta.validation.Validation;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ProgramacionNominaTest {
 private FiltroProgramacionNomina filtro(String estado){return new FiltroProgramacionNomina(" prueba ",estado,"MENSUAL",1L,"selección",2L,"periodo",LocalDate.of(2027,1,1),LocalDate.of(2027,1,31),-1,999);}
 @Test void estadosDistintosYLimites(){var c=filtro("PROGRAMADO").normalizado(false);assertThat(c.estado()).isEqualTo("TODOS");assertThat(c.filtroPeriodoId()).isNull();assertThat(c.desde()).isNull();assertThat(c.q()).isEqualTo("prueba");assertThat(c.pagina()).isZero();assertThat(c.tamanio()).isEqualTo(25);assertThat(filtro("PROGRAMADO").normalizado(true).estado()).isEqualTo("PROGRAMADO");assertThat(filtro("ACTIVO").normalizado(true).estado()).isEqualTo("TODOS");}
 @Test void fechasEIdentificadoresConservadosAlVolver(){assertThat(filtro("CANCELADO").url("/admin/periodos-nomina/12")).contains("filtroCalendarioId=1","filtroPeriodoId=2","desde=2027-01-01","hasta=2027-01-31","estado=CANCELADO");assertThat(filtro("ACTIVO").url("/admin/calendarios-nomina")).doesNotContain("desde=","filtroPeriodoId=");}
 @Test void textoSinSeleccionYFechasInvertidasNoSeIgnoran(){assertThatThrownBy(()->new FiltroProgramacionNomina("","","",null,"Pendiente",null,"",null,null,0,10).validar()).hasMessageContaining("Selecciona el calendario");assertThatThrownBy(()->new FiltroProgramacionNomina("","","",null,"",null,"Pendiente",null,null,0,10).validar()).hasMessageContaining("Selecciona el periodo");assertThatThrownBy(()->new FiltroProgramacionNomina("","","",null,"",null,"",LocalDate.of(2027,2,1),LocalDate.of(2027,1,1),0,10).validar()).hasMessageContaining("rango");}
 @Test void capturaObligatoriaSinDatosFinancieros(){try(var factory=Validation.buildDefaultValidatorFactory()){var v=factory.getValidator();assertThat(v.validate(new CalendarioNominaForm())).isNotEmpty();assertThat(v.validate(new PeriodoNominaForm())).isNotEmpty();}assertThat(java.util.Arrays.stream(PeriodoNominaForm.class.getDeclaredFields()).map(java.lang.reflect.Field::getName)).doesNotContain("importe","empleadoId","sueldo","cuentaId");}
 @Test void duracionInclusivaYEstadoNoEsPago(){var p=new ProgramacionNominaService.Periodo(1L,1L,"ENE","Enero","CAL","Mensual","MENSUAL",0L,LocalDate.of(2027,1,1),LocalDate.of(2027,1,31),LocalDate.of(2027,1,28),LocalDate.of(2027,1,31),"PROGRAMADO","Original","Original",0L,"Próximo");assertThat(p.dias()).isEqualTo(31);assertThat(p.estadoTexto()).isEqualTo("Programado");assertThat(p.frecuenciaTexto()).isEqualTo("Mensual");}
 @Test void fechaDeIncidenciasEsOpcionalPeroPagoSigueObligatorio(){try(var factory=Validation.buildDefaultValidatorFactory()){var v=factory.getValidator();var f=new PeriodoNominaForm();f.setCalendarioId(1L);f.setCalendarioTexto("CAL · Mensual");f.setCalendarioVersion(0L);f.setCodigo("ENE");f.setNombre("Enero");f.setInicio(LocalDate.of(2027,1,1));f.setFin(LocalDate.of(2027,1,31));f.setPago(LocalDate.of(2027,1,31));f.setMotivo("Programación de prueba");assertThat(v.validate(f)).isEmpty();f.setPago(null);assertThat(v.validate(f)).extracting(x->x.getPropertyPath().toString()).contains("pago").doesNotContain("corte");}}
}
