package escuela.docente.support;

import escuela.docente.entity.*;
import escuela.docente.repository.*;
import escuela.docente.service.*;
import escuela.academico.entity.*;
import escuela.academico.repository.GrupoRepository;
import escuela.institucion.entity.*;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.*;
import escuela.auditoria.service.RegistroAuditoriaService;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class PlaneacionPublicacionTest {
 private final PlaneacionSemanalRepository planes=mock(PlaneacionSemanalRepository.class);
 private final PlaneacionHistorialRepository historial=mock(PlaneacionHistorialRepository.class);
 private final PlaneacionVersionPublicadaRepository versiones=mock(PlaneacionVersionPublicadaRepository.class);
 private final UsuarioRepository usuarios=mock(UsuarioRepository.class);
 private final PlaneacionService service=new PlaneacionService(planes,historial,versiones,mock(MaestroRepository.class),mock(AsignacionMaestroRepository.class),mock(GrupoRepository.class),usuarios,mock(AlcanceDatosService.class),mock(RegistroAuditoriaService.class));
 private final Map<Integer,PlaneacionVersionPublicada> guardadas=new HashMap<>();
 private PlaneacionSemanal preparar(){
  var principal=new UsuarioPrincipal(9L,1L,Set.of(),false,false,"admin.qa","",List.of());
  SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(principal,"",List.of()));
  when(usuarios.getReferenceById(9L)).thenReturn(new Usuario());
  var p=new PlaneacionSemanal();p.setId(22L);p.setVersion(2L);p.setEstado(EstadoPlaneacion.EN_REVISION);
  var institucion=new Institucion();institucion.setId(1L);institucion.setNombre("Escuela ficticia");institucion.setZonaHoraria("America/Mexico_City");p.setInstitucion(institucion);
  var maestro=mock(Maestro.class);when(maestro.getNombres()).thenReturn("Docente");when(maestro.getPrimerApellido()).thenReturn("Ficticio");when(maestro.getNumeroEmpleado()).thenReturn("DOC-QA");p.setMaestro(maestro);
  var grupo=mock(Grupo.class);var plantel=mock(Plantel.class);var grado=mock(Grado.class);when(grupo.getId()).thenReturn(2L);when(grupo.getNombre()).thenReturn("A");when(grupo.getPlantel()).thenReturn(plantel);when(plantel.getNombre()).thenReturn("Plantel QA");when(grupo.getGrado()).thenReturn(grado);when(grado.getNombre()).thenReturn("Primero");p.setGrupo(grupo);
  var ciclo=mock(CicloEscolar.class);when(ciclo.getNombre()).thenReturn("2026-2027");p.setCicloEscolar(ciclo);
  p.setFechaInicio(LocalDate.of(2026,10,19));p.setFechaFin(LocalDate.of(2026,10,23));p.setProposito("Resolver sumas.");
  var materia=mock(Materia.class);when(materia.getId()).thenReturn(3L);when(materia.getCodigo()).thenReturn("MAT");when(materia.getNombre()).thenReturn("Matemáticas");
  var pm=new PlaneacionMateria();pm.setMateria(materia);p.getMaterias().add(pm);
  var a=new PlaneacionActividad();a.setMateria(materia);a.setFecha(LocalDate.of(2026,10,19));a.setTitulo("Sumar fichas");a.setInicio("Repasar");a.setDesarrollo("Sumar");a.setCierre("Compartir");a.setDuracionMinutos(30);p.getActividades().add(a);
  when(planes.findByIdForUpdate(22L)).thenReturn(Optional.of(p));when(planes.findDetalleById(22L)).thenReturn(Optional.of(p));
  when(versiones.save(any())).thenAnswer(inv->{PlaneacionVersionPublicada v=inv.getArgument(0);guardadas.put(v.getNumeroRevision(),v);return v;});
  when(versiones.findByPlaneacionIdAndNumeroRevision(eq(22L),anyInt())).thenAnswer(inv->Optional.ofNullable(guardadas.get(inv.getArgument(1))));
  return p;
 }
 @Test void publicaYLeeVersionConFechasDeSemanaActividadEInstanteSinModulosJackson2()throws Exception{
  try{var p=preparar();service.publicar(22L,2L);
   assertThat(p.getEstado()).isEqualTo(EstadoPlaneacion.PUBLICADA);assertThat(p.getNumeroRevision()).isEqualTo(1);
   assertThat(guardadas.get(1).getContenidoJson()).contains("\"fechaInicio\":\"2026-10-19\"","\"fecha\":\"2026-10-19\"");
   var leida=service.version(22L,1,false,null);assertThat(leida.fechaInicio()).isEqualTo(p.getFechaInicio());assertThat(leida.fechaFin()).isEqualTo(p.getFechaFin());assertThat(leida.publicadaEn()).isEqualTo(p.getPublicadaEn());assertThat(leida.actividades().getFirst().fecha()).isEqualTo(p.getFechaInicio());
   var out=new java.io.ByteArrayOutputStream();new PdfPlaneacionService().exportar(leida,out);assertThat(new String(out.toByteArray(),0,5,java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
   verify(historial).save(argThat(h->h.getEstadoNuevo()==EstadoPlaneacion.PUBLICADA));
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void reabrirYVolverAPublicarConservaVersionAnterior(){
  try{var p=preparar();service.publicar(22L,2L);String original=guardadas.get(1).getContenidoJson();
   service.reabrir(22L,2L,"Agregar evaluación");p.setProposito("Resolver sumas y explicar resultados.");p.setEstado(EstadoPlaneacion.EN_REVISION);service.publicar(22L,2L);
   assertThat(guardadas).hasSize(2);assertThat(guardadas.get(1).getContenidoJson()).isEqualTo(original);assertThat(service.version(22L,1,false,null).proposito()).isEqualTo("Resolver sumas.");assertThat(service.version(22L,2,false,null).proposito()).contains("explicar resultados");
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void noDuplicaVersionSiEstadoOControlDeVersionSonInvalidos(){
  try{var p=preparar();assertThatThrownBy(()->service.publicar(22L,1L)).isInstanceOf(RuntimeException.class);verifyNoInteractions(versiones);
   service.publicar(22L,2L);assertThatThrownBy(()->service.publicar(22L,2L)).hasMessageContaining("en revisión");assertThat(guardadas).hasSize(1);
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void puedeLeerFechasNumericasDeVersionesHistoricasLegadas(){
  try{var p=preparar();service.publicar(22L,2L);var snapshot=guardadas.get(1);var instante=p.getPublicadaEn();
   String legacy=snapshot.getContenidoJson().replace("\"fechaInicio\":\"2026-10-19\"","\"fechaInicio\":[2026,10,19]")
    .replace("\"fechaFin\":\"2026-10-23\"","\"fechaFin\":[2026,10,23]")
    .replace("\"fecha\":\"2026-10-19\"","\"fecha\":[2026,10,19]")
    .replace("\""+instante+"\"",instante.getEpochSecond()+"."+String.format(java.util.Locale.ROOT,"%09d",instante.getNano()));
   snapshot.setContenidoJson(legacy);var leida=service.version(22L,1,false,null);
   assertThat(leida.fechaInicio()).isEqualTo(p.getFechaInicio());assertThat(leida.fechaFin()).isEqualTo(p.getFechaFin());assertThat(leida.actividades().getFirst().fecha()).isEqualTo(p.getFechaInicio());assertThat(leida.publicadaEn()).isEqualTo(instante);
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void listaVersionesConFechaLocalInstitucionalSinModificarInstanteGuardado(){
  try{preparar();var v=new PlaneacionVersionPublicada();v.setNumeroRevision(2);var instante=java.time.Instant.parse("2026-10-20T02:30:00Z");v.setPublicadaEn(instante);when(versiones.findAllByPlaneacionIdOrderByNumeroRevisionDesc(22L)).thenReturn(List.of(v));
   var filas=service.versiones(22L);assertThat(filas).hasSize(1);assertThat(filas.getFirst().numeroRevision()).isEqualTo(2);assertThat(filas.getFirst().publicadaEn()).isEqualTo(java.time.LocalDateTime.of(2026,10,19,20,30));assertThat(v.getPublicadaEn()).isEqualTo(instante);
  }finally{SecurityContextHolder.clearContext();}
 }
}
