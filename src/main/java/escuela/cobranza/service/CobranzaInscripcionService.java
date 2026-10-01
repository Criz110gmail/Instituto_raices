package escuela.cobranza.service;

import escuela.alumno.entity.AlumnoTutor;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.cobranza.dto.request.CuotaAlumnoRequest;
import escuela.cobranza.dto.response.CobranzaInscripcionResumen;
import escuela.cobranza.dto.response.CobranzaInscripcionResumen.TutorPagoSugerido;
import escuela.cobranza.dto.response.ResultadoCobranzaAsistida;
import escuela.cobranza.mapper.CargoMapper;
import escuela.cobranza.mapper.CuotaAlumnoMapper;
import escuela.cobranza.repository.CargoRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.inscripcion.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class CobranzaInscripcionService {
    private final InscripcionRepository inscripcionRepository;
    private final CuotaAlumnoRepository cuotaRepository;
    private final CargoRepository cargoRepository;
    private final AlumnoTutorRepository alumnoTutorRepository;
    private final CuotaAlumnoMapper cuotaMapper;
    private final CargoMapper cargoMapper;
    private final CuotaAlumnoService cuotaService;
    private final CargoService cargoService;

    @Transactional(readOnly = true)
    public CobranzaInscripcionResumen resumen(Long inscripcionId) {
        var inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la inscripción", inscripcionId));
        var cuotas = cuotaRepository.findAllByInscripcionIdOrderByCreadoEnDescIdDesc(inscripcionId)
                .stream().map(cuotaMapper::respuesta).toList();
        var cargos = cargoRepository.findAllByInscripcionIdOrderByFechaVencimientoDescIdDesc(inscripcionId)
                .stream().map(cargoMapper::respuesta).toList();
        var cuotasConCargo = cargos.stream().map(cargo -> cargo.cuotaAlumnoId())
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        boolean cargoPagado = cargos.stream()
                .anyMatch(cargo -> cargo.situacionCobro() == escuela.cobranza.entity.SituacionCobro.PAGADO);
        var responsables = alumnoTutorRepository.responsablesFinancierosVigentes(
                inscripcion.getAlumno().getId(), LocalDate.now());
        TutorPagoSugerido sugerido = responsables.isEmpty() ? null
                : new TutorPagoSugerido(responsables.getFirst().getTutor().getId(),
                nombre(responsables.getFirst()), responsables.size());
        return new CobranzaInscripcionResumen(cuotas, cargos, cuotasConCargo, cargoPagado, sugerido);
    }

    public ResultadoCobranzaAsistida crearCuota(CuotaAlumnoRequest request,
                                                  boolean generarCargoAhora) {
        var cuota = cuotaService.crear(request);
        var cargo = generarCargoAhora ? cargoService.generarCargoUnico(cuota.id()) : null;
        return new ResultadoCobranzaAsistida(cuota, cargo);
    }

    @Transactional(readOnly = true)
    public CobranzaInscripcionResumen.TutorPagoSugerido tutorParaCargo(Long cargoId) {
        var cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el cargo", cargoId));
        var responsables = alumnoTutorRepository.responsablesFinancierosVigentes(
                cargo.getInscripcion().getAlumno().getId(), LocalDate.now());
        if (responsables.isEmpty()) return null;
        return new TutorPagoSugerido(responsables.getFirst().getTutor().getId(),
                nombre(responsables.getFirst()), responsables.size());
    }

    private String nombre(AlumnoTutor vinculo) {
        var tutor = vinculo.getTutor();
        return Stream.of(tutor.getNombres(), tutor.getPrimerApellido(), tutor.getSegundoApellido())
                .filter(valor -> valor != null && !valor.isBlank())
                .collect(Collectors.joining(" "));
    }
}
