package escuela.cobranza.service.impl;

import escuela.cobranza.dto.request.BecaAlumnoRequest;
import escuela.cobranza.dto.response.BecaAlumnoResponse;
import escuela.cobranza.entity.*;
import escuela.cobranza.mapper.BecaAlumnoMapper;
import escuela.cobranza.repository.*;
import escuela.cobranza.service.BecaAlumnoService;
import escuela.common.exception.*;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.RoundingMode;
import java.util.EnumSet;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class BecaAlumnoServiceImpl implements BecaAlumnoService {
    private static final EnumSet<EstadoInscripcion> VIGENTES=EnumSet.of(EstadoInscripcion.PREINSCRITA,EstadoInscripcion.ACTIVA);
    private final BecaAlumnoRepository repository; private final InscripcionRepository inscripcionRepository;
    private final TipoBecaRepository tipoRepository; private final ConceptoCobroRepository conceptoRepository;
    private final UsuarioRepository usuarioRepository; private final BecaAlumnoMapper mapper;
    public BecaAlumnoResponse crear(BecaAlumnoRequest r){var i=inscripcionRepository.findByIdForUpdate(r.inscripcionId()).orElseThrow(()->new RecursoNoEncontradoException("la inscripción",r.inscripcionId()));
        var t=tipoRepository.findById(r.tipoBecaId()).orElseThrow(()->new RecursoNoEncontradoException("el tipo de beca",r.tipoBecaId()));
        var c=conceptoRepository.findById(r.conceptoCobroId()).orElseThrow(()->new RecursoNoEncontradoException("el concepto de cobro",r.conceptoCobroId()));
        validar(r,i,t,c,0L); return mapper.respuesta(repository.saveAndFlush(mapper.nueva(normalizar(r),i,t,c,actor())));}
    public BecaAlumnoResponse actualizar(Long id,BecaAlumnoRequest r){BecaAlumno b=repository.findByIdForUpdate(id).orElseThrow(()->new RecursoNoEncontradoException("la beca del alumno",id)); verificar(b,r.version(),"Beca del alumno");
        inscripcionRepository.findByIdForUpdate(b.getInscripcion().getId())
                .orElseThrow(()->new RecursoNoEncontradoException("la inscripción",b.getInscripcion().getId()));
        if(!b.getInscripcion().getId().equals(r.inscripcionId())||!b.getTipoBeca().getId().equals(r.tipoBecaId())||!b.getConceptoCobro().getId().equals(r.conceptoCobroId())) throw new ReglaNegocioException("No se pueden cambiar la inscripción, el tipo ni el concepto de una beca histórica");
        if(b.getEstado()==EstadoBeca.FINALIZADA||b.getEstado()==EstadoBeca.CANCELADA) throw new ReglaNegocioException("Una beca finalizada o cancelada es histórica y no puede modificarse");
        validar(r,b.getInscripcion(),b.getTipoBeca(),b.getConceptoCobro(),id); mapper.actualizar(b,normalizar(r)); return mapper.respuesta(repository.saveAndFlush(b));}
    @Transactional(readOnly=true) public BecaAlumnoResponse obtener(Long id){return mapper.respuesta(repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("la beca del alumno",id)));}
    private void validar(BecaAlumnoRequest r,escuela.inscripcion.entity.Inscripcion i,TipoBeca t,ConceptoCobro c,Long id){Long inst=i.getAlumno().getInstitucion().getId();
        if(!inst.equals(t.getInstitucion().getId())||!inst.equals(c.getInstitucion().getId())) throw new ReglaNegocioException("La inscripción, el tipo de beca y el concepto deben pertenecer a la misma institución");
        if(r.estado()==EstadoBeca.ACTIVA&&(!VIGENTES.contains(i.getEstado())||!t.isActivo()||!c.isActivo())) throw new ReglaNegocioException("Una beca activa requiere inscripción, tipo de beca y concepto vigentes");
        if(r.estado()==EstadoBeca.ACTIVA&&!c.isPermiteBeca()) throw new ReglaNegocioException("El concepto seleccionado no permite becas");
        if(r.fechaFin().isBefore(r.fechaInicio())) throw new ReglaNegocioException("El fin de la beca no puede ser anterior al inicio");
        var limiteFin=i.getFechaFin()==null?i.getCicloEscolar().getFechaFin():i.getFechaFin().isBefore(i.getCicloEscolar().getFechaFin())?i.getFechaFin():i.getCicloEscolar().getFechaFin();
        if(r.fechaInicio().isBefore(i.getFechaInicio())||r.fechaFin().isAfter(limiteFin)) throw new ReglaNegocioException("La beca debe quedar dentro de la inscripción: "+i.getFechaInicio()+" a "+limiteFin);
        if(r.modalidad()==ModalidadBeca.PORCENTAJE){if(r.porcentaje()==null||r.porcentaje().signum()<=0||r.porcentaje().compareTo(new java.math.BigDecimal("100"))>0||r.porcentaje().scale()>4||r.montoFijo()!=null) throw new ReglaNegocioException("La beca porcentual requiere un porcentaje mayor a 0 y máximo 100");}
        else {if(r.montoFijo()==null||r.montoFijo().signum()<=0||r.montoFijo().scale()>2||r.porcentaje()!=null) throw new ReglaNegocioException("La beca de monto fijo requiere un importe positivo con máximo dos decimales"); if(!codigo(r.moneda()).equals(i.getAlumno().getInstitucion().getMonedaPredeterminada())) throw new ReglaNegocioException("La moneda debe coincidir con la moneda de la institución");}
        if(r.estado()==EstadoBeca.ACTIVA&&!repository.buscarActivasSuperpuestas(i.getId(),c.getId(),id,r.fechaInicio(),r.fechaFin()).isEmpty()) throw new ReglaNegocioException("Ya existe una beca activa para ese alumno, concepto y periodo");}
    private BecaAlumnoRequest normalizar(BecaAlumnoRequest r){return r.modalidad()==ModalidadBeca.PORCENTAJE?new BecaAlumnoRequest(r.inscripcionId(),r.tipoBecaId(),r.conceptoCobroId(),r.modalidad(),r.porcentaje().setScale(Math.min(4,Math.max(0,r.porcentaje().scale())),RoundingMode.UNNECESSARY),null,null,r.fechaInicio(),r.fechaFin(),r.motivo(),r.estado(),r.version()):new BecaAlumnoRequest(r.inscripcionId(),r.tipoBecaId(),r.conceptoCobroId(),r.modalidad(),null,r.montoFijo().setScale(2,RoundingMode.UNNECESSARY),codigo(r.moneda()),r.fechaInicio(),r.fechaFin(),r.motivo(),r.estado(),r.version());}
    private Usuario actor(){var a=SecurityContextHolder.getContext().getAuthentication(); if(a!=null&&a.getPrincipal() instanceof UsuarioPrincipal p&&!p.accesoRecuperacion()) return usuarioRepository.findById(p.usuarioId()).orElse(null); return null;}
}
