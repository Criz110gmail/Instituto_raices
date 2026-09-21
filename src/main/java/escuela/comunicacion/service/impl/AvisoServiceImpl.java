package escuela.comunicacion.service.impl;

import escuela.comunicacion.dto.request.AvisoRequest;
import escuela.comunicacion.dto.response.AvisoResponse;
import escuela.comunicacion.entity.*;
import escuela.comunicacion.mapper.AvisoMapper;
import escuela.comunicacion.repository.AvisoRepository;
import escuela.comunicacion.service.AvisoService;
import escuela.common.exception.*;
import escuela.institucion.entity.*;
import escuela.institucion.repository.*;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class AvisoServiceImpl implements AvisoService {
    private final AvisoRepository repository;
    private final InstitucionRepository institucionRepository;
    private final PlantelRepository plantelRepository;
    private final AvisoMapper mapper;
    private final AlcanceDatosService alcance;

    @Override public AvisoResponse crear(AvisoRequest r) {
        Contexto c = contexto(r); Aviso a = new Aviso(); a.setInstitucion(c.institucion());
        aplicar(a, r, c); a.setEstado(EstadoAviso.BORRADOR);
        return mapper.respuesta(repository.saveAndFlush(a));
    }
    @Override public AvisoResponse actualizar(Long id, AvisoRequest r) {
        Aviso a = bloquear(id); verificar(a, r.version(), "Aviso");
        if (a.getEstado() != EstadoAviso.BORRADOR) throw new ReglaNegocioException("Sólo los avisos en borrador pueden modificarse");
        if (!a.getInstitucion().getId().equals(r.institucionId()))
            throw new ReglaNegocioException("La institución del aviso no puede cambiarse");
        aplicar(a, r, contexto(r)); return mapper.respuesta(repository.saveAndFlush(a));
    }
    @Override public AvisoResponse publicar(Long id, Long version) {
        Aviso a = bloquear(id); verificar(a, version, "Aviso"); validarAlcance(a.getInstitucion().getId(), a.getPlantel());
        if (a.getEstado() != EstadoAviso.BORRADOR) throw new ReglaNegocioException("El aviso ya fue publicado o retirado");
        Instant ahora = Instant.now();
        if (a.getExpiraEn() != null && !a.getExpiraEn().isAfter(ahora))
            throw new ReglaNegocioException("El vencimiento debe ser posterior al momento de publicación");
        a.setEstado(EstadoAviso.PUBLICADO); a.setPublicadoEn(ahora);
        return mapper.respuesta(repository.saveAndFlush(a));
    }
    @Override public AvisoResponse retirar(Long id, Long version, String motivo) {
        Aviso a = bloquear(id); verificar(a, version, "Aviso"); validarAlcance(a.getInstitucion().getId(), a.getPlantel());
        if (a.getEstado() != EstadoAviso.PUBLICADO) throw new ReglaNegocioException("Sólo un aviso publicado puede retirarse");
        String razon = limpiar(motivo);
        if (razon == null || razon.length() > 1000) throw new ReglaNegocioException("Explica el retiro en un máximo de 1000 caracteres");
        a.setEstado(EstadoAviso.RETIRADO); a.setRetiradoEn(Instant.now()); a.setMotivoRetiro(razon);
        return mapper.respuesta(repository.saveAndFlush(a));
    }
    @Override @Transactional(readOnly = true) public AvisoResponse obtener(Long id) {
        alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.AVISOS, id);
        return mapper.respuesta(repository.findDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el aviso", id)));
    }
    private void aplicar(Aviso a, AvisoRequest r, Contexto c) {
        validarAlcance(c.institucion().getId(), c.plantel());
        if (!c.institucion().isActivo()) throw new ReglaNegocioException("La institución está inactiva");
        if (c.plantel() != null && (!c.plantel().isActivo()
                || !c.plantel().getInstitucion().getId().equals(c.institucion().getId())))
            throw new ReglaNegocioException("El plantel no está activo en la institución");
        a.setPlantel(c.plantel()); a.setTitulo(limpiar(r.titulo())); a.setContenido(limpiar(r.contenido()));
        a.setExpiraEn(r.expiraLocal() == null ? null
                : r.expiraLocal().atZone(ZoneId.of(c.institucion().getZonaHoraria())).toInstant());
    }
    private Contexto contexto(AvisoRequest r) {
        Institucion i = institucionRepository.findById(r.institucionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", r.institucionId()));
        Plantel p = r.plantelId() == null ? null : plantelRepository.findById(r.plantelId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el plantel", r.plantelId()));
        return new Contexto(i, p);
    }
    private void validarAlcance(Long institucionId, Plantel plantel) {
        alcance.validarInstitucion(institucionId);
        if (plantel == null && !alcance.alcanceInstitucionalActual(institucionId))
            throw new AccessDeniedException("Se requiere alcance institucional para un aviso general");
        if (plantel != null) alcance.validarPlantel(plantel.getId());
    }
    private Aviso bloquear(Long id) { return repository.findByIdForUpdate(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("el aviso", id)); }
    private record Contexto(Institucion institucion, Plantel plantel) { }
}
