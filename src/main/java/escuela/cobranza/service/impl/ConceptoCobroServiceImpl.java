package escuela.cobranza.service.impl;

import escuela.cobranza.dto.request.ConceptoCobroRequest;
import escuela.cobranza.dto.response.ConceptoCobroResponse;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.mapper.ConceptoCobroMapper;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.cobranza.repository.BecaAlumnoRepository;
import escuela.cobranza.entity.EstadoBeca;
import escuela.cobranza.service.ConceptoCobroService;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class ConceptoCobroServiceImpl implements ConceptoCobroService {

    private final ConceptoCobroRepository repository;
    private final CuotaAlumnoRepository cuotaRepository;
    private final BecaAlumnoRepository becaRepository;
    private final InstitucionRepository institucionRepository;
    private final ConceptoCobroMapper mapper;

    @Override
    public ConceptoCobroResponse crear(ConceptoCobroRequest request) {
        Institucion institucion = institucionRepository.buscarPorIdConBloqueo(request.institucionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", request.institucionId()));
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("La institución debe estar activa para crear conceptos");
        }
        validarCodigo(request, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, institucion)));
    }

    @Override
    public ConceptoCobroResponse actualizar(Long id, ConceptoCobroRequest request) {
        ConceptoCobro entidad = buscar(id);
        verificar(entidad, request.version(), "Concepto de cobro");
        if (!entidad.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución del concepto");
        }
        if (request.activo() && !entidad.getInstitucion().isActivo()) {
            throw new ReglaNegocioException("No se puede activar un concepto de una institución inactiva");
        }
        if (!request.activo() && cuotaRepository.existsByConceptoCobroIdAndEstado(id, EstadoCuota.ACTIVA)) {
            throw new ReglaNegocioException("Finaliza o suspende las cuotas activas antes de desactivar el concepto");
        }
        if ((!request.activo() || !request.permiteBeca())
                && becaRepository.existsByConceptoCobroIdAndEstado(id, EstadoBeca.ACTIVA)) {
            throw new ReglaNegocioException("Suspende o finaliza las becas activas antes de retirar esta autorización");
        }
        validarCodigo(request, id);
        mapper.actualizar(entidad, request);
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public ConceptoCobroResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    public void desactivar(Long id, Long version) {
        ConceptoCobro entidad = buscar(id);
        verificar(entidad, version, "Concepto de cobro");
        if (cuotaRepository.existsByConceptoCobroIdAndEstado(id, EstadoCuota.ACTIVA)) {
            throw new ReglaNegocioException("Finaliza o suspende las cuotas activas antes de desactivar el concepto");
        }
        if (becaRepository.existsByConceptoCobroIdAndEstado(id, EstadoBeca.ACTIVA)) {
            throw new ReglaNegocioException("Suspende o finaliza las becas activas antes de desactivar el concepto");
        }
        entidad.setActivo(false);
    }

    private void validarCodigo(ConceptoCobroRequest request, Long id) {
        if (repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                request.institucionId(), codigo(request.codigo()), id)) {
            throw new RecursoDuplicadoException("Ya existe un concepto con ese código en la institución");
        }
    }

    private ConceptoCobro buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el concepto de cobro", id));
    }
}
