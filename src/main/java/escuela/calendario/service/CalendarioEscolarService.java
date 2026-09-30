package escuela.calendario.service;

import escuela.academico.entity.*;
import escuela.academico.repository.*;
import escuela.admin.dto.ModuloCatalogo;
import escuela.calendario.dto.*;
import escuela.calendario.entity.*;
import escuela.calendario.repository.CalendarioEscolarRepository;
import escuela.common.exception.*;
import escuela.institucion.entity.*;
import escuela.institucion.repository.*;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import static escuela.common.mapper.NormalizacionTexto.limpiar;

@Service @RequiredArgsConstructor
public class CalendarioEscolarService {
    private final CalendarioEscolarRepository repository;
    private final InstitucionRepository instituciones;private final CicloEscolarRepository ciclos;
    private final PlantelRepository planteles;private final NivelEducativoRepository niveles;
    private final PlantelNivelRepository ofertas;private final AlcanceDatosService alcance;

    @Transactional(readOnly=true)
    public Page<CalendarioEscolarFila> listar(FiltroCalendarioEscolar entrada){FiltroCalendarioEscolar f=entrada.normalizado();validarFiltro(f);return repository.findAll(especificacion(f),PageRequest.of(f.pagina(),f.tamanio(),Sort.by(Sort.Order.asc("fechaInicio"),Sort.Order.asc("id")))).map(this::fila);}
    @Transactional(readOnly=true) public Page<CalendarioEscolarFila> bloque(FiltroCalendarioEscolar f){return listar(f);}
    @Transactional public Long crear(CalendarioEscolarForm f){CalendarioEscolarDetalle c=new CalendarioEscolarDetalle();copiar(c,f,0L);return repository.saveAndFlush(c).getId();}
    @Transactional public void actualizar(Long id,CalendarioEscolarForm f){CalendarioEscolarDetalle c=obtener(id);validarAlcance(c);if(f.getVersion()==null||!f.getVersion().equals(c.getVersion()))throw new ConflictoVersionException("La fecha del calendario",id);copiar(c,f,id);repository.saveAndFlush(c);}
    @Transactional(readOnly=true) public CalendarioEscolarForm formulario(Long id){CalendarioEscolarDetalle c=obtener(id);validarAlcance(c);CalendarioEscolarForm f=new CalendarioEscolarForm();f.setInstitucionId(c.getInstitucion().getId());f.setCicloEscolarId(c.getCicloEscolar().getId());f.setPlantelId(c.getPlantel()==null?null:c.getPlantel().getId());f.setNivelEducativoId(c.getNivelEducativo()==null?null:c.getNivelEducativo().getId());f.setTipo(c.getTipo());f.setTitulo(c.getTitulo());f.setFechaInicio(c.getFechaInicio());f.setFechaFin(c.getFechaFin());f.setHoraInicio(c.getHoraInicio());f.setHoraFin(c.getHoraFin());f.setSuspendeClases(c.isSuspendeClases());f.setDescripcion(c.getDescripcion());f.setActivo(c.isActivo());f.setVersion(c.getVersion());return f;}

    private void copiar(CalendarioEscolarDetalle c,CalendarioEscolarForm f,Long id){
        var institucion=instituciones.findById(f.getInstitucionId()).orElseThrow(()->new RecursoNoEncontradoException("la institución",f.getInstitucionId()));
        var ciclo=ciclos.findById(f.getCicloEscolarId()).orElseThrow(()->new RecursoNoEncontradoException("el ciclo escolar",f.getCicloEscolarId()));
        if(!ciclo.getInstitucion().getId().equals(institucion.getId()))throw new ReglaNegocioException("El ciclo escolar no pertenece a la institución seleccionada");
        if(ciclo.getEstado()==EstadoAcademico.CERRADO)throw new ReglaNegocioException("No se puede modificar el calendario de un ciclo escolar cerrado");
        Plantel plantel=f.getPlantelId()==null?null:planteles.findById(f.getPlantelId()).orElseThrow(()->new RecursoNoEncontradoException("el plantel",f.getPlantelId()));
        NivelEducativo nivel=f.getNivelEducativoId()==null?null:niveles.findById(f.getNivelEducativoId()).orElseThrow(()->new RecursoNoEncontradoException("el nivel educativo",f.getNivelEducativoId()));
        if(plantel!=null){alcance.validarPlantel(plantel.getId());if(!plantel.getInstitucion().getId().equals(institucion.getId())||!plantel.isActivo())throw new ReglaNegocioException("El plantel debe estar activo y pertenecer a la institución");}
        else alcance.validarAdministracionInstitucional(institucion.getId());
        if(nivel!=null&&(!nivel.getInstitucion().getId().equals(institucion.getId())||!nivel.isActivo()))throw new ReglaNegocioException("El nivel debe estar activo y pertenecer a la institución");
        if(plantel!=null&&nivel!=null&&!ofertas.existsByPlantelIdAndNivelEducativoIdAndActivoTrue(plantel.getId(),nivel.getId()))throw new ReglaNegocioException("El plantel no tiene activo el nivel educativo seleccionado");
        if(f.getFechaInicio()==null||f.getFechaFin()==null||f.getFechaFin().isBefore(f.getFechaInicio()))throw new ReglaNegocioException("La fecha final debe ser igual o posterior a la fecha inicial");
        if(f.getFechaInicio().isBefore(ciclo.getFechaInicio())||f.getFechaFin().isAfter(ciclo.getFechaFin()))throw new ReglaNegocioException("Las fechas deben quedar dentro del ciclo escolar ("+ciclo.getFechaInicio()+" a "+ciclo.getFechaFin()+")");
        boolean unaHora=f.getHoraInicio()!=null||f.getHoraFin()!=null;if(unaHora&&(f.getHoraInicio()==null||f.getHoraFin()==null||!f.getHoraFin().isAfter(f.getHoraInicio())))throw new ReglaNegocioException("Captura ambas horas y asegúrate de que la hora final sea posterior");
        if(f.getTipo()==TipoFechaCalendario.VARIACION_HORARIO&&!unaHora)throw new ReglaNegocioException("Una variación de horario requiere hora inicial y final");
        boolean suspension=f.getTipo()==TipoFechaCalendario.DIA_INHABIL||f.getTipo()==TipoFechaCalendario.VACACIONES;
        if(f.isActivo()&&suspension&&repository.existeSuspensionSuperpuesta(id,ciclo.getId(),plantel==null?null:plantel.getId(),nivel==null?null:nivel.getId(),f.getFechaInicio(),f.getFechaFin()))throw new ReglaNegocioException("Ya existe un día inhábil o periodo vacacional activo que se cruza con esas fechas y el mismo alcance");
        c.setInstitucion(institucion);c.setCicloEscolar(ciclo);c.setPlantel(plantel);c.setNivelEducativo(nivel);c.setTipo(f.getTipo());c.setTitulo(limpiar(f.getTitulo()));c.setFechaInicio(f.getFechaInicio());c.setFechaFin(f.getFechaFin());c.setHoraInicio(f.getHoraInicio());c.setHoraFin(f.getHoraFin());c.setSuspendeClases(suspension||f.isSuspendeClases());c.setDescripcion(limpiar(f.getDescripcion()));c.setActivo(f.isActivo());
    }

