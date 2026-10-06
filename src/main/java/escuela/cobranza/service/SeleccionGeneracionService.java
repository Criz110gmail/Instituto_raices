package escuela.cobranza.service;

import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

/** Guarda claves por bloques, no expedientes ni toda la selección en memoria o en sesión. */
@Service @RequiredArgsConstructor
public class SeleccionGeneracionService {
    private final NamedParameterJdbcTemplate jdbc;
    public record Item(String clave,Long version,BigDecimal importe,LocalDate vencimiento) { }
    public record Vista(UUID id,boolean nueva) { }

    public Vista preparar(UUID id,String tipo,Long institucion,Long plantel,LocalDate corte) {
        if(id!=null){validar(id,tipo,institucion,plantel,corte,false);return new Vista(id,false);}
        var actor=actor();var token=UUID.randomUUID();
        var params=parametros(token,tipo,institucion,plantel,corte);params.put("usuario",actor.usuarioId());
        jdbc.update("INSERT INTO generacion_seleccion(id,usuario_id,tipo,institucion_id,plantel_id,fecha_corte,expira_en) VALUES(:id,:usuario,:tipo,:institucion,:plantel,:corte,CURRENT_TIMESTAMP+INTERVAL '2 hours')",params);
        return new Vista(token,true);
    }
    public void guardar(UUID id,List<Item> items) {
        if(items.isEmpty())return;
        @SuppressWarnings("unchecked") Map<String,?>[] parametros=items.stream().map(i -> Map.<String,Object>of(
                "id",id,"clave",i.clave(),"version",i.version(),"importe",i.importe(),"vencimiento",i.vencimiento())).toArray(Map[]::new);
        jdbc.batchUpdate("INSERT INTO generacion_seleccion_item(seleccion_id,clave,version_origen,importe_esperado,vencimiento) VALUES(:id,:clave,:version,:importe,:vencimiento)",parametros);
    }
    public void completar(UUID id){jdbc.update("UPDATE generacion_seleccion SET completa=true WHERE id=:id",Map.of("id",id));}
    public Map<String,Item> items(UUID id,List<String> claves) {
        if(claves.isEmpty())return Map.of();
        var lista=jdbc.query("SELECT clave,version_origen,importe_esperado,vencimiento FROM generacion_seleccion_item WHERE seleccion_id=:id AND clave IN (:claves)",Map.of("id",id,"claves",claves),
            (r,n)->new Item(r.getString(1),r.getLong(2),r.getBigDecimal(3),r.getObject(4,LocalDate.class)));
        var resultado=new HashMap<String,Item>();for(var i:lista)resultado.put(i.clave(),i);return resultado;
    }
    public long confirmar(UUID id,String tipo,Long institucion,Long plantel,LocalDate corte,Set<String> excluidas,Set<String> incluidas) {
        if(id==null)throw new ReglaNegocioException("Primero visualiza y revisa la selección antes de confirmar.");
        validar(id,tipo,institucion,plantel,corte,true);
        var claves=incluidas==null?excluidas:incluidas;
        if(claves.size()>5000)throw new ReglaNegocioException("La selección supera 5000 cambios. Reduce el alcance del proceso.");
        if(incluidas!=null && incluidas.isEmpty())throw new ReglaNegocioException("Selecciona al menos un registro antes de confirmar.");
        if(!claves.isEmpty()) {
            var total=jdbc.queryForObject("SELECT count(*) FROM generacion_seleccion_item WHERE seleccion_id=:id AND clave IN (:claves)",Map.of("id",id,"claves",claves),Long.class);
            if(total==null || total!=claves.size())throw new ReglaNegocioException("La selección no corresponde a esta vista previa. Vuelve a visualizar.");
        }
        long cantidad=incluidas==null?cantidad(id)-excluidas.size():incluidas.size();
        if(cantidad<=0)throw new ReglaNegocioException("Selecciona al menos un registro antes de confirmar.");
        return cantidad;
    }
    public long cantidad(UUID id){return Objects.requireNonNull(jdbc.queryForObject("SELECT count(*) FROM generacion_seleccion_item WHERE seleccion_id=:id",Map.of("id",id),Long.class));}
    public void comprobarCantidad(long esperada,long actual){if(esperada!=actual)throw new ReglaNegocioException("La lista cambió después de visualizar. No se generó la selección; vuelve a visualizar y revisa los registros.");}
    public static boolean solicitada(String clave,Set<String> excluidas,Set<String> incluidas){return incluidas==null?!excluidas.contains(clave):incluidas.contains(clave);}
    public void consumida(UUID id){jdbc.update("UPDATE generacion_seleccion SET utilizada=true WHERE id=:id",Map.of("id",id));}
    public void comprobar(Item esperado,Long version,BigDecimal importe,LocalDate vencimiento) {
        if(!Objects.equals(esperado.version(),version) || esperado.importe().compareTo(importe)!=0 || !esperado.vencimiento().equals(vencimiento))
            throw new ReglaNegocioException("Cambió un registro o su importe después de visualizar. No se generó la selección; vuelve a visualizar y revisa los datos.");
    }
    private void validar(UUID id,String tipo,Long institucion,Long plantel,LocalDate corte,boolean bloqueo) {
        var filas=jdbc.queryForList("SELECT usuario_id,tipo,institucion_id,plantel_id,fecha_corte,completa,utilizada,expira_en>CURRENT_TIMESTAMP AS vigente FROM generacion_seleccion WHERE id=:id"+(bloqueo?" FOR UPDATE":""),Map.of("id",id));
        if(filas.isEmpty())throw new ReglaNegocioException("La vista previa no existe. Vuelve a visualizar.");
        var f=filas.getFirst();var p=actor();
        if(!Objects.equals(((Number)f.get("usuario_id")).longValue(),p.usuarioId()) || !tipo.equals(f.get("tipo"))
                || !Objects.equals(((Number)f.get("institucion_id")).longValue(),institucion)
                || !Objects.equals(f.get("plantel_id"),plantel) || !corte.equals(((java.sql.Date)f.get("fecha_corte")).toLocalDate()))
            throw new ReglaNegocioException("La vista previa no corresponde a tu usuario o a los filtros seleccionados.");
        if(!Boolean.TRUE.equals(f.get("completa")) || Boolean.TRUE.equals(f.get("utilizada")) || !Boolean.TRUE.equals(f.get("vigente")))
            throw new ReglaNegocioException("La vista previa caducó o ya fue confirmada. Vuelve a visualizar.");
    }
    private UsuarioPrincipal actor() {
        var a=SecurityContextHolder.getContext().getAuthentication();
        if(a==null || !(a.getPrincipal() instanceof UsuarioPrincipal p) || p.usuarioId()==null || p.accesoRecuperacion())
            throw new ReglaNegocioException("La selección requiere un usuario administrativo identificado, no el acceso de recuperación.");
        return p;
    }
    private Map<String,Object> parametros(UUID id,String tipo,Long institucion,Long plantel,LocalDate corte) {
        var m=new HashMap<String,Object>();m.put("id",id);m.put("tipo",tipo);m.put("institucion",institucion);m.put("plantel",plantel);m.put("corte",corte);return m;
    }
}
