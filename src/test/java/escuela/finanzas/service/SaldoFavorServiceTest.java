package escuela.finanzas.service;

import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.CargoRepository;
import escuela.finanzas.dto.request.*;
import escuela.finanzas.entity.*;
import escuela.finanzas.mapper.PagoMapper;
import escuela.finanzas.repository.*;
import escuela.institucion.entity.*;
import escuela.inscripcion.entity.Inscripcion;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.*;
import escuela.tutor.entity.Tutor;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class SaldoFavorServiceTest {
    final PagoRepository pagos=mock(PagoRepository.class);
    final AplicacionPagoRepository aplicaciones=mock(AplicacionPagoRepository.class);
    final CargoRepository cargos=mock(CargoRepository.class);
    final AlumnoTutorRepository vinculos=mock(AlumnoTutorRepository.class);
    final UsuarioRepository usuarios=mock(UsuarioRepository.class);
    final AlcanceDatosService alcance=mock(AlcanceDatosService.class);
    final RegistroAuditoriaService auditoria=mock(RegistroAuditoriaService.class);
    final PagoMapper mapper=new PagoMapper();
    final SaldoFavorService service=new SaldoFavorService(pagos,aplicaciones,cargos,vinculos,usuarios,mapper,alcance,auditoria);
    Pago pago; Plantel plantel; Institucion institucion; Usuario actor; Cargo a,b;
    @BeforeEach void preparar() {
        institucion=new Institucion();institucion.setId(1L);institucion.setZonaHoraria("America/Mexico_City");
        plantel=new Plantel();plantel.setId(2L);plantel.setInstitucion(institucion);
        Tutor tutor=new Tutor();tutor.setId(3L);tutor.setInstitucion(institucion);tutor.setActivo(true);
        actor=new Usuario();actor.setId(9L);actor.setInstitucion(institucion);actor.setUsername("administrador");
        pago=new Pago();pago.setId(50L);pago.setInstitucion(institucion);pago.setPlantelRegistro(plantel);
        pago.setTutor(tutor);pago.setMonto(new BigDecimal("100.00"));pago.setMoneda("MXN");
        pago.setEstado(EstadoPago.VALIDADO);pago.setFolio("PAG-TEST");pago.setVersion(0L);
        when(pagos.findByIdForUpdate(50L)).thenReturn(Optional.of(pago));
        when(usuarios.findById(9L)).thenReturn(Optional.of(actor));
        when(vinculos.tieneResponsabilidadFinancieraVigente(anyLong(),eq(3L),any())).thenReturn(true);
        when(aplicaciones.saveAndFlush(any())).thenAnswer(i->{AplicacionPago app=i.getArgument(0);app.setId((long)pago.getAplicaciones().size()+1);return app;});
        a=cargo(10L);b=cargo(11L); autenticar(false);
    }
    @AfterEach void limpiar(){SecurityContextHolder.clearContext();}
    void autenticar(boolean recuperacion) {
        var p=new UsuarioPrincipal(9L,1L,Set.of(2L),true,recuperacion,"admin","x",List.of(new SimpleGrantedAuthority("PAGO_VALIDAR"),new SimpleGrantedAuthority("PAGO_CANCELAR")));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,null,p.getAuthorities()));
    }
    Cargo cargo(Long id) {
        Alumno alumno=new Alumno();alumno.setId(id+100);alumno.setInstitucion(institucion);alumno.setActivo(true);alumno.setNombres("Hijo "+id);
        Inscripcion i=new Inscripcion();i.setAlumno(alumno);i.setPlantel(plantel);
        ConceptoCobro concepto=new ConceptoCobro();concepto.setNombre("Cuota");
        Cargo c=new Cargo();c.setId(id);c.setInscripcion(i);c.setConceptoCobro(concepto);c.setDescripcion("Cuota "+id);
        c.setMoneda("MXN");c.setImporteOriginal(new BigDecimal("100.00"));c.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);
        when(cargos.findByIdForUpdate(id)).thenReturn(Optional.of(c));return c;
    }
    AplicarSaldoFavorRequest request(String monto) {return new AplicarSaldoFavorRequest(0L,UUID.randomUUID().toString(),"Aplicar a mensualidad",List.of(new SolicitudAplicacionPagoRequest(10L,new BigDecimal(monto))));}
    @Test void distribuyeEntreHermanosSinCrearDineroYRevierteConAuditoria() {
        var r=new AplicarSaldoFavorRequest(0L,UUID.randomUUID().toString(),"Entre hermanos",List.of(new SolicitudAplicacionPagoRequest(11L,new BigDecimal("40")),new SolicitudAplicacionPagoRequest(10L,new BigDecimal("60"))));
        service.aplicar(50L,r);
        assertThat(mapper.respuesta(pago).montoDisponible()).isEqualByComparingTo("0");
        assertThat(escuela.cobranza.support.CalculoCargo.saldo(a)).isEqualByComparingTo("40");
        assertThat(escuela.cobranza.support.CalculoCargo.saldo(b)).isEqualByComparingTo("60");
        assertThat(pago.getMonto()).isEqualByComparingTo("100");assertThat(pago.getMovimiento()).isNull();
        var app=pago.getAplicaciones().get(0);assertThat(app.isSaldoFavor()).isTrue();assertThat(app.getAutorizadoPor()).isSameAs(actor);
        when(aplicaciones.findById(app.getId())).thenReturn(Optional.of(app));
        service.revertir(50L,app.getId(),0L,"Se eligió otro alumno");
        assertThat(mapper.respuesta(pago).montoDisponible()).isEqualByComparingTo("60");
        assertThat(escuela.cobranza.support.CalculoCargo.saldo(a)).isEqualByComparingTo("100");
        service.revertir(50L,app.getId(),0L,"Se eligió otro alumno");
        verify(aplicaciones,times(3)).saveAndFlush(any());
        verify(auditoria,times(2)).registrar(anyLong(),any(),anyString(),anyLong(),anyString(),anyMap());
    }
    @Test void reintentoIdenticoNoDuplicaElAbono() {
        var r=request("100");service.aplicar(50L,r);var app=pago.getAplicaciones().get(0);
        when(aplicaciones.findByPagoIdAndClaveSaldoFavor(50L,r.clave()+":10")).thenReturn(Optional.of(app));
        service.aplicar(50L,r);verify(aplicaciones,times(1)).saveAndFlush(any());
        assertThatThrownBy(()->service.aplicar(50L,new AplicarSaldoFavorRequest(0L,r.clave(),"Distinto",r.cargos()))).hasMessageContaining("otros datos");
    }
    @Test void rechazaDuplicadosExcesoVersionYRecuperacionAntesDeEscribir() {
        var r=request("101");assertThatThrownBy(()->service.aplicar(50L,r)).hasMessageContaining("supera el saldo");
        assertThatThrownBy(()->service.aplicar(50L,new AplicarSaldoFavorRequest(0L,r.clave(),r.motivo(),List.of(r.cargos().get(0),r.cargos().get(0))))).hasMessageContaining("una sola vez");
        pago.setVersion(1L);assertThatThrownBy(()->service.aplicar(50L,request("50"))).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);
        autenticar(true);assertThatThrownBy(()->service.aplicar(50L,request("50"))).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
        verify(aplicaciones,never()).saveAndFlush(any());
    }
    @Test void bloqueaPagosEnRevisionTutorNoResponsableOtraMonedaYPlantel() {
        when(cargos.tienePagoEnRevision(10L)).thenReturn(true);
        assertThatThrownBy(()->service.aplicar(50L,request("50"))).hasMessageContaining("en revisión");
        when(cargos.tienePagoEnRevision(10L)).thenReturn(false);
        when(vinculos.tieneResponsabilidadFinancieraVigente(anyLong(),eq(3L),any())).thenReturn(false);
        assertThatThrownBy(()->service.aplicar(50L,request("50"))).hasMessageContaining("responsable financiero");
        a.setMoneda("USD");assertThatThrownBy(()->service.aplicar(50L,request("50"))).hasMessageContaining("plantel y moneda");
        a.setMoneda("MXN");Plantel otro=new Plantel();otro.setId(99L);a.getInscripcion().setPlantel(otro);
        assertThatThrownBy(()->service.aplicar(50L,request("50"))).hasMessageContaining("plantel y moneda");
        verify(aplicaciones,never()).saveAndFlush(any());
    }
    @Test void noRevierteAbonosIncluidosEnConveniosOPagoCancelado() {
        service.aplicar(50L,request("50"));var app=pago.getAplicaciones().get(0);
        when(aplicaciones.findById(app.getId())).thenReturn(Optional.of(app));
        a.setEstadoRegistro(EstadoRegistroCargo.CANCELADO);
        assertThatThrownBy(()->service.revertir(50L,app.getId(),0L,"Error")).hasMessageContaining("convenio");
        pago.setEstado(EstadoPago.CANCELADO);
        assertThatThrownBy(()->service.revertir(50L,app.getId(),0L,"Error")).hasMessageContaining("validado");
        verify(aplicaciones,times(1)).saveAndFlush(any());
    }
}
