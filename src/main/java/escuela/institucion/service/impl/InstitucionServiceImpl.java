package escuela.institucion.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.institucion.dto.request.InstitucionRequest;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.entity.Institucion;
import escuela.institucion.mapper.InstitucionMapper;
import escuela.institucion.repository.InstitucionRepository;
import escuela.institucion.service.InstitucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class InstitucionServiceImpl implements InstitucionService {

    private final InstitucionRepository repository;
    private final InstitucionMapper mapper;

    @Override
    public InstitucionResponse crear(InstitucionRequest request) {
        validarCodigo(request.codigo(), 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nueva(request)));
    }

    @Override
    public InstitucionResponse actualizar(Long id, InstitucionRequest request) {
        Institucion entidad = buscar(id);
        verificar(entidad, request.version(), "Institución");
        validarCodigo(request.codigo(), id);
        mapper.actualizar(entidad, request);
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public InstitucionResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstitucionResponse> listar() {
        return repository.findAll().stream().map(mapper::respuesta).toList();
    }

    @Override
    public void desactivar(Long id, Long version) {
        Institucion entidad = buscar(id);
        verificar(entidad, version, "Institución");
        entidad.setActivo(false);
    }

    private Institucion buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
    }

    private void validarCodigo(String valor, Long idExcluido) {
        if (repository.existsByCodigoIgnoreCaseAndIdNot(codigo(valor), idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe una institución con ese código");
        }
    }
}
