package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.entity.CorteCaja;
import escuela.finanzas.entity.EstadoCorteCaja;
import escuela.finanzas.repository.CorteCajaRepository;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CorteCajaConsultaService {
    private static final DateTimeFormatter FECHA = DateTimeFormatter
            .ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"));

    private final CorteCajaRepository repository;
    private final InstitucionRepository institucionRepository;
    private final AlcanceDatosService alcance;

    public ResultadoCortesCaja consultar(FiltroCorteCaja original) {
        FiltroCorteCaja filtro = original.normalizado();
        if (filtro.institucionId() == null)
            throw new ReglaNegocioException("Selecciona una institución para consultar los cortes de caja");
        if (filtro.fechaDesde() != null && filtro.fechaHasta() != null
                && filtro.fechaDesde().isAfter(filtro.fechaHasta()))
            throw new ReglaNegocioException("La fecha inicial no puede ser posterior a la fecha final");
        alcance.validarInstitucion(filtro.institucionId());
        var institucion = institucionRepository.findById(filtro.institucionId())
                .orElseThrow(() -> new ReglaNegocioException("La institución seleccionada no existe"));
        ZoneId zona = ZoneId.of(institucion.getZonaHoraria());
        Specification<CorteCaja> especificacion = especificacion(filtro, zona);
        Page<CorteCajaFila> pagina = repository.findAll(especificacion,
                        PageRequest.of(filtro.pagina(), filtro.tamanio(),
                                Sort.by(Sort.Order.desc("abiertoEn"), Sort.Order.desc("id"))))
                .map(corte -> fila(corte, zona));
        long abiertos = repository.count(Specification.where(alcance.especificacionCortesCaja())
                .and((root, query, cb) -> cb.equal(root.get("institucion").get("id"), filtro.institucionId()))
                .and((root, query, cb) -> cb.equal(root.get("estado"), EstadoCorteCaja.ABIERTO)));
        return new ResultadoCortesCaja(pagina, abiertos);
    }

    private Specification<CorteCaja> especificacion(FiltroCorteCaja filtro, ZoneId zona) {
        return Specification.where(alcance.especificacionCortesCaja())
                .and((root, query, cb) -> cb.equal(root.get("institucion").get("id"), filtro.institucionId()))
                .and((root, query, cb) -> filtro.cuentaId() == null ? cb.conjunction()
                        : cb.equal(root.get("cuenta").get("id"), filtro.cuentaId()))
                .and((root, query, cb) -> filtro.estado().equals("TODOS") ? cb.conjunction()
                        : cb.equal(root.get("estado").as(String.class), filtro.estado()))
                .and((root, query, cb) -> filtro.fechaDesde() == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("abiertoEn"),
                        filtro.fechaDesde().atStartOfDay(zona).toInstant()))
                .and((root, query, cb) -> filtro.fechaHasta() == null ? cb.conjunction()
                        : cb.lessThan(root.get("abiertoEn"),
                        filtro.fechaHasta().plusDays(1).atStartOfDay(zona).toInstant()));
    }

    private CorteCajaFila fila(CorteCaja corte, ZoneId zona) {
        return new CorteCajaFila(corte.getId(), corte.getCuenta().getCodigo() + " · " + corte.getCuenta().getNombre(),
                corte.getCuenta().getPlantel() == null ? "Institucional" : corte.getCuenta().getPlantel().getNombre(),
                corte.getEstado().name(), FECHA.withZone(zona).format(corte.getAbiertoEn()),
                corte.getAbiertoPor().getUsername(),
                corte.getCerradoEn() == null ? "—" : FECHA.withZone(zona).format(corte.getCerradoEn()),
                corte.getSaldoInicialSistema(), corte.getSaldoEsperado(), corte.getEfectivoDeclarado(),
                corte.getDiferencia(), corte.getCuenta().getMoneda());
    }
}
