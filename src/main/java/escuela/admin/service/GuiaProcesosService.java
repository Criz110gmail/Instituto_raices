package escuela.admin.service;

import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuiaProcesosService {
    private final NamedParameterJdbcTemplate jdbc;
    private static final String ACCESO = """
        FROM guia_proceso g WHERE NOT EXISTS (
          SELECT 1 FROM guia_proceso_permiso gp JOIN permiso p ON p.id=gp.permiso_id
          WHERE gp.guia_id=g.id AND p.codigo NOT IN (:permisos))
        """;
    private static final String FILTROS = """
        AND (:categoria='' OR g.categoria=:categoria)
        AND (:estado='TODOS' OR g.estado=:estado)
        AND (strpos(lower(g.titulo || ' ' || g.resumen || ' ' || g.ejemplo),lower(:q))>0
          OR EXISTS(SELECT 1 FROM guia_proceso_paso s WHERE s.guia_id=g.id
            AND strpos(lower(s.titulo || ' ' || s.instrucciones || ' ' || s.precaucion),lower(:q))>0))
        """;

    public record Guia(Long id, String slug, String titulo, String categoria, String resumen,
                       String requisitos, String ejemplo, int versionContenido, LocalDate revisadaEl, String estado) {}
    public record Paso(int numero, String titulo, String perfil, String modulo, String instrucciones,
                       String ejemplo, String resultado, String precaucion, String ruta) {
        public List<String> acciones() {
            return Arrays.stream(instrucciones.split("(?=(?<!\\S)\\d+\\. )"))
                    .map(String::strip).filter(s->!s.isEmpty())
                    .map(s->s.replaceFirst("^\\d+\\.\\s*", "")).toList();
        }
    }
    public record Detalle(Guia guia, List<Paso> pasos) {}
    public record Filtro(String q, String categoria, String estado, Integer pagina, Integer tamanio) {
        public Filtro normalizado() {
            String texto = q == null ? "" : q.strip();
            if (texto.length()>200) texto=texto.substring(0,200);
            return new Filtro(texto, categoria == null ? "" : categoria.strip(),
                    Set.of("CONFIRMADA","PENDIENTE").contains(estado == null ? "" : estado) ? estado : "TODOS",
                    Math.max(0,pagina == null ? 0 : pagina),
                    Set.of(10,25,50,100).contains(tamanio == null ? 0 : tamanio) ? tamanio : 25);
        }
    }

    public Page<Guia> listar(UsuarioPrincipal principal, Filtro original) {
        var f=original.normalizado(); var p=parametros(principal).addValue("q",f.q())
                .addValue("categoria",f.categoria()).addValue("estado",f.estado())
                .addValue("limite",f.tamanio()).addValue("offset",(long)f.pagina()*f.tamanio());
        Long total=jdbc.queryForObject("SELECT count(*) "+ACCESO+FILTROS,p,Long.class);
        var filas=jdbc.query("SELECT g.* "+ACCESO+FILTROS+" ORDER BY g.id LIMIT :limite OFFSET :offset",p,
                (rs,n)->new Guia(rs.getLong("id"),rs.getString("slug"),rs.getString("titulo"),
                        rs.getString("categoria"),rs.getString("resumen"),rs.getString("requisitos"),
                        rs.getString("ejemplo"),rs.getInt("version_contenido"),rs.getObject("revisada_el",LocalDate.class),rs.getString("estado")));
        return new PageImpl<>(filas,PageRequest.of(f.pagina(),f.tamanio()),total == null ? 0 : total);
    }

    public Detalle obtener(UsuarioPrincipal principal,String slug) {
        var p=parametros(principal).addValue("slug",slug);
        var guias=jdbc.query("SELECT g.* "+ACCESO+" AND g.slug=:slug",p,
                (rs,n)->new Guia(rs.getLong("id"),rs.getString("slug"),rs.getString("titulo"),
                        rs.getString("categoria"),rs.getString("resumen"),rs.getString("requisitos"),
                        rs.getString("ejemplo"),rs.getInt("version_contenido"),rs.getObject("revisada_el",LocalDate.class),rs.getString("estado")));
        if(guias.isEmpty()) throw new AccessDeniedException("La guía no está disponible para tus permisos");
        var guia=guias.getFirst();
        var pasos=jdbc.query("SELECT * FROM guia_proceso_paso WHERE guia_id=:id ORDER BY numero LIMIT 100",
                new MapSqlParameterSource("id",guia.id()),(rs,n)->new Paso(rs.getInt("numero"),rs.getString("titulo"),
                        rs.getString("perfil"),rs.getString("modulo"),rs.getString("instrucciones"),rs.getString("ejemplo"),
                        rs.getString("resultado"),rs.getString("precaucion"),rs.getString("ruta")));
        return new Detalle(guia,pasos);
    }

    private MapSqlParameterSource parametros(UsuarioPrincipal principal) {
        if(principal==null || principal.usuarioId()==null || principal.institucionId()==null
                || principal.accesoRecuperacion()) throw new AccessDeniedException("Usa una cuenta administrativa identificable");
        var permisos=principal.authorities().stream().map(a->a.getAuthority()).toList();
        if(!permisos.contains("GUIA_PROCESOS_CONSULTAR")) throw new AccessDeniedException("Sin permiso para consultar guías");
        return new MapSqlParameterSource("permisos",permisos);
    }
}
