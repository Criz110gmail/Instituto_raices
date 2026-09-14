package escuela.cobranza.service.impl;

import escuela.cobranza.dto.request.CuotaAlumnoRequest;
import escuela.cobranza.dto.response.CuotaAlumnoResponse;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.CuotaAlumno;
import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.entity.FrecuenciaCuota;
import escuela.cobranza.mapper.CuotaAlumnoMapper;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.cobranza.service.CuotaAlumnoService;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class CuotaAlumnoServiceImpl implements CuotaAlumnoService {

    private static final DateTimeFormatter FECHA_MENSAJE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final EnumSet<EstadoInscripcion> INSCRIPCIONES_CONFIGURABLES =
            EnumSet.of(EstadoInscripcion.PREINSCRITA, EstadoInscripcion.ACTIVA);

    private final CuotaAlumnoRepository repository;
    private final InscripcionRepository inscripcionRepository;
    private final ConceptoCobroRepository conceptoRepository;
    private final CuotaAlumnoMapper mapper;

    @Override
    public CuotaAlumnoResponse crear(CuotaAlumnoRequest request) {
        Inscripcion inscripcion = inscripcionRepository.findByIdForUpdate(request.inscripcionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la inscripción", request.inscripcionId()));
        ConceptoCobro concepto = concepto(request.conceptoCobroId());
        validar(request, inscripcion, concepto, 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.nueva(request, inscripcion, concepto)));
    }

    @Override
    public CuotaAlumnoResponse actualizar(Long id, CuotaAlumnoRequest request) {
        CuotaAlumno entidad = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuota del alumno", id));
        verificar(entidad, request.version(), "Cuota del alumno");
        if (!entidad.getInscripcion().getId().equals(request.inscripcionId())
                || !entidad.getConceptoCobro().getId().equals(request.conceptoCobroId())) {
            throw new ReglaNegocioException("No se pueden cambiar la inscripción ni el concepto de una cuota");
        }
        if (entidad.getEstado() == EstadoCuota.FINALIZADA) {
            throw new ReglaNegocioException("Una cuota finalizada no puede modificarse ni reactivarse");
        }
        validarTransicion(entidad.getEstado(), request.estado());
        validar(request, entidad.getInscripcion(), entidad.getConceptoCobro(), id);
        mapper.actualizar(entidad, request);
        return mapper.respuesta(repository.saveAndFlush(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public CuotaAlumnoResponse obtener(Long id) {
        return mapper.respuesta(repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuota del alumno", id)));
    }

    private void validar(CuotaAlumnoRequest request, Inscripcion inscripcion,
                         ConceptoCobro concepto, Long id) {
        if (!inscripcion.getAlumno().getInstitucion().getId().equals(concepto.getInstitucion().getId())) {
            throw new ReglaNegocioException("La inscripción y el concepto deben pertenecer a la misma institución");
        }
        if (request.estado() == EstadoCuota.ACTIVA
                && (!concepto.isActivo() || !INSCRIPCIONES_CONFIGURABLES.contains(inscripcion.getEstado()))) {
            throw new ReglaNegocioException("Una cuota activa requiere concepto e inscripción vigentes");
        }
        if (request.fechaFin().isBefore(request.fechaInicio())) {
            throw new ReglaNegocioException("La fecha final no puede ser anterior al inicio");
        }
        validarVigencia(request, inscripcion);
        if (request.importeBase().signum() < 0 || request.importeBase().scale() > 2) {
            throw new ReglaNegocioException("El importe debe ser positivo o cero y tener máximo dos decimales");
        }
        request.importeBase().setScale(2, RoundingMode.UNNECESSARY);
        validarVencimiento(request);
        if (request.estado() == EstadoCuota.ACTIVA
                && !repository.buscarSuperpuestas(inscripcion.getId(), concepto.getId(), id,
                EstadoCuota.ACTIVA, request.fechaInicio(), request.fechaFin()).isEmpty()) {
            throw new RecursoDuplicadoException("Ya existe una cuota activa de ese concepto durante esas fechas");
        }
        if (request.estado() != EstadoCuota.ACTIVA && request.generacionAutomatica()) {
            throw new ReglaNegocioException("Sólo una cuota activa puede generar cargos automáticamente");
        }
    }

    private void validarVigencia(CuotaAlumnoRequest request, Inscripcion inscripcion) {
        LocalDate inicioPermitido = fechaMayor(inscripcion.getFechaInicio(),
                inscripcion.getCicloEscolar().getFechaInicio());
        LocalDate finPermitido = inscripcion.getFechaFin() == null
                ? inscripcion.getCicloEscolar().getFechaFin()
                : fechaMenor(inscripcion.getFechaFin(), inscripcion.getCicloEscolar().getFechaFin());
        String rango = "Para esta inscripción y su ciclo escolar, la vigencia permitida es del "
                + FECHA_MENSAJE.format(inicioPermitido) + " al " + FECHA_MENSAJE.format(finPermitido) + ".";
        if (request.fechaInicio().isBefore(inicioPermitido)) {
            throw new ReglaNegocioException("El inicio de la cuota ("
                    + FECHA_MENSAJE.format(request.fechaInicio())
                    + ") está antes del rango permitido. " + rango);
        }
        if (request.fechaFin().isAfter(finPermitido)) {
            throw new ReglaNegocioException("El fin de la cuota ("
                    + FECHA_MENSAJE.format(request.fechaFin())
                    + ") está después del rango permitido. " + rango);
        }
    }

    private LocalDate fechaMayor(LocalDate primera, LocalDate segunda) {
        return primera.isAfter(segunda) ? primera : segunda;
    }

    private LocalDate fechaMenor(LocalDate primera, LocalDate segunda) {
        return primera.isBefore(segunda) ? primera : segunda;
    }

    private void validarVencimiento(CuotaAlumnoRequest request) {
        if (request.frecuencia() == FrecuenciaCuota.MENSUAL) {
            if (request.diaVencimiento() == null || request.diaVencimiento() < 1
                    || request.diaVencimiento() > 31 || request.fechaVencimientoUnico() != null) {
                throw new ReglaNegocioException("La cuota mensual requiere un día de vencimiento entre 1 y 31");
            }
            return;
        }
        if (request.diaVencimiento() != null || request.fechaVencimientoUnico() == null) {
            throw new ReglaNegocioException("La cuota única requiere una fecha exacta de vencimiento");
        }
        if (request.fechaVencimientoUnico().isBefore(request.fechaInicio())
                || request.fechaVencimientoUnico().isAfter(request.fechaFin())) {
            throw new ReglaNegocioException("El vencimiento único debe quedar dentro de la vigencia de la cuota");
        }
    }

    private void validarTransicion(EstadoCuota actual, EstadoCuota nueva) {
        boolean valida = actual == nueva
                || actual == EstadoCuota.ACTIVA
                    && (nueva == EstadoCuota.SUSPENDIDA || nueva == EstadoCuota.FINALIZADA)
                || actual == EstadoCuota.SUSPENDIDA
                    && (nueva == EstadoCuota.ACTIVA || nueva == EstadoCuota.FINALIZADA);
        if (!valida) throw new ReglaNegocioException("La transición de estado de la cuota no está permitida");
    }

    private ConceptoCobro concepto(Long id) {
        return conceptoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el concepto de cobro", id));
    }
}
