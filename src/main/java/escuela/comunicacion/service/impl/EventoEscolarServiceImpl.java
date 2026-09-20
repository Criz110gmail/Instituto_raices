package escuela.comunicacion.service.impl;

import escuela.academico.entity.*;
import escuela.academico.repository.*;
import escuela.alumno.repository.AlumnoRepository;
import escuela.comunicacion.dto.request.*;
import escuela.comunicacion.dto.response.EventoEscolarResponse;
import escuela.comunicacion.entity.*;
import escuela.comunicacion.mapper.EventoEscolarMapper;
import escuela.comunicacion.repository.EventoEscolarRepository;
import escuela.comunicacion.service.EventoEscolarService;
import escuela.common.exception.*;
import escuela.institucion.entity.Plantel;
import escuela.institucion.repository.*;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class EventoEscolarServiceImpl implements EventoEscolarService {
    private final EventoEscolarRepository repository;
    private final InstitucionRepository institucionRepository;
    private final CicloEscolarRepository cicloRepository;
    private final PlantelRepository plantelRepository;
    private final NivelEducativoRepository nivelRepository;
    private final GradoRepository gradoRepository;
    private final GrupoRepository grupoRepository;
    private final AlumnoRepository alumnoRepository;
    private final PlantelNivelRepository ofertaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EventoEscolarMapper mapper;
    private final AlcanceDatosService alcanceDatos;

    @Override public EventoEscolarResponse crear(EventoEscolarRequest request) {
        var contexto = contexto(request);
        EventoEscolar evento = new EventoEscolar();
        evento.setInstitucion(contexto.institucion()); evento.setCicloEscolar(contexto.ciclo());
        aplicar(evento, request, contexto);
        evento.setEstado(EstadoEventoEscolar.BORRADOR);
        return mapper.respuesta(repository.saveAndFlush(evento));
    }

    @Override public EventoEscolarResponse actualizar(Long id, EventoEscolarRequest request) {
        EventoEscolar evento = bloquear(id); verificar(evento, request.version(), "Evento escolar");
        if (evento.getEstado() != EstadoEventoEscolar.BORRADOR)
            throw new ReglaNegocioException("Sólo los eventos en borrador pueden modificarse");
        if (!evento.getInstitucion().getId().equals(request.institucionId())
                || !evento.getCicloEscolar().getId().equals(request.cicloEscolarId()))
            throw new ReglaNegocioException("La institución y el ciclo de un evento histórico no pueden cambiarse");
        aplicar(evento, request, contexto(request));
        return mapper.respuesta(repository.saveAndFlush(evento));
    }

    @Override public EventoEscolarResponse publicar(Long id, Long version) {
        EventoEscolar evento = bloquear(id); verificar(evento, version, "Evento escolar");
        alcance(evento.getInstitucion().getId(), evento.getAlcance(), evento.getPlantel());
        if (evento.getEstado() != EstadoEventoEscolar.BORRADOR)
            throw new ReglaNegocioException("El evento ya fue publicado o cancelado");
        if (evento.getCicloEscolar().getEstado() == EstadoAcademico.CERRADO)
            throw new ReglaNegocioException("No se puede publicar un evento en un ciclo cerrado");
        if (evento.getAlcance() == AlcanceEventoEscolar.SELECCION && evento.getDestinatarios().isEmpty())
            throw new ReglaNegocioException("Agrega al menos un destinatario antes de publicar");
        evento.setEstado(EstadoEventoEscolar.PUBLICADO); evento.setPublicadoEn(Instant.now());
        return mapper.respuesta(repository.saveAndFlush(evento));
    }

    @Override public EventoEscolarResponse cancelar(Long id, Long version, String motivo) {
        EventoEscolar evento = bloquear(id); verificar(evento, version, "Evento escolar");
        alcance(evento.getInstitucion().getId(), evento.getAlcance(), evento.getPlantel());
        if (evento.getEstado() == EstadoEventoEscolar.CANCELADO)
            throw new ReglaNegocioException("El evento ya está cancelado");
        String razon = limpiar(motivo);
        if (razon == null || razon.length() > 2000)
            throw new ReglaNegocioException("Explica la cancelación en un máximo de 2000 caracteres");
        evento.setEstado(EstadoEventoEscolar.CANCELADO); evento.setCanceladoEn(Instant.now());
        evento.setMotivoCancelacion(razon);
        return mapper.respuesta(repository.saveAndFlush(evento));
    }

    @Override @Transactional(readOnly = true) public EventoEscolarResponse obtener(Long id) {
        alcanceDatos.validarRecurso(escuela.admin.dto.ModuloCatalogo.EVENTOS_ESCOLARES, id);
        return mapper.respuesta(repository.findDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el evento escolar", id)));
    }

    private void aplicar(EventoEscolar evento, EventoEscolarRequest request, Contexto c) {
        validarBasico(request, c);
        evento.setPlantel(c.plantel()); evento.setTitulo(limpiar(request.titulo()));
        evento.setUbicacion(limpiar(request.ubicacion())); evento.setDescripcion(limpiar(request.descripcion()));
        ZoneId zona = ZoneId.of(c.institucion().getZonaHoraria());
        evento.setInicioEn(request.inicioLocal().atZone(zona).toInstant());
        evento.setFinEn(request.finLocal().atZone(zona).toInstant());
        evento.setTipo(request.tipo()); evento.setAlcance(request.alcance());
        evento.reemplazarDestinatarios(destinatarios(request, c));
    }

    private Contexto contexto(EventoEscolarRequest request) {
        var institucion = institucionRepository.findById(request.institucionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", request.institucionId()));
        var ciclo = cicloRepository.findById(request.cicloEscolarId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el ciclo escolar", request.cicloEscolarId()));
        Plantel plantel = request.plantelId() == null ? null : plantelRepository.findById(request.plantelId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el plantel", request.plantelId()));
        return new Contexto(institucion, ciclo, plantel);
    }

    private void validarBasico(EventoEscolarRequest r, Contexto c) {
        alcance(c.institucion().getId(), r.alcance(), c.plantel());
        if (!c.institucion().isActivo()) throw new ReglaNegocioException("La institución está inactiva");
        if (!c.ciclo().getInstitucion().getId().equals(c.institucion().getId()))
            throw new ReglaNegocioException("El ciclo no pertenece a la institución indicada");
        if (c.ciclo().getEstado() == EstadoAcademico.CERRADO)
            throw new ReglaNegocioException("No se puede preparar un evento en un ciclo cerrado");
        if (c.plantel() != null && (!c.plantel().getInstitucion().getId().equals(c.institucion().getId())
                || !c.plantel().isActivo())) throw new ReglaNegocioException("El plantel no está activo en la institución");
        if (r.finLocal().isBefore(r.inicioLocal()))
            throw new ReglaNegocioException("El fin del evento no puede ser anterior al inicio");
        if (r.inicioLocal().toLocalDate().isBefore(c.ciclo().getFechaInicio())
                || r.finLocal().toLocalDate().isAfter(c.ciclo().getFechaFin()))
            throw new ReglaNegocioException("El evento debe quedar dentro de las fechas del ciclo escolar");
        List<DestinatarioEventoRequest> destinos = r.destinatarios() == null ? List.of() : r.destinatarios();
        if (r.alcance() == AlcanceEventoEscolar.INSTITUCION && (c.plantel() != null || !destinos.isEmpty()))
            throw new ReglaNegocioException("Un evento institucional no lleva plantel ni selección de destinatarios");
        if (r.alcance() == AlcanceEventoEscolar.PLANTEL && (c.plantel() == null || !destinos.isEmpty()))
            throw new ReglaNegocioException("Un evento de plantel requiere plantel y no lleva selección adicional");
        if (r.alcance() == AlcanceEventoEscolar.SELECCION && destinos.isEmpty())
            throw new ReglaNegocioException("Un evento de selección requiere al menos un destinatario");
    }

    private void alcance(Long institucionId, AlcanceEventoEscolar tipo, Plantel plantel) {
        alcanceDatos.validarInstitucion(institucionId);
        if (plantel != null) alcanceDatos.validarPlantel(plantel.getId());
        if ((tipo == AlcanceEventoEscolar.INSTITUCION || plantel == null)
                && !alcanceDatos.alcanceInstitucionalActual(institucionId))
            throw new org.springframework.security.access.AccessDeniedException(
                    "Se requiere alcance institucional para este evento");
    }

    private List<DestinatarioEvento> destinatarios(EventoEscolarRequest r, Contexto c) {
        if (r.alcance() != AlcanceEventoEscolar.SELECCION) return List.of();
        Set<String> unicos = new HashSet<>(); List<DestinatarioEvento> resultado = new ArrayList<>();
        for (DestinatarioEventoRequest destino : r.destinatarios()) {
            String clave = destino.tipo() + ":" + destino.id();
            if (!unicos.add(clave)) throw new ReglaNegocioException("No repitas un destinatario en el evento");
            DestinatarioEvento entidad = new DestinatarioEvento();
            switch (destino.tipo()) {
                case NIVEL -> validarNivel(entidad, destino.id(), c);
                case GRADO -> validarGrado(entidad, destino.id(), c);
                case GRUPO -> validarGrupo(entidad, destino.id(), c);
                case ALUMNO -> validarAlumno(entidad, destino.id(), c);
            }
            resultado.add(entidad);
        }
        return resultado;
    }

    private void validarNivel(DestinatarioEvento d, Long id, Contexto c) {
        NivelEducativo nivel = nivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el nivel educativo", id));
        if (!nivel.isActivo() || !nivel.getInstitucion().getId().equals(c.institucion().getId()))
            throw new ReglaNegocioException("El nivel seleccionado no está activo en la institución");
        validarOferta(c.plantel(), nivel.getId()); d.setNivelEducativo(nivel);
    }
    private void validarGrado(DestinatarioEvento d, Long id, Contexto c) {
        Grado grado = gradoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el grado", id));
        if (!grado.isActivo() || !grado.getNivelEducativo().isActivo()
                || !grado.getNivelEducativo().getInstitucion().getId().equals(c.institucion().getId()))
            throw new ReglaNegocioException("El grado seleccionado no está activo en la institución");
        validarOferta(c.plantel(), grado.getNivelEducativo().getId()); d.setGrado(grado);
    }
    private void validarGrupo(DestinatarioEvento d, Long id, Contexto c) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el grupo", id));
        if (!grupo.isActivo() || !grupo.getCicloEscolar().getId().equals(c.ciclo().getId())
                || !grupo.getPlantel().getInstitucion().getId().equals(c.institucion().getId())
                || (c.plantel() != null && !grupo.getPlantel().getId().equals(c.plantel().getId())))
            throw new ReglaNegocioException("El grupo no corresponde al ciclo y alcance del evento");
        d.setGrupo(grupo);
    }
    private void validarAlumno(DestinatarioEvento d, Long id, Contexto c) {
        var alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", id));
        if (!alumno.isActivo() || !alumno.getInstitucion().getId().equals(c.institucion().getId())
                || !inscripcionRepository.existeAlumnoEnCicloYPlantel(id, c.ciclo().getId(),
                c.plantel() == null ? null : c.plantel().getId()))
            throw new ReglaNegocioException("El alumno no tiene una inscripción vigente en el ciclo y alcance del evento");
        d.setAlumno(alumno);
    }
    private void validarOferta(Plantel plantel, Long nivelId) {
        if (plantel != null && !ofertaRepository.existsByPlantelIdAndNivelEducativoIdAndActivoTrue(
                plantel.getId(), nivelId)) throw new ReglaNegocioException("El plantel no ofrece el nivel seleccionado");
    }
    private EventoEscolar bloquear(Long id) { return repository.findByIdForUpdate(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("el evento escolar", id)); }
    private record Contexto(escuela.institucion.entity.Institucion institucion,
                            CicloEscolar ciclo, Plantel plantel) { }
}
