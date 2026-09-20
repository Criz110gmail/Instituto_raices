package escuela.admin.repository;

import escuela.admin.dto.*;
import org.springframework.data.domain.Page;

import java.time.Instant;

public interface ReporteFinancieroRepository {
    Page<EstadoCuentaCargoFila> estadoCuenta(FiltroEstadoCuentaAlumno filtro,
                                             AlcanceReporteFinanciero alcance,
                                             Instant corteExclusivo);
    ResumenEstadoCuenta resumenEstadoCuenta(FiltroEstadoCuentaAlumno filtro,
                                             AlcanceReporteFinanciero alcance,
                                             Instant corteExclusivo, String moneda);
    Page<ReporteTesoreriaFila> tesoreria(FiltroReporteTesoreria filtro,
                                         AlcanceReporteFinanciero alcance,
                                         Instant desde, Instant hastaExclusivo,
                                         String zona);
    ResumenTesoreria resumenTesoreria(FiltroReporteTesoreria filtro,
                                       AlcanceReporteFinanciero alcance,
                                       Instant desde, Instant hastaExclusivo,
                                       String moneda);
}
