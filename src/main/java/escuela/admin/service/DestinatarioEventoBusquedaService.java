package escuela.admin.service;

import escuela.admin.dto.*;
import escuela.comunicacion.entity.TipoDestinatarioEvento;
import escuela.seguridad.service.AlcanceDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class DestinatarioEventoBusquedaService {
    private static final int LIMITE = 21;
    private final NamedParameterJdbcTemplate jdbc;
    private final AlcanceDatosService alcance;

    public ResultadoAutocompletado buscar(TipoDestinatarioEvento tipo, Long institucionId,
                                          Long cicloId, Long plantelId, String consulta) {
        validarAlcance(institucionId, plantelId);
        String texto = normalizar(consulta);
        if (texto == null || cicloId == null) return ResultadoAutocompletado.vacio();
        MapSqlParameterSource p = new MapSqlParameterSource().addValue("institucionId", institucionId)
                .addValue("cicloId", cicloId).addValue("plantelId", plantelId)
                .addValue("texto", texto).addValue("limite", LIMITE);
        String sql = switch (tipo) {
            case NIVEL -> """
                    SELECT n.id, n.codigo || ' · ' || n.nombre titulo, 'Nivel educativo' detalle
                    FROM nivel_educativo n WHERE n.institucion_id=:institucionId AND n.activo=true
                      AND lower(n.codigo || ' ' || n.nombre) LIKE ('%' || :texto || '%')
                      AND (CAST(:plantelId AS bigint) IS NULL OR EXISTS (SELECT 1 FROM plantel_nivel pn
                           WHERE pn.plantel_id=:plantelId AND pn.nivel_educativo_id=n.id AND pn.activo=true))
                    ORDER BY n.orden,n.id LIMIT :limite
                    """;
            case GRADO -> """
                    SELECT g.id, g.codigo || ' · ' || g.nombre titulo, n.nombre detalle
                    FROM grado g JOIN nivel_educativo n ON n.id=g.nivel_educativo_id
                    WHERE n.institucion_id=:institucionId AND n.activo=true AND g.activo=true
                      AND lower(g.codigo || ' ' || g.nombre) LIKE ('%' || :texto || '%')
                      AND (CAST(:plantelId AS bigint) IS NULL OR EXISTS (SELECT 1 FROM plantel_nivel pn
                           WHERE pn.plantel_id=:plantelId AND pn.nivel_educativo_id=n.id AND pn.activo=true))
                    ORDER BY n.orden,g.orden,g.id LIMIT :limite
                    """;
            case GRUPO -> """
                    SELECT g.id, coalesce(g.codigo || ' · ','') || g.nombre || ' · ' || g.turno titulo,
                           p.nombre detalle
                    FROM grupo g JOIN plantel p ON p.id=g.plantel_id
                    WHERE p.institucion_id=:institucionId AND g.ciclo_escolar_id=:cicloId AND g.activo=true
                      AND lower(coalesce(g.codigo,'') || ' ' || g.nombre || ' ' || g.turno) LIKE ('%' || :texto || '%')
                      AND (CAST(:plantelId AS bigint) IS NULL OR g.plantel_id=:plantelId)
                    ORDER BY p.nombre,g.nombre,g.id LIMIT :limite
                    """;
            case ALUMNO -> """
                    SELECT DISTINCT a.id, a.matricula || ' · ' || a.nombres || ' ' || a.primer_apellido
                           || coalesce(' ' || a.segundo_apellido,'') titulo, 'Alumno' detalle
                    FROM alumno a JOIN inscripcion i ON i.alumno_id=a.id
                    WHERE a.institucion_id=:institucionId AND a.activo=true
                      AND i.ciclo_escolar_id=:cicloId AND i.estado IN ('PREINSCRITA','ACTIVA')
                      AND a.busqueda_autocomplete LIKE ('%' || :texto || '%')
                      AND (CAST(:plantelId AS bigint) IS NULL OR i.plantel_id=:plantelId)
                    ORDER BY titulo,a.id LIMIT :limite
                    """;
        };
        var filas = jdbc.query(sql, p, (rs, n) -> new OpcionAutocompletado(rs.getLong("id"),
                rs.getString("titulo"), rs.getString("detalle")));
        boolean mas = filas.size() == LIMITE;
        return new ResultadoAutocompletado(filas.stream().limit(20).toList(), mas);
    }

    public List<SeleccionDestinatarioEvento> resolver(List<String> claves) {
        if (claves == null) return List.of();
        return claves.stream().distinct().map(this::resolver).filter(Objects::nonNull).toList();
    }

    private SeleccionDestinatarioEvento resolver(String clave) {
        try {
            var d = EventoEscolarForm.destino(clave);
            String tabla = switch (d.tipo()) { case NIVEL -> "nivel_educativo"; case GRADO -> "grado";
                case GRUPO -> "grupo"; case ALUMNO -> "alumno"; };
            String titulo = switch (d.tipo()) {
                case NIVEL, GRADO -> "codigo || ' · ' || nombre";
                case GRUPO -> "coalesce(codigo || ' · ','') || nombre";
                case ALUMNO -> "matricula || ' · ' || nombres || ' ' || primer_apellido || coalesce(' ' || segundo_apellido,'')";
            };
            List<String> encontrado = jdbc.query("SELECT " + titulo + " titulo FROM " + tabla + " WHERE id=:id",
                    Map.of("id", d.id()), (rs, n) -> rs.getString("titulo"));
            return encontrado.isEmpty() ? null : new SeleccionDestinatarioEvento(clave, encontrado.getFirst(), d.tipo().name());
        } catch (IllegalArgumentException ex) { return null; }
    }

    private void validarAlcance(Long institucionId, Long plantelId) {
        alcance.validarInstitucion(institucionId);
        if (plantelId != null) alcance.validarPlantel(plantelId);
        else if (!alcance.alcanceInstitucionalActual(institucionId))
            throw new org.springframework.security.access.AccessDeniedException("Selecciona un plantel autorizado");
    }
    private String normalizar(String valor) {
        if (valor == null) return null; String t = valor.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
        return t.length() < 3 ? null : t;
    }
}
