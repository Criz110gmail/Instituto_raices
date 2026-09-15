package escuela.cobranza.service.impl;

import escuela.cobranza.dto.request.TipoBecaRequest;
import escuela.cobranza.dto.response.TipoBecaResponse;
import escuela.cobranza.entity.*;
import escuela.cobranza.mapper.TipoBecaMapper;
import escuela.cobranza.repository.*;
import escuela.cobranza.service.TipoBecaService;
import escuela.common.exception.*;
import escuela.institucion.repository.InstitucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static escuela.common.mapper.NormalizacionTexto.codigo;
import static escuela.common.service.ValidacionVersion.verificar;

@Service @RequiredArgsConstructor @Transactional
public class TipoBecaServiceImpl implements TipoBecaService {
    private final TipoBecaRepository repository; private final BecaAlumnoRepository becaRepository;
    private final InstitucionRepository institucionRepository; private final TipoBecaMapper mapper;
    public TipoBecaResponse crear(TipoBecaRequest r) {
        var i=institucionRepository.buscarPorIdConBloqueo(r.institucionId())
                .orElseThrow(()->new RecursoNoEncontradoException("la institución",r.institucionId()));
        if(!i.isActivo()) throw new ReglaNegocioException("La institución debe estar activa para crear tipos de beca");
        validarCodigo(r,0L); return mapper.respuesta(repository.saveAndFlush(mapper.nuevo(r,i)));
    }
    public TipoBecaResponse actualizar(Long id, TipoBecaRequest r) {
        TipoBeca t=buscar(id); verificar(t,r.version(),"Tipo de beca");
        if(!t.getInstitucion().getId().equals(r.institucionId())) throw new ReglaNegocioException("No se puede cambiar la institución del tipo de beca");
        if(!r.activo()&&becaRepository.existsByTipoBecaIdAndEstado(id, EstadoBeca.ACTIVA))
            throw new ReglaNegocioException("Suspende o finaliza las becas activas antes de desactivar este tipo");
        validarCodigo(r,id); mapper.actualizar(t,r); return mapper.respuesta(repository.saveAndFlush(t));
    }
    @Transactional(readOnly=true) public TipoBecaResponse obtener(Long id){return mapper.respuesta(buscar(id));}
    public void desactivar(Long id,Long version){TipoBeca t=buscar(id);verificar(t,version,"Tipo de beca");
        if(becaRepository.existsByTipoBecaIdAndEstado(id,EstadoBeca.ACTIVA)) throw new ReglaNegocioException("Suspende o finaliza las becas activas antes de desactivar este tipo"); t.setActivo(false);}
    private void validarCodigo(TipoBecaRequest r,Long id){if(repository.existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(r.institucionId(),codigo(r.codigo()),id)) throw new RecursoDuplicadoException("Ya existe un tipo de beca con ese código en la institución");}
    private TipoBeca buscar(Long id){return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("el tipo de beca",id));}
}
