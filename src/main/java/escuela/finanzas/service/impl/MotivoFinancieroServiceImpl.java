package escuela.finanzas.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.request.MotivoFinancieroRequest;
import escuela.finanzas.dto.response.MotivoFinancieroResponse;
import escuela.finanzas.entity.MotivoFinanciero;
import escuela.finanzas.mapper.MotivoFinancieroMapper;
import escuela.finanzas.repository.MotivoFinancieroRepository;
import escuela.finanzas.service.MotivoFinancieroService;
import escuela.institucion.repository.InstitucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class MotivoFinancieroServiceImpl implements MotivoFinancieroService {
    private static final String MOTIVO_RESERVADO = "COBROS_ESCOLARES";
    private static final String MOTIVO_TRASPASO = "TRASPASO_INTERNO";
    private final MotivoFinancieroRepository repository;
    private final InstitucionRepository institucionRepository;
    private final MotivoFinancieroMapper mapper;

    @Override
    public MotivoFinancieroResponse crear(MotivoFinancieroRequest request) {
        var institucion = institucionRepository.buscarPorIdConBloqueo(request.institucionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", request.institucionId()));
        if (!institucion.isActivo()) throw new ReglaNegocioException("La institución debe estar activa para crear motivos financieros");
        validarCodigo(request, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(request, institucion)));
    }

    @Override
    public MotivoFinancieroResponse actualizar(Long id, MotivoFinancieroRequest request) {
        MotivoFinanciero motivo = buscar(id);
        verificar(motivo, request.version(), "Motivo financiero");
        if (!motivo.getInstitucion().getId().equals(request.institucionId()))
            throw new ReglaNegocioException("No se puede cambiar la institución del motivo financiero");
        validarReservado(motivo, request);
        validarCodigo(request, id);
        mapper.actualizar(motivo, request);
        return mapper.respuesta(repository.saveAndFlush(motivo));
    }

    @Override
    @Transactional(readOnly = true)
    public MotivoFinancieroResponse obtener(Long id) { return mapper.respuesta(buscar(id)); }

    @Override
    @Transactional(readOnly = true)
    public List<MotivoFinancieroResponse> listarActivos(Long institucionId) {
        return repository.findAllByInstitucionIdAndActivoTrueOrderByNombreAsc(institucionId)
                .stream().map(mapper::respuesta).toList();
    }

    @Override
    public void desactivar(Long id, Long version) {
        MotivoFinanciero motivo = buscar(id);
        verificar(motivo, version, "Motivo financiero");
        if (esReservado(motivo.getCodigo()))
            throw new ReglaNegocioException("El motivo es reservado para cobros o traspasos y no puede desactivarse");
        motivo.setActivo(false);
    }

    private void validarReservado(MotivoFinanciero actual, MotivoFinancieroRequest request) {
        if (MOTIVO_RESERVADO.equalsIgnoreCase(actual.getCodigo())
                && (!MOTIVO_RESERVADO.equalsIgnoreCase(request.codigo()) || !request.activo()
                || request.naturaleza() != escuela.finanzas.entity.NaturalezaMotivoFinanciero.INGRESO))
            throw new ReglaNegocioException("El motivo COBROS_ESCOLARES es reservado y debe permanecer activo como ingreso");
        if (MOTIVO_TRASPASO.equalsIgnoreCase(actual.getCodigo())
                && (!MOTIVO_TRASPASO.equalsIgnoreCase(request.codigo()) || !request.activo()
                || request.naturaleza() != escuela.finanzas.entity.NaturalezaMotivoFinanciero.AMBOS))
            throw new ReglaNegocioException("El motivo TRASPASO_INTERNO es reservado y debe permanecer activo para ambas direcciones");
    }

    private boolean esReservado(String codigo) {
        return MOTIVO_RESERVADO.equalsIgnoreCase(codigo) || MOTIVO_TRASPASO.equalsIgnoreCase(codigo);
    }

    private void validarCodigo(MotivoFinancieroRequest request, Long id) {
        if (repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
                request.institucionId(), codigo(request.codigo()), id))
            throw new RecursoDuplicadoException("Ya existe un motivo financiero con ese código en la institución");
    }

    private MotivoFinanciero buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el motivo financiero", id));
    }
}
