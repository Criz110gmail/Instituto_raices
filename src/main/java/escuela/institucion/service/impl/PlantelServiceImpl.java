package escuela.institucion.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.dto.request.PlantelRequest;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.institucion.mapper.PlantelMapper;
import escuela.institucion.repository.InstitucionRepository;
import escuela.institucion.repository.PlantelRepository;
import escuela.institucion.service.PlantelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class PlantelServiceImpl implements PlantelService {

    private final PlantelRepository repository;
    private final InstitucionRepository institucionRepository;
    private final PlantelMapper mapper;
    private final escuela.auditoria.service.RegistroAuditoriaService auditoria;

    @Override
    public PlantelResponse crear(PlantelRequest request) {
        Institucion institucion = institucionActiva(request.institucionId());
        validarCodigo(request.institucionId(), request.codigo(), 0L);
        if(request.permitirTransferenciasVencidas()) exigirActorConfiguracion(institucion.getId(),null);
        var nuevo=repository.saveAndFlush(mapper.nuevo(request, institucion));
        if(request.permitirTransferenciasVencidas()) registrarConfiguracion(nuevo,false);
        return mapper.respuesta(nuevo);
    }

    @Override
    public PlantelResponse actualizar(Long id, PlantelRequest request) {
        Plantel entidad = buscar(id);
        verificar(entidad, request.version(), "Plantel");
        if (!entidad.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución propietaria de un plantel");
        }
        validarCodigo(request.institucionId(), request.codigo(), id);
        boolean anterior=entidad.isPermitirTransferenciasVencidas();
        if(anterior!=request.permitirTransferenciasVencidas()) exigirActorConfiguracion(entidad.getInstitucion().getId(),entidad.getId());
        mapper.actualizar(entidad, request, entidad.getInstitucion());
        repository.saveAndFlush(entidad);
        if(anterior!=entidad.isPermitirTransferenciasVencidas()) registrarConfiguracion(entidad,anterior);
        return mapper.respuesta(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public PlantelResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantelResponse> listar() {
        return repository.findAllByOrderByNombreAsc().stream().map(mapper::respuesta).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantelResponse> listarPorInstitucion(Long institucionId) {
        return repository.findAllByInstitucionIdOrderByNombreAsc(institucionId).stream()
                .map(mapper::respuesta).toList();
    }

    @Override
    public void desactivar(Long id, Long version) {
        Plantel entidad = buscar(id);
        verificar(entidad, version, "Plantel");
        entidad.setActivo(false);
    }

    private Plantel buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el plantel", id));
    }

    private void exigirActorConfiguracion(Long institucionId,Long plantelId) {
        var auth=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if(auth==null || !(auth.getPrincipal() instanceof escuela.seguridad.service.UsuarioPrincipal p)
                || p.accesoRecuperacion() || p.usuarioId()==null || !institucionId.equals(p.institucionId())
                || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("PLANTEL_ADMINISTRAR")))
            throw new ReglaNegocioException("La configuración de transferencias requiere un administrador identificado con permiso de Planteles.");
        if(!p.alcanceInstitucional() && (plantelId==null || !p.plantelIds().contains(plantelId)))
            throw new org.springframework.security.access.AccessDeniedException("El plantel no pertenece a tu alcance administrativo.");
    }
    private void registrarConfiguracion(Plantel plantel,boolean anterior) {
        auditoria.registrar(plantel.getInstitucion().getId(),
                escuela.auditoria.entity.AccionAuditoria.TRANSFERENCIA_VENCIDA_CONFIGURADA,
                "PLANTEL",plantel.getId(),"Configuración de transferencias vencidas en portal familiar",
                java.util.Map.of("permitirAnterior",anterior,"permitirNuevo",plantel.isPermitirTransferenciasVencidas()));
    }

    private Institucion institucionActiva(Long id) {
        Institucion institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("No se pueden crear planteles en una institución inactiva");
        }
        return institucion;
    }

    private void validarCodigo(Long institucionId, String valor, Long idExcluido) {
        if (repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                institucionId, codigo(valor), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un plantel con ese código en la institución");
        }
    }
}
