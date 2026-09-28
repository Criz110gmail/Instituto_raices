package escuela.alumno.service.impl;

import escuela.alumno.dto.request.FichaMedicaAlumnoRequest;
import escuela.alumno.dto.response.FichaMedicaAlumnoResponse;
import escuela.alumno.entity.Alumno;
import escuela.alumno.entity.FichaMedicaAlumno;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.repository.FichaMedicaAlumnoRepository;
import escuela.alumno.service.FichaMedicaAlumnoService;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class FichaMedicaAlumnoServiceImpl implements FichaMedicaAlumnoService {
    private final AlumnoRepository alumnoRepository;
    private final FichaMedicaAlumnoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public FichaMedicaAlumnoResponse obtener(Long alumnoId) {
        buscar(alumnoId);
        return repository.findByAlumnoId(alumnoId).map(this::respuesta).orElse(null);
    }

    @Override
    public FichaMedicaAlumnoResponse guardar(Long alumnoId, FichaMedicaAlumnoRequest request) {
        Alumno alumno = buscarConBloqueo(alumnoId);
        if (!alumno.isActivo()) {
            throw new ReglaNegocioException("No se puede modificar la ficha médica de un alumno inactivo");
        }
        if (request.tipoSanguineo() == null) {
            throw new ReglaNegocioException("Selecciona el tipo sanguíneo o indica que no se conoce");
        }
        FichaMedicaAlumno ficha = repository.findByAlumnoId(alumnoId).orElseGet(() -> {
            FichaMedicaAlumno nueva = new FichaMedicaAlumno();
            nueva.setAlumno(alumno);
            return nueva;
        });
        if (ficha.getId() != null) verificar(ficha, request.version(), "Ficha médica");
        ficha.setTipoSanguineo(request.tipoSanguineo());
        ficha.setAlergias(limpiar(request.alergias()));
        ficha.setPadecimientos(limpiar(request.padecimientos()));
        ficha.setMedicamentos(limpiar(request.medicamentos()));
        ficha.setDiscapacidadNecesidades(limpiar(request.discapacidadNecesidades()));
        ficha.setRestriccionesFisicas(limpiar(request.restriccionesFisicas()));
        ficha.setRestriccionesAlimentarias(limpiar(request.restriccionesAlimentarias()));
        ficha.setServicioMedico(limpiar(request.servicioMedico()));
        ficha.setNumeroAfiliacion(limpiar(request.numeroAfiliacion()));
        ficha.setMedicoTratante(limpiar(request.medicoTratante()));
        ficha.setContactoEmergencia(limpiar(request.contactoEmergencia()));
        ficha.setTelefonoEmergencia(limpiar(request.telefonoEmergencia()));
        ficha.setObservaciones(limpiar(request.observaciones()));
        ficha.setAutorizaAtencionEmergencia(request.autorizaAtencionEmergencia());
        return respuesta(repository.saveAndFlush(ficha));
    }

    private Alumno buscar(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", id));
    }

    private Alumno buscarConBloqueo(Long id) {
        return alumnoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el alumno", id));
    }

    private FichaMedicaAlumnoResponse respuesta(FichaMedicaAlumno ficha) {
        return new FichaMedicaAlumnoResponse(ficha.getTipoSanguineo(), ficha.getAlergias(),
                ficha.getPadecimientos(), ficha.getMedicamentos(), ficha.getDiscapacidadNecesidades(),
                ficha.getRestriccionesFisicas(), ficha.getRestriccionesAlimentarias(),
                ficha.getServicioMedico(), ficha.getNumeroAfiliacion(), ficha.getMedicoTratante(),
                ficha.getContactoEmergencia(), ficha.getTelefonoEmergencia(), ficha.getObservaciones(),
                ficha.isAutorizaAtencionEmergencia(), ficha.getVersion());
    }
}

