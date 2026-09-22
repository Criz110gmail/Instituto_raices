package escuela.portal.repository;

import escuela.portal.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PortalTutorRepository {
    private static final String EVENTOS_DESDE = """
            FROM evento_escolar e
            JOIN institucion inst ON inst.id = e.institucion_id
            LEFT JOIN plantel p ON p.id = e.plantel_id
            WHERE e.institucion_id = :institucionId
              AND e.estado = 'PUBLICADO'
              AND e.fin_en >= :desde
              AND EXISTS (
                  SELECT 1
                  FROM inscripcion i
                  JOIN grado gr ON gr.id = i.grado_id
                  WHERE i.alumno_id = :alumnoId
                    AND i.ciclo_escolar_id = e.ciclo_escolar_id
                    AND i.estado <> 'CANCELADA'
                    AND (e.plantel_id IS NULL OR i.plantel_id = e.plantel_id)
                    AND (
                        e.alcance IN ('INSTITUCION', 'PLANTEL')
                        OR EXISTS (
                            SELECT 1 FROM destinatario_evento de
                            WHERE de.evento_id = e.id
                              AND (de.alumno_id = :alumnoId
                                OR de.grado_id = i.grado_id
                                OR de.nivel_educativo_id = gr.nivel_educativo_id
                                OR (de.grupo_id IS NOT NULL AND EXISTS (
                                    SELECT 1 FROM asignacion_grupo ag
                                    WHERE ag.inscripcion_id = i.id AND ag.grupo_id = de.grupo_id
                                      AND ag.fecha_inicio <= timezone(inst.zona_horaria, e.inicio_en)::date
                                      AND (ag.fecha_fin IS NULL OR ag.fecha_fin >= timezone(inst.zona_horaria, e.inicio_en)::date)
                                )))
                        )
                    )
              )
            """;

    private final NamedParameterJdbcTemplate jdbc;

    public String nombreTutor(Long usuarioId, Long institucionId) {
        return jdbc.query("""
                SELECT concat_ws(' ', t.nombres, t.primer_apellido, t.segundo_apellido)
                FROM tutor t
                WHERE t.usuario_id=:usuarioId AND t.institucion_id=:institucionId AND t.activo=true
                """, new MapSqlParameterSource().addValue("usuarioId", usuarioId)
                        .addValue("institucionId", institucionId),
                (rs, n) -> rs.getString(1)).stream().findFirst().orElse(null);
    }

    public List<PortalPlantelPago> plantelesPago(Long usuarioId, Long institucionId, LocalDate hoy) {
        return jdbc.query("""
                SELECT DISTINCT p.id,p.nombre FROM plantel p JOIN inscripcion i ON i.plantel_id=p.id
                JOIN alumno_tutor v ON v.alumno_id=i.alumno_id JOIN tutor t ON t.id=v.tutor_id
                JOIN cargo c ON c.inscripcion_id=i.id
                WHERE t.usuario_id=:usuarioId AND p.institucion_id=:institucionId AND p.activo=true
                  AND v.activo=true AND v.es_responsable_financiero=true AND v.puede_ver_finanzas=true
                  AND i.estado IN ('PREINSCRITA','ACTIVA') AND c.estado_registro='EMITIDO'
                ORDER BY p.nombre,p.id
                """, new MapSqlParameterSource().addValue("usuarioId",usuarioId).addValue("institucionId",institucionId),
                (rs,n)->new PortalPlantelPago(rs.getLong("id"),rs.getString("nombre")));
    }

    public Page<PortalPagoFila> pagos(Long usuarioId, Long institucionId, String zona, int pagina, int tamanio) {
        var p = new MapSqlParameterSource().addValue("usuarioId",usuarioId).addValue("institucionId",institucionId)
                .addValue("zona",zona).addValue("limite",tamanio).addValue("offset",(long)pagina*tamanio);
        String desde=" FROM pago pa JOIN tutor t ON t.id=pa.tutor_id WHERE t.usuario_id=:usuarioId AND pa.institucion_id=:institucionId ";
        Long total=jdbc.queryForObject("SELECT count(*)"+desde,p,Long.class);
        List<PortalPagoFila> filas=jdbc.query("""
                SELECT pa.id,pa.folio,timezone(:zona,pa.fecha_pago) fecha_local,pa.monto,pa.moneda,pa.estado,pa.referencia,
                       (SELECT count(*) FROM comprobante_pago cp WHERE cp.pago_id=pa.id) comprobantes
                """+desde+" ORDER BY pa.fecha_pago DESC,pa.id DESC LIMIT :limite OFFSET :offset",p,
                (rs,n)->new PortalPagoFila(rs.getLong("id"),rs.getString("folio"),rs.getObject("fecha_local",LocalDateTime.class),
                        rs.getBigDecimal("monto"),rs.getString("moneda"),rs.getString("estado"),rs.getString("referencia"),rs.getInt("comprobantes")));
        return new PageImpl<>(filas,PageRequest.of(pagina,tamanio),total==null?0:total);
    }

    public List<PortalHijoResumen> hijos(Long usuarioId, Long institucionId, LocalDate hoy) {
        return jdbc.query("""
                SELECT a.id, a.matricula,
                       concat_ws(' ', a.nombres, a.primer_apellido, a.segundo_apellido) AS nombre,
                       CASE at.parentesco WHEN 'MADRE' THEN 'Madre' WHEN 'PADRE' THEN 'Padre'
                            WHEN 'TUTOR_LEGAL' THEN 'Tutor legal' ELSE at.parentesco_otro END AS parentesco,
                       at.es_contacto_principal, at.es_responsable_financiero, at.puede_ver_finanzas,
                       (a.fotografia_archivo_id IS NOT NULL) AS tiene_fotografia,
                       COALESCE(actual.plantel, 'Sin inscripción vigente') AS plantel,
                       COALESCE(actual.ciclo, 'Sin ciclo vigente') AS ciclo,
                       COALESCE(actual.grado, 'Sin grado vigente') AS grado,
                       COALESCE(actual.grupo, 'Sin grupo asignado') AS grupo
                FROM tutor t
                JOIN alumno_tutor at ON at.tutor_id=t.id
                JOIN alumno a ON a.id=at.alumno_id
                LEFT JOIN LATERAL (
                    SELECT p.nombre AS plantel, ce.nombre AS ciclo, g.nombre AS grado,
                           COALESCE(gru.nombre, 'Sin grupo asignado') AS grupo
                    FROM inscripcion i
                    JOIN plantel p ON p.id=i.plantel_id
                    JOIN ciclo_escolar ce ON ce.id=i.ciclo_escolar_id
                    JOIN grado g ON g.id=i.grado_id
                    LEFT JOIN asignacion_grupo ag ON ag.inscripcion_id=i.id
                        AND ag.fecha_inicio <= :hoy AND (ag.fecha_fin IS NULL OR ag.fecha_fin >= :hoy)
                    LEFT JOIN grupo gru ON gru.id=ag.grupo_id
                    WHERE i.alumno_id=a.id AND i.estado IN ('PREINSCRITA','ACTIVA')
                      AND i.fecha_inicio <= :hoy AND (i.fecha_fin IS NULL OR i.fecha_fin >= :hoy)
                    ORDER BY i.fecha_inicio DESC, i.id DESC LIMIT 1
                ) actual ON true
                WHERE t.usuario_id=:usuarioId AND t.institucion_id=:institucionId AND t.activo=true
                  AND a.activo=true AND at.activo=true AND at.fecha_inicio <= :hoy
                  AND (at.fecha_fin IS NULL OR at.fecha_fin >= :hoy)
                ORDER BY a.primer_apellido, a.segundo_apellido NULLS LAST, a.nombres, a.id
                """, new MapSqlParameterSource().addValue("usuarioId", usuarioId)
                        .addValue("institucionId", institucionId).addValue("hoy", hoy),
                (rs, n) -> new PortalHijoResumen(rs.getLong("id"), rs.getString("matricula"),
                        rs.getString("nombre"), rs.getString("parentesco"),
                        rs.getBoolean("es_contacto_principal"), rs.getBoolean("es_responsable_financiero"),
                        rs.getBoolean("puede_ver_finanzas"), rs.getBoolean("tiene_fotografia"),
                        rs.getString("plantel"), rs.getString("ciclo"), rs.getString("grado"),
                        rs.getString("grupo")));
    }

    public Page<PortalEventoFila> eventos(Long alumnoId, Long institucionId, String zona,
                                          Instant desde, int pagina, int tamanio) {
        MapSqlParameterSource p = new MapSqlParameterSource().addValue("alumnoId", alumnoId)
                .addValue("institucionId", institucionId).addValue("desde", Timestamp.from(desde))
                .addValue("zona", zona).addValue("limite", tamanio)
                .addValue("offset", (long) pagina * tamanio);
        Long total = jdbc.queryForObject("SELECT count(*) " + EVENTOS_DESDE, p, Long.class);
        List<PortalEventoFila> filas = jdbc.query("""
                SELECT e.id, e.titulo, e.tipo, e.ubicacion, e.descripcion,
                       timezone(:zona, e.inicio_en) AS inicio_local,
                       timezone(:zona, e.fin_en) AS fin_local,
                       COALESCE(p.nombre, 'Toda la institución') AS plantel, e.alcance
                """ + EVENTOS_DESDE + """
                ORDER BY CASE WHEN e.inicio_en >= CURRENT_TIMESTAMP THEN 0 ELSE 1 END,
                         CASE WHEN e.inicio_en >= CURRENT_TIMESTAMP THEN e.inicio_en END ASC,
                         CASE WHEN e.inicio_en < CURRENT_TIMESTAMP THEN e.inicio_en END DESC,
                         e.id DESC
                LIMIT :limite OFFSET :offset
                """, p, (rs, n) -> new PortalEventoFila(rs.getLong("id"), rs.getString("titulo"),
                rs.getString("tipo"), rs.getString("ubicacion"), rs.getString("descripcion"),
                rs.getObject("inicio_local", LocalDateTime.class),
                rs.getObject("fin_local", LocalDateTime.class), rs.getString("plantel"),
                rs.getString("alcance")));
        return new PageImpl<>(filas, PageRequest.of(pagina, tamanio), total == null ? 0 : total);
    }

    public Page<PortalAvisoFila> avisos(Long alumnoId, Long institucionId, String zona,
                                        LocalDate hoy, Instant ahora, int pagina, int tamanio) {
        String desde = """
                FROM aviso a
                LEFT JOIN plantel p ON p.id=a.plantel_id
                WHERE a.institucion_id=:institucionId AND a.estado='PUBLICADO'
                  AND a.publicado_en <= :ahora AND (a.expira_en IS NULL OR a.expira_en > :ahora)
                  AND (a.plantel_id IS NULL OR EXISTS (
                      SELECT 1 FROM inscripcion i WHERE i.alumno_id=:alumnoId
                        AND i.plantel_id=a.plantel_id AND i.estado IN ('PREINSCRITA','ACTIVA')
                        AND i.fecha_inicio <= :hoy AND (i.fecha_fin IS NULL OR i.fecha_fin >= :hoy)))
                """;
        var p = new MapSqlParameterSource().addValue("alumnoId", alumnoId)
                .addValue("institucionId", institucionId).addValue("zona", zona)
                .addValue("hoy", hoy).addValue("ahora", Timestamp.from(ahora))
                .addValue("limite", tamanio).addValue("offset", (long) pagina*tamanio);
        Long total=jdbc.queryForObject("SELECT count(*) "+desde,p,Long.class);
        var filas=jdbc.query("""
                SELECT a.id,a.titulo,a.contenido,COALESCE(p.nombre,'Toda la institución') alcance,
                       timezone(:zona,a.publicado_en) publicado_local,
                       timezone(:zona,a.expira_en) expira_local
                """+desde+" ORDER BY a.publicado_en DESC,a.id DESC LIMIT :limite OFFSET :offset",p,
                (rs,n)->new PortalAvisoFila(rs.getLong("id"),rs.getString("titulo"),rs.getString("contenido"),
                        rs.getString("alcance"),rs.getObject("publicado_local",LocalDateTime.class),
                        rs.getObject("expira_local",LocalDateTime.class)));
        return new PageImpl<>(filas,PageRequest.of(pagina,tamanio),total==null?0:total);
    }
}
