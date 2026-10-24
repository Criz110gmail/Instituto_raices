package escuela.personal;
import escuela.personal.dto.*;import escuela.personal.entity.*;import escuela.personal.repository.*;import escuela.personal.service.*;
import escuela.docente.entity.Maestro;import escuela.docente.repository.MaestroRepository;import escuela.institucion.entity.*;import escuela.institucion.repository.*;
import escuela.seguridad.entity.*;import escuela.seguridad.repository.UsuarioRepository;import escuela.seguridad.service.*;
import org.junit.jupiter.api.*;import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;import org.springframework.security.core.authority.SimpleGrantedAuthority;import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.security.access.AccessDeniedException;import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;import static org.mockito.Mockito.*;import static org.mockito.ArgumentMatchers.*;
class EmpleadoServiceTest {
 final EmpleadoRepository empleados=mock(EmpleadoRepository.class);final EmpleadoLaboralRepository laborales=mock(EmpleadoLaboralRepository.class);final EmpleadoPlantelRepository ads=mock(EmpleadoPlantelRepository.class);final MaestroRepository maestros=mock(MaestroRepository.class);final InstitucionRepository instituciones=mock(InstitucionRepository.class);final PlantelRepository planteles=mock(PlantelRepository.class);final UsuarioRepository usuarios=mock(UsuarioRepository.class);
 final JdbcTemplate jdbc=mock(JdbcTemplate.class);
 final EmpleadoService s=new EmpleadoService(empleados,laborales,ads,maestros,instituciones,planteles,usuarios,mock(AlcanceDatosService.class),jdbc);
 final Institucion i=new Institucion();final Empleado e=new Empleado();final Usuario u=new Usuario();
 @BeforeEach void datos(){i.setId(1L);i.setActivo(true);i.setZonaHoraria("America/Mexico_City");e.setId(2L);e.setInstitucion(i);e.setCodigo("EMP-2");e.setNombres("Ana");e.setPrimerApellido("Prueba");e.setVersion(4L);u.setId(3L);u.setInstitucion(i);u.setTipoCuenta(TipoCuentaUsuario.ADMINISTRATIVO);u.setEstado(EstadoUsuario.ACTIVO);when(usuarios.findById(3L)).thenReturn(Optional.of(u));when(empleados.findById(2L)).thenReturn(Optional.of(e));when(empleados.bloquear(2L)).thenReturn(Optional.of(e));when(maestros.findByEmpleadoId(anyLong())).thenReturn(Optional.empty());sesion(false,true,"EMPLEADO_LEER","EMPLEADO_ADMINISTRAR");}
 void sesion(boolean bootstrap,boolean institucional,String...permisos){var authorities=Arrays.stream(permisos).map(SimpleGrantedAuthority::new).toList();var p=new UsuarioPrincipal(3L,1L,Set.of(),institucional,bootstrap,"qa","",authorities);SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,"",authorities));}
 @AfterEach void limpiar(){SecurityContextHolder.clearContext();}
 @Test void otraInstitucionRecuperacionYAlcanceLimitadoNoPuedenLeer(){var otra=new Institucion();otra.setId(9L);e.setInstitucion(otra);assertThatThrownBy(()->s.obtener(2L)).isInstanceOf(AccessDeniedException.class);e.setInstitucion(i);sesion(true,true,"EMPLEADO_LEER");assertThatThrownBy(()->s.obtener(2L)).isInstanceOf(AccessDeniedException.class);sesion(false,false,"EMPLEADO_LEER");assertThatThrownBy(()->s.obtener(2L)).isInstanceOf(AccessDeniedException.class);}
 @Test void lecturaPersonalNoFiltraDatosLaborales(){assertThat(s.obtener(2L).nombre()).isEqualTo("Ana Prueba");assertThatThrownBy(()->s.laboral(2L)).isInstanceOf(AccessDeniedException.class);verifyNoInteractions(laborales);}
 @Test void resumenEnListadoSeConsultaEnBloqueSoloParaPaginaEInstitucionAutorizadas(){
  when(empleados.findAll(any(org.springframework.data.jpa.domain.Specification.class),any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(e)));
  var filtro=new FiltroEmpleado(1L,"","TODOS",0,10);
  assertThat(s.listar(filtro).getContent().getFirst().resumenLaboral()).isNull();verifyNoInteractions(laborales);
  sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER");var fila=mock(EmpleadoLaboralRepository.ResumenFila.class);when(fila.getEmpleadoId()).thenReturn(2L);when(fila.getSituacion()).thenReturn("ACTIVO");when(fila.getIngreso()).thenReturn(LocalDate.of(2026,10,1));when(laborales.resumirPagina(1L,List.of(2L))).thenReturn(List.of(fila));
  assertThat(s.listar(filtro).getContent().getFirst().resumenLaboral().ingresoTexto()).isEqualTo("01/10/2026");verify(laborales).resumirPagina(1L,List.of(2L));verify(laborales,never()).findByEmpleadoId(anyLong());
  assertThatThrownBy(()->s.listar(new FiltroEmpleado(9L,"","TODOS",0,10))).isInstanceOf(AccessDeniedException.class);
 }
 @Test void fichaConsultaResumenPersistidoSoloConPermiso(){
  sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER");var laboral=new EmpleadoLaboral();laboral.setSituacion("BAJA");laboral.setFechaIngreso(LocalDate.of(2026,10,1));laboral.setFechaBaja(LocalDate.of(2026,10,20));when(laborales.findByEmpleadoId(2L)).thenReturn(Optional.of(laboral));
  var resumen=s.obtener(2L).resumenLaboral();assertThat(resumen.etiqueta()).isEqualTo("Baja laboral");assertThat(resumen.bajaTexto()).isEqualTo("20/10/2026");
 }
 @Test void sueldosExigenPermisoPropioSinHerenciaAcademicaOLaboral(){
  sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR","MAESTRO_ADMINISTRAR");assertThatThrownBy(()->s.autorizarSalarios(false)).isInstanceOf(AccessDeniedException.class);
  sesion(false,true,"EMPLEADO_LEER","CONDICION_SALARIAL_ADMINISTRAR");assertThatThrownBy(()->s.autorizarSalarios(true)).isInstanceOf(AccessDeniedException.class);
 }
 @Test void lecturaSalarialNoPermiteEscrituraYRecuperacionNoAccede(){
  sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER");assertThat(s.autorizarSalarios(false).usuarioId()).isEqualTo(3L);assertThatThrownBy(()->s.autorizarSalarios(true)).isInstanceOf(AccessDeniedException.class);
  sesion(true,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR","CONDICION_SALARIAL_ADMINISTRAR");assertThatThrownBy(()->s.autorizarSalarios(true)).isInstanceOf(AccessDeniedException.class);
 }
 @Test void cuentaInactivaOTipoDocenteNoAdministranPersonal(){u.setEstado(EstadoUsuario.INACTIVO);assertThatThrownBy(()->s.obtener(2L)).isInstanceOf(AccessDeniedException.class);u.setEstado(EstadoUsuario.ACTIVO);u.setTipoCuenta(TipoCuentaUsuario.PORTAL_MAESTRO);assertThatThrownBy(()->s.obtener(2L)).isInstanceOf(AccessDeniedException.class);}
 @Test void editarNoReactivaEmpleadoNiCuentaYRechazaVersionVieja(){e.setActivo(false);var f=new EmpleadoForm();f.setInstitucionId(1L);f.setCodigo("EMP-2");f.setNombres("Ana editada");f.setPrimerApellido("Prueba");f.setVersion(4L);s.guardar(2L,f);assertThat(e.getNombres()).isEqualTo("Ana editada");assertThat(e.isActivo()).isFalse();verify(usuarios,never()).save(any());f.setVersion(1L);assertThatThrownBy(()->s.guardar(2L,f)).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);}
 @Test void habilitarPerfilConservaIdYCuentaYNoCreaOtro(){var m=new Maestro();m.setId(7L);m.setEmpleado(e);m.setInstitucion(i);m.setNumeroEmpleado("DOC-ANTERIOR");m.setUsuario(u);m.setActivo(false);when(maestros.findByEmpleadoId(2L)).thenReturn(Optional.of(m));s.perfil(2L,4L,true,"IGNORADO");assertThat(m.getId()).isEqualTo(7L);assertThat(m.getNumeroEmpleado()).isEqualTo("DOC-ANTERIOR");assertThat(m.getUsuario()).isSameAs(u);assertThat(m.isActivo()).isTrue();verify(maestros).saveAndFlush(m);verify(usuarios,never()).save(any());}
 @Test void dependenciaDocenteImpideDeshabilitarSinTocarCuenta(){var m=new Maestro();m.setId(7L);m.setEmpleado(e);m.setActivo(true);m.setUsuario(u);when(maestros.findByEmpleadoId(2L)).thenReturn(Optional.of(m));when(maestros.tieneDependenciasActivas(eq(7L),any())).thenReturn(true);assertThatThrownBy(()->s.perfil(2L,4L,false,"")).hasMessageContaining("Finaliza");assertThat(m.isActivo()).isTrue();assertThat(u.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);}
 @Test void bajaRequiereMotivoYSoloCambiaLaboral(){sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR");var l=new EmpleadoLaboral();l.setEmpleado(e);l.setVersion(2L);when(laborales.findByEmpleadoId(2L)).thenReturn(Optional.of(l));var f=new LaboralForm();f.setVersion(2L);f.setSituacion("BAJA");f.setFechaBaja(LocalDate.of(2026,1,1));assertThatThrownBy(()->s.guardarLaboral(2L,f)).hasMessageContaining("motivo");f.setMotivoBaja("Fin de relación confirmado");s.guardarLaboral(2L,f);assertThat(l.getSituacion()).isEqualTo("BAJA");assertThat(e.isActivo()).isTrue();assertThat(u.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);verifyNoInteractions(maestros);}
 @Test void plantelAjenoNoSeAsignaNiEntregaPermisos(){sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR");var otra=new Institucion();otra.setId(9L);var p=new Plantel();p.setId(5L);p.setInstitucion(otra);p.setActivo(true);when(planteles.findById(5L)).thenReturn(Optional.of(p));var f=new PlantelEmpleadoForm();f.setPlantelId(5L);f.setFechaInicio(LocalDate.of(2026,1,1));assertThatThrownBy(()->s.agregarPlantel(2L,f)).isInstanceOf(AccessDeniedException.class);verifyNoInteractions(ads);}
 @Test void identidadCanonicaSeComparteConMaestroSinReactivar(){var m=new Maestro();m.setId(7L);m.setEmpleado(e);m.setActivo(false);m.setNombres("Nombre único");assertThat(e.getNombres()).isEqualTo("Nombre único");e.setTelefono("123");assertThat(m.getTelefono()).isEqualTo("123");assertThat(m.isActivo()).isFalse();}
 private LaboralForm laboralParaPrueba(String puesto){
  sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR");var l=new EmpleadoLaboral();l.setEmpleado(e);l.setVersion(2L);l.setPuesto("Puesto anterior");when(laborales.findByEmpleadoId(2L)).thenReturn(Optional.of(l));
  var f=new LaboralForm();f.setVersion(2L);f.setSituacion("ACTIVO");f.setFechaIngreso(LocalDate.of(2026,1,1));f.setPuesto(puesto);return f;
 }
 @Test void puestoNuevoExigeSeleccionYConservaTextoHistoricoSinConsultarSalarios(){
  var f=laboralParaPrueba("Texto libre nuevo");assertThatThrownBy(()->s.guardarLaboral(2L,f)).hasMessageContaining("sugerencias");verify(laborales,never()).saveAndFlush(any());
  f.setPuesto("Puesto anterior");s.guardarLaboral(2L,f);assertThat(laborales.findByEmpleadoId(2L).orElseThrow().getPuesto()).isEqualTo("Puesto anterior");verifyNoInteractions(jdbc);
 }
 @Test void puestoSeleccionadoValidaInstitucionActivoYEtiquetaYGuardaNombreHistorico(){
  var f=laboralParaPrueba("Auxiliar de prueba");f.setPuestoId(7L);
  when(jdbc.query(contains("SELECT nombre FROM puesto_laboral"),any(org.springframework.jdbc.core.RowMapper.class),eq(7L),eq(1L))).thenReturn(List.of("Auxiliar de prueba"));
  s.guardarLaboral(2L,f);assertThat(laborales.findByEmpleadoId(2L).orElseThrow().getPuesto()).isEqualTo("Auxiliar de prueba");
  verify(jdbc).query(contains("activo FOR SHARE"),any(org.springframework.jdbc.core.RowMapper.class),eq(7L),eq(1L));
  f.setPuesto("Etiqueta alterada");assertThatThrownBy(()->s.guardarLaboral(2L,f)).hasMessageContaining("seleccionarlo");
  when(jdbc.query(contains("SELECT nombre FROM puesto_laboral"),any(org.springframework.jdbc.core.RowMapper.class),eq(7L),eq(1L))).thenReturn(List.of());
  assertThatThrownBy(()->s.guardarLaboral(2L,f)).hasMessageContaining("activo de tu institución");verify(laborales,times(1)).saveAndFlush(any());
 }
 @Test void ayudaPuestosLimitadaSinSalariosPeroConPermisoLaboralYEmpleadoPropio(){
  assertThatThrownBy(()->s.buscarPuestosLaborales(2L,"")).isInstanceOf(AccessDeniedException.class);verifyNoInteractions(jdbc);
  sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR");assertThat(s.buscarPuestosLaborales(2L,"ab")).isEmpty();verifyNoInteractions(jdbc);
  s.buscarPuestosLaborales(2L,"");verify(jdbc).query(contains("ORDER BY nombre,id LIMIT 11"),any(org.springframework.jdbc.core.RowMapper.class),eq(1L),eq(""));
  s.buscarPuestosLaborales(2L,"AUX");verify(jdbc).query(contains("institucion_id=? AND activo"),any(org.springframework.jdbc.core.RowMapper.class),eq(1L),eq("AUX"));
  var otra=new Institucion();otra.setId(99L);e.setInstitucion(otra);assertThatThrownBy(()->s.buscarPuestosLaborales(2L,"")).isInstanceOf(AccessDeniedException.class);
 }
 @Test void resumenPrincipalConsultaActualYProximoConLimiteUnoSinDependerDePagina(){
  sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER");var actual=new EmpleadoPlantel();var pa=new Plantel();pa.setNombre("Plantel B");actual.setPlantel(pa);
  var proximo=new EmpleadoPlantel();var pb=new Plantel();pb.setNombre("Plantel A");proximo.setPlantel(pb);proximo.setFechaInicio(LocalDate.now(java.time.ZoneId.of("America/Mexico_City")).plusDays(1));
  when(ads.findAll(any(org.springframework.data.jpa.domain.Specification.class),any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(actual)),new org.springframework.data.domain.PageImpl<>(List.of(proximo)));
  var resumen=s.resumenPrincipal(2L);assertThat(resumen.actual()).isEqualTo("Plantel B");assertThat(resumen.proximo()).isEqualTo("Plantel A");assertThat(resumen.desde()).isEqualTo(resumen.hoy().plusDays(1));
  var paginas=org.mockito.ArgumentCaptor.forClass(org.springframework.data.domain.Pageable.class);verify(ads,times(2)).findAll(any(org.springframework.data.jpa.domain.Specification.class),paginas.capture());assertThat(paginas.getAllValues()).allSatisfy(p->assertThat(p.getPageSize()).isEqualTo(1));assertThat(paginas.getValue().getSort().getOrderFor("fechaInicio").isAscending()).isTrue();verify(ads,never()).save(any());
 }
 @Test void resumenVacioSinProgramacionYSinPermisoNoConsulta(){
  assertThatThrownBy(()->s.resumenPrincipal(2L)).isInstanceOf(AccessDeniedException.class);verifyNoInteractions(ads);
  sesion(false,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER");when(ads.findAll(any(org.springframework.data.jpa.domain.Specification.class),any(org.springframework.data.domain.Pageable.class))).thenReturn(org.springframework.data.domain.Page.empty());
  var resumen=s.resumenPrincipal(2L);assertThat(resumen.actual()).isNull();assertThat(resumen.proximo()).isNull();assertThat(resumen.desde()).isNull();
 }
}
