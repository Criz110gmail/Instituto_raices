package escuela.admin.repository;

import escuela.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcReporteFinancieroRepository implements ReporteFinancieroRepository {
    private static final String ESTADO_CUENTA_CTE = """
            WITH importes AS (
                SELECT c.id, c.estado_registro, c.descripcion, c.fecha_emision,
                       c.fecha_vencimiento, c.importe_original, c.moneda,
                       p.nombre AS plantel, ce.nombre AS ciclo,
                       cc.codigo || ' · ' || cc.nombre AS concepto,
                       COALESCE((SELECT SUM(CASE WHEN ac.efecto = 'AUMENTO' THEN ac.monto ELSE -ac.monto END)
                                 FROM ajuste_cargo ac
                                 WHERE ac.cargo_id = c.id AND ac.fecha_efectiva <= :fechaCorte), 0) AS ajustes,
                       COALESCE((SELECT SUM(CASE WHEN ap.operacion = 'APLICAR' THEN ap.monto ELSE -ap.monto END)
                                 FROM aplicacion_pago ap
                                 WHERE ap.cargo_id = c.id AND ap.fecha_aplicacion < :corteExclusivo), 0) AS aplicado
                FROM cargo c
                JOIN inscripcion i ON i.id = c.inscripcion_id
                JOIN alumno a ON a.id = i.alumno_id
                JOIN plantel p ON p.id = i.plantel_id
                JOIN ciclo_escolar ce ON ce.id = i.ciclo_escolar_id
                JOIN concepto_cobro cc ON cc.id = c.concepto_cobro_id
                WHERE a.institucion_id = :institucionId
                  AND a.id = :alumnoId
                  AND c.fecha_emision <= :fechaCorte
                  AND (:institucional OR i.plantel_id IN (:plantelIds))
                  AND (CAST(:plantelId AS bigint) IS NULL OR i.plantel_id = :plantelId)
            ), calculados AS (
                SELECT *, GREATEST(importe_original + ajustes, 0) AS importe_total
                FROM importes
            ), estado AS (
                SELECT *, CASE WHEN estado_registro = 'CANCELADO' THEN 0
                               ELSE GREATEST(importe_total - aplicado, 0) END AS saldo,
                       CASE WHEN estado_registro = 'CANCELADO' THEN 'CANCELADO'
                            WHEN GREATEST(importe_total - aplicado, 0) = 0 THEN 'PAGADO'
                            WHEN fecha_vencimiento < :fechaCorte THEN 'VENCIDO'
                            WHEN aplicado > 0 THEN 'PARCIAL'
                            ELSE 'PENDIENTE' END AS situacion
                FROM calculados
            )
            """;

    private static final String TESORERIA_FROM = """
            FROM movimiento_financiero m
            JOIN cuenta_financiera c ON c.id = m.cuenta_id
            LEFT JOIN plantel pc ON pc.id = c.plantel_id
            LEFT JOIN reversion_financiera rf ON rf.id = m.reversion_financiera_id
            WHERE m.institucion_id = :institucionId
              AND m.fecha_operacion >= :fechaDesde AND m.fecha_operacion < :fechaHasta
              AND (:institucional OR c.plantel_id IN (:plantelIds))
              AND (CAST(:cuentaId AS bigint) IS NULL OR c.id = :cuentaId)
              AND (CAST(:plantelId AS bigint) IS NULL OR m.plantel_operacion_id = :plantelId)
            """;

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Page<EstadoCuentaCargoFila> estadoCuenta(FiltroEstadoCuentaAlumno filtro,
                                                    AlcanceReporteFinanciero alcance,
                                                    Instant corteExclusivo) {
        MapSqlParameterSource p = parametrosEstado(filtro, alcance, corteExclusivo)
                .addValue("limite", filtro.tamanio()).addValue("offset", (long) filtro.pagina() * filtro.tamanio());
        String condicion = " WHERE (:situacion = 'TODOS' OR situacion = :situacion) ";
        Long total = jdbc.queryForObject(ESTADO_CUENTA_CTE + "SELECT count(*) FROM estado" + condicion, p, Long.class);
        var filas = jdbc.query(ESTADO_CUENTA_CTE + """
                SELECT id, plantel, ciclo, concepto, descripcion, fecha_emision, fecha_vencimiento,
                       importe_original, ajustes, importe_total, aplicado, saldo, situacion, moneda
                FROM estado
                """ + condicion + " ORDER BY fecha_vencimiento, id LIMIT :limite OFFSET :offset", p,
                (rs, n) -> new EstadoCuentaCargoFila(rs.getLong("id"), rs.getString("plantel"),
                        rs.getString("ciclo"), rs.getString("concepto"), rs.getString("descripcion"),
                        rs.getObject("fecha_emision", LocalDate.class),
                        rs.getObject("fecha_vencimiento", LocalDate.class),
                        rs.getBigDecimal("importe_original"), rs.getBigDecimal("ajustes"),
                        rs.getBigDecimal("importe_total"), rs.getBigDecimal("aplicado"),
                        rs.getBigDecimal("saldo"), rs.getString("situacion"), rs.getString("moneda")));
        return new PageImpl<>(filas, PageRequest.of(filtro.pagina(), filtro.tamanio()), total == null ? 0 : total);
    }

    @Override
    public ResumenEstadoCuenta resumenEstadoCuenta(FiltroEstadoCuentaAlumno filtro,
                                                    AlcanceReporteFinanciero alcance,
                                                    Instant corteExclusivo, String moneda) {
        MapSqlParameterSource p = parametrosEstado(filtro, alcance, corteExclusivo);
        String condicion = " WHERE (:situacion = 'TODOS' OR situacion = :situacion) ";
        return jdbc.queryForObject(ESTADO_CUENTA_CTE + """
                SELECT count(*) AS cargos,
                       COALESCE(sum(CASE WHEN estado_registro = 'CANCELADO' THEN 0 ELSE importe_total END), 0) AS total,
                       COALESCE(sum(CASE WHEN estado_registro = 'CANCELADO' THEN 0 ELSE aplicado END), 0) AS aplicado,
                       COALESCE(sum(saldo), 0) AS saldo,
                       COALESCE(sum(CASE WHEN saldo > 0 AND fecha_vencimiento < :fechaCorte THEN saldo ELSE 0 END), 0) AS vencido
                FROM estado
                """ + condicion, p, (rs, n) -> new ResumenEstadoCuenta(rs.getLong("cargos"),
                rs.getBigDecimal("total"), rs.getBigDecimal("aplicado"), rs.getBigDecimal("saldo"),
                rs.getBigDecimal("vencido"), moneda));
    }

    @Override
    public Page<ReporteTesoreriaFila> tesoreria(FiltroReporteTesoreria filtro,
                                                AlcanceReporteFinanciero alcance,
                                                Instant desde, Instant hastaExclusivo,
                                                String zona) {
        MapSqlParameterSource p = parametrosTesoreria(filtro, alcance, desde, hastaExclusivo)
                .addValue("zona", zona).addValue("agrupacion", filtro.agrupacion())
                .addValue("limite", filtro.tamanio()).addValue("offset", (long) filtro.pagina() * filtro.tamanio());
        String periodo = """
                CASE :agrupacion WHEN 'MENSUAL' THEN date_trunc('month', timezone(:zona, m.fecha_operacion))::date
                                  WHEN 'ANUAL' THEN date_trunc('year', timezone(:zona, m.fecha_operacion))::date
                                  ELSE timezone(:zona, m.fecha_operacion)::date END
                """;
        String agrupada = """
                SELECT %s AS periodo, c.id AS cuenta_id, c.codigo || ' · ' || c.nombre AS cuenta,
                       c.tipo, COALESCE(pc.nombre, 'Institucional') AS alcance, c.moneda,
                       count(*) AS movimientos,
                       COALESCE(sum(CASE WHEN m.direccion='INGRESO' AND m.clase<>'TRASPASO'
                                          AND NOT (m.clase='REVERSO' AND rf.tipo='TRANSFERENCIA') THEN m.monto ELSE 0 END),0) AS ingresos,
                       COALESCE(sum(CASE WHEN m.direccion='EGRESO' AND m.clase<>'TRASPASO'
                                          AND NOT (m.clase='REVERSO' AND rf.tipo='TRANSFERENCIA') THEN m.monto ELSE 0 END),0) AS egresos,
                       COALESCE(sum(CASE WHEN m.direccion='INGRESO' AND (m.clase='TRASPASO'
                                          OR (m.clase='REVERSO' AND rf.tipo='TRANSFERENCIA')) THEN m.monto ELSE 0 END),0) AS traspasos_entrada,
                       COALESCE(sum(CASE WHEN m.direccion='EGRESO' AND (m.clase='TRASPASO'
                                          OR (m.clase='REVERSO' AND rf.tipo='TRANSFERENCIA')) THEN m.monto ELSE 0 END),0) AS traspasos_salida
                %s
                GROUP BY periodo, c.id, c.codigo, c.nombre, c.tipo, pc.nombre, c.moneda
                """.formatted(periodo, TESORERIA_FROM);
        Long total = jdbc.queryForObject("SELECT count(*) FROM (" + agrupada + ") x", p, Long.class);
        var filas = jdbc.query(agrupada + " ORDER BY periodo DESC, cuenta_id LIMIT :limite OFFSET :offset", p,
                (rs, n) -> new ReporteTesoreriaFila(rs.getObject("periodo", LocalDate.class),
                        rs.getString("cuenta"), rs.getString("tipo"), rs.getString("alcance"),
                        rs.getLong("movimientos"), rs.getBigDecimal("ingresos"), rs.getBigDecimal("egresos"),
                        rs.getBigDecimal("ingresos").subtract(rs.getBigDecimal("egresos")),
                        rs.getBigDecimal("traspasos_entrada"), rs.getBigDecimal("traspasos_salida"),
                        rs.getString("moneda")));
        return new PageImpl<>(filas, PageRequest.of(filtro.pagina(), filtro.tamanio()), total == null ? 0 : total);
    }

    @Override
    public ResumenTesoreria resumenTesoreria(FiltroReporteTesoreria filtro,
                                              AlcanceReporteFinanciero alcance,
                                              Instant desde, Instant hastaExclusivo,
                                              String moneda) {
        MapSqlParameterSource p = parametrosTesoreria(filtro, alcance, desde, hastaExclusivo);
        Flujo flujo = jdbc.queryForObject("""
                SELECT count(*) AS movimientos,
                       COALESCE(sum(CASE WHEN m.direccion='INGRESO' AND m.clase<>'TRASPASO'
                                          AND NOT (m.clase='REVERSO' AND rf.tipo='TRANSFERENCIA') THEN m.monto ELSE 0 END),0) AS ingresos,
                       COALESCE(sum(CASE WHEN m.direccion='EGRESO' AND m.clase<>'TRASPASO'
                                          AND NOT (m.clase='REVERSO' AND rf.tipo='TRANSFERENCIA') THEN m.monto ELSE 0 END),0) AS egresos,
                       COALESCE(sum(CASE WHEN m.direccion='INGRESO' AND (m.clase='TRASPASO'
                                          OR (m.clase='REVERSO' AND rf.tipo='TRANSFERENCIA')) THEN m.monto ELSE 0 END),0) AS traspasos_entrada,
                       COALESCE(sum(CASE WHEN m.direccion='EGRESO' AND (m.clase='TRASPASO'
                                          OR (m.clase='REVERSO' AND rf.tipo='TRANSFERENCIA')) THEN m.monto ELSE 0 END),0) AS traspasos_salida
                """ + TESORERIA_FROM, p, (rs, n) -> new Flujo(rs.getLong("movimientos"),
                rs.getBigDecimal("ingresos"), rs.getBigDecimal("egresos"),
                rs.getBigDecimal("traspasos_entrada"), rs.getBigDecimal("traspasos_salida")));
        boolean saldos = filtro.plantelId() == null;
        BigDecimal apertura = null, cierre = null;
        if (saldos) {
            Saldo saldo = jdbc.queryForObject("""
                    SELECT COALESCE(sum(CASE WHEN c.fecha_saldo_inicial <= :fechaDesdeLocal THEN c.saldo_inicial +
                               COALESCE((SELECT sum(CASE WHEN m.direccion='INGRESO' THEN m.monto ELSE -m.monto END)
                                         FROM movimiento_financiero m WHERE m.cuenta_id=c.id AND m.fecha_operacion < :fechaDesde),0)
                               ELSE 0 END),0) AS apertura,
                           COALESCE(sum(CASE WHEN c.fecha_saldo_inicial <= :fechaHastaLocal THEN c.saldo_inicial +
                               COALESCE((SELECT sum(CASE WHEN m.direccion='INGRESO' THEN m.monto ELSE -m.monto END)
                                         FROM movimiento_financiero m WHERE m.cuenta_id=c.id AND m.fecha_operacion < :fechaHasta),0)
                               ELSE 0 END),0) AS cierre
                    FROM cuenta_financiera c
                    WHERE c.institucion_id=:institucionId
                      AND (:institucional OR c.plantel_id IN (:plantelIds))
                      AND (CAST(:cuentaId AS bigint) IS NULL OR c.id=:cuentaId)
                    """, p.addValue("fechaDesdeLocal", filtro.fechaDesde())
                            .addValue("fechaHastaLocal", filtro.fechaHasta().plusDays(1)),
                    (rs, n) -> new Saldo(rs.getBigDecimal("apertura"), rs.getBigDecimal("cierre")));
            apertura = saldo.apertura(); cierre = saldo.cierre();
        }
        return new ResumenTesoreria(flujo.movimientos(), flujo.ingresos(), flujo.egresos(),
                flujo.ingresos().subtract(flujo.egresos()), flujo.traspasosEntrada(),
                flujo.traspasosSalida(), apertura, cierre, saldos, moneda);
    }

    private MapSqlParameterSource parametrosEstado(FiltroEstadoCuentaAlumno f,
                                                    AlcanceReporteFinanciero a,
                                                    Instant corteExclusivo) {
        return new MapSqlParameterSource().addValue("institucionId", f.institucionId())
                .addValue("alumnoId", f.alumnoId()).addValue("plantelId", f.plantelId())
                .addValue("fechaCorte", f.fechaCorte()).addValue("corteExclusivo", Timestamp.from(corteExclusivo))
                .addValue("situacion", f.situacion()).addValue("institucional", a.institucional())
                .addValue("plantelIds", a.plantelIds());
    }

    private MapSqlParameterSource parametrosTesoreria(FiltroReporteTesoreria f,
                                                       AlcanceReporteFinanciero a,
                                                       Instant desde, Instant hasta) {
        return new MapSqlParameterSource().addValue("institucionId", f.institucionId())
                .addValue("cuentaId", f.cuentaId()).addValue("plantelId", f.plantelId())
                .addValue("fechaDesde", Timestamp.from(desde)).addValue("fechaHasta", Timestamp.from(hasta))
                .addValue("institucional", a.institucional()).addValue("plantelIds", a.plantelIds());
    }

    private record Flujo(long movimientos, BigDecimal ingresos, BigDecimal egresos,
                         BigDecimal traspasosEntrada, BigDecimal traspasosSalida) { }
    private record Saldo(BigDecimal apertura, BigDecimal cierre) { }
}