    private Specification<CalendarioEscolarDetalle> especificacion(FiltroCalendarioEscolar f){Specification<CalendarioEscolarDetalle>s=alcance.especificacion(ModuloCatalogo.CALENDARIO_ESCOLAR);if(f.institucionId()!=null)s=s.and((r,q,cb)->cb.equal(r.get("institucion").get("id"),f.institucionId()));if(f.cicloEscolarId()!=null)s=s.and((r,q,cb)->cb.equal(r.get("cicloEscolar").get("id"),f.cicloEscolarId()));if(f.plantelId()!=null)s=s.and((r,q,cb)->cb.equal(r.get("plantel").get("id"),f.plantelId()));if(f.nivelEducativoId()!=null)s=s.and((r,q,cb)->cb.equal(r.get("nivelEducativo").get("id"),f.nivelEducativoId()));if(f.tipo()!=null)s=s.and((r,q,cb)->cb.equal(r.get("tipo"),f.tipo()));if(f.activo()!=null)s=s.and((r,q,cb)->cb.equal(r.get("activo"),f.activo()));if(f.desde()!=null)s=s.and((r,q,cb)->cb.greaterThanOrEqualTo(r.get("fechaFin"),f.desde()));if(f.hasta()!=null)s=s.and((r,q,cb)->cb.lessThanOrEqualTo(r.get("fechaInicio"),f.hasta()));if(!f.texto().isBlank()){String p="%"+f.texto().toLowerCase(Locale.ROOT)+"%";s=s.and((r,q,cb)->cb.or(cb.like(cb.lower(r.get("titulo")),p),cb.like(cb.lower(cb.coalesce(r.get("descripcion"),"")),p)));}return s;}
    private void validarFiltro(FiltroCalendarioEscolar f){if(f.institucionId()!=null)alcance.validarInstitucion(f.institucionId());if(f.plantelId()!=null)alcance.validarPlantel(f.plantelId());if(f.desde()!=null&&f.hasta()!=null&&f.hasta().isBefore(f.desde()))throw new ReglaNegocioException("La fecha final del filtro debe ser igual o posterior a la inicial");}
    private CalendarioEscolarDetalle obtener(Long id){return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("la fecha del calendario",id));}
    private void validarAlcance(CalendarioEscolarDetalle c){if(c.getPlantel()!=null)alcance.validarPlantel(c.getPlantel().getId());else alcance.validarAdministracionInstitucional(c.getInstitucion().getId());}
    private CalendarioEscolarFila fila(CalendarioEscolarDetalle c){String a=c.getPlantel()==null&&c.getNivelEducativo()==null?"Toda la institución":c.getPlantel()!=null&&c.getNivelEducativo()!=null?c.getPlantel().getNombre()+" · "+c.getNivelEducativo().getNombre():c.getPlantel()!=null?c.getPlantel().getNombre():"Todos los planteles · "+c.getNivelEducativo().getNombre();return new CalendarioEscolarFila(c.getId(),c.getInstitucion().getNombre(),c.getCicloEscolar().getNombre(),a,c.getTipo(),c.getTitulo(),c.getFechaInicio(),c.getFechaFin(),c.getHoraInicio(),c.getHoraFin(),c.isSuspendeClases(),c.getDescripcion(),c.isActivo(),c.getVersion());}
}
