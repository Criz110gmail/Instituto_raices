package escuela.personal.service;

import escuela.admin.dto.OpcionAutocompletado;
import escuela.common.exception.ReglaNegocioException;
import escuela.personal.dto.FiltroReciboNomina;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

/** Comprobantes derivados de hechos históricos. Ningún método crea pagos ni recibos fiscales. */
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ReciboNominaService {
    private final EmpleadoService empleados;
    private final PreparacionNominaService preparacion;
    private final NamedParameterJdbcTemplate jdbc;
    private final AccesoRecibosEmpleadoService accesos;

    public record Fila(String tipo,Long documentoId,Long filaId,Long nominaId,Long empleadoId,Long periodoId,
            String codigo,String nombre,String periodo,String estado,LocalDate inicio,LocalDate fin,
            BigDecimal neto,String moneda,String fecha) {
        public String folio(){return "RN-"+switch(tipo){case "PAGO"->"P-";case "CERO"->"C-";default->"H-";}+String.format(Locale.ROOT,"%06d",documentoId);}
        public String nomina(){return "BOR-"+String.format(Locale.ROOT,"%06d",nominaId);}
        public String estadoTexto(){return switch(estado){case "PAGADO"->"Pagado";case "CANCELADO"->"Pago cancelado";case "NETO_CERO"->"Neto cero confirmado";default->"Aplicación histórica";};}
        public String ruta(){return "/admin/recibos-nomina/"+tipo+"/"+documentoId;}
    }
    public record Fuente(String cuenta,String medio,BigDecimal importe,String cheque,String estadoCheque){}
    public record Prestamo(String folio,String concepto,BigDecimal cuota,BigDecimal capital,BigDecimal interes){}
    public record Detalle(Fila recibo,Long institucionId,String institucion,BigDecimal sueldo,BigDecimal percepciones,
            BigDecimal deducciones,BigDecimal aportaciones,String motivo,String responsable,String cancelacion,
            String puestos,List<PreparacionNominaService.Linea> lineas,List<Fuente> fuentes,List<Prestamo> prestamos){}

    private MapSqlParameterSource p(){var a=empleados.autorizarSalarios(false);return new MapSqlParameterSource("inst",a.institucionId());}
    // Predicado institucional dentro de cada rama: no se materializa historia de otra institución.
    private static final String EVENTOS="""
        WITH eventos AS (
          SELECT 'PAGO'::text tipo,ni.id documento_id,ni.fila_id,ni.institucion_id,
            CASE WHEN ni.estado='REVERTIDO' THEN 'CANCELADO' ELSE 'PAGADO' END estado,
            ni.monto neto,ni.moneda,ni.ocurrido_en registrado_en,ni.motivo,ni.usuario_id
          FROM nomina_pago_intento ni WHERE ni.institucion_id=:inst AND ni.estado IN ('PAGADO','REVERTIDO')
          UNION ALL
          SELECT 'CERO',nc.fila_id,nc.fila_id,nc.institucion_id,'NETO_CERO',0,ne.moneda,nc.ocurrido_en,nc.motivo,nc.usuario_id
          FROM nomina_cierre_cero nc JOIN nomina_borrador_empleado ne ON ne.id=nc.fila_id AND ne.institucion_id=nc.institucion_id
          WHERE nc.institucion_id=:inst
          UNION ALL
          SELECT 'HISTORICO',ne.id,ne.id,a.institucion_id,'HISTORICO',ne.propuesto,ne.moneda,a.aplicado_en,a.motivo,a.aplicado_por_id
          FROM nomina_aplicacion a JOIN nomina_borrador_empleado ne ON ne.borrador_id=a.borrador_id AND ne.revision=a.revision AND ne.institucion_id=a.institucion_id
          WHERE a.institucion_id=:inst
        )
        """;
    private static final String DESDE="""
         FROM eventos ev JOIN nomina_fila_efectiva ne ON ne.id=ev.fila_id AND ne.institucion_id=ev.institucion_id
         JOIN nomina_borrador_revision nr ON nr.borrador_id=ne.borrador_id AND nr.numero=ne.revision AND nr.institucion_id=ne.institucion_id
         JOIN institucion i ON i.id=ev.institucion_id
        """;
    private static final String COLUMNAS="""
        SELECT ev.tipo,ev.documento_id,ne.id fila_id,ne.borrador_id,ne.empleado_id,ne.periodo_id,
        ne.empleado_codigo,ne.empleado_nombre,nr.periodo_codigo,ev.estado,ne.fecha_inicio,ne.fecha_fin,
        ev.neto,ev.moneda,to_char(ev.registrado_en AT TIME ZONE i.zona_horaria,'DD/MM/YYYY HH24:MI') fecha
        """;
    private static final String FILTROS="""
         WHERE (:estado='TODOS' OR ev.estado=:estado)
         AND (CAST(:emp AS bigint) IS NULL OR ne.empleado_id=:emp)
         AND (CAST(:periodo AS bigint) IS NULL OR ne.periodo_id=:periodo)
         AND (CAST(:desde AS date) IS NULL OR ne.fecha_fin>=:desde)
         AND (CAST(:hasta AS date) IS NULL OR ne.fecha_inicio<=:hasta)
         AND strpos(lower(ne.empleado_codigo||' '||ne.empleado_nombre||' '||nr.periodo_codigo||' BOR-'||lpad(ne.borrador_id::text,6,'0')||' RN-'||CASE ev.tipo WHEN 'PAGO' THEN 'P-' WHEN 'CERO' THEN 'C-' ELSE 'H-' END||lpad(ev.documento_id::text,6,'0')),lower(:q))>0
        """;
    private static final RowMapper<Fila> MAP=(r,n)->new Fila(r.getString("tipo"),r.getLong("documento_id"),r.getLong("fila_id"),r.getLong("borrador_id"),r.getLong("empleado_id"),r.getLong("periodo_id"),r.getString("empleado_codigo"),r.getString("empleado_nombre"),r.getString("periodo_codigo"),r.getString("estado"),r.getObject("fecha_inicio",LocalDate.class),r.getObject("fecha_fin",LocalDate.class),r.getBigDecimal("neto"),r.getString("moneda"),r.getString("fecha"));
    private MapSqlParameterSource filtros(FiltroReciboNomina f){
        var par=p();f.validar();
        if(f.empleadoId()!=null&&jdbc.queryForObject("SELECT count(*) FROM nomina_borrador_empleado WHERE institucion_id=:inst AND empleado_id=:emp",par.addValue("emp",f.empleadoId()),Long.class)==0)throw new AccessDeniedException("Empleado histórico no autorizado");
        if(f.periodoId()!=null&&jdbc.queryForObject("SELECT count(*) FROM nomina_borrador_revision WHERE institucion_id=:inst AND periodo_id=:periodo",par.addValue("periodo",f.periodoId()),Long.class)==0)throw new AccessDeniedException("Periodo histórico no autorizado");
        return par.addValue("q",f.q()).addValue("estado",f.estado()).addValue("emp",f.empleadoId(),java.sql.Types.BIGINT)
                .addValue("periodo",f.periodoId(),java.sql.Types.BIGINT).addValue("desde",f.desde(),java.sql.Types.DATE).addValue("hasta",f.hasta(),java.sql.Types.DATE);
    }
    public Page<Fila> listar(FiltroReciboNomina original){
        var f=original.normalizado();var par=filtros(f);var pg=PageRequest.of(f.pagina(),f.tamanio());
        long n=jdbc.queryForObject(EVENTOS+"SELECT count(*)"+DESDE+FILTROS,par,Long.class);
        return new PageImpl<>(jdbc.query(EVENTOS+COLUMNAS+DESDE+FILTROS+" ORDER BY ev.registrado_en DESC,ev.tipo,ev.documento_id DESC LIMIT :limite OFFSET :offset",par.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset()),MAP),pg,n);
    }
    public Page<Fila> listarPropios(FiltroReciboNomina original){
        var identidad=accesos.identidadActual();var f=original.normalizado();f.validar();
        if(f.empleadoId()!=null&&!f.empleadoId().equals(identidad.empleadoId())||f.periodoId()!=null)throw new AccessDeniedException("Filtro personal no autorizado");
        var par=new MapSqlParameterSource("inst",identidad.institucionId()).addValue("estado",f.estado()).addValue("emp",identidad.empleadoId()).addValue("periodo",null,java.sql.Types.BIGINT).addValue("desde",f.desde(),java.sql.Types.DATE).addValue("hasta",f.hasta(),java.sql.Types.DATE).addValue("q",f.q());
        var pg=PageRequest.of(f.pagina(),f.tamanio());long n=jdbc.queryForObject(EVENTOS+"SELECT count(*)"+DESDE+FILTROS,par,Long.class);
        return new PageImpl<>(jdbc.query(EVENTOS+COLUMNAS+DESDE+FILTROS+" ORDER BY ev.registrado_en DESC,ev.tipo,ev.documento_id DESC LIMIT :limite OFFSET :offset",par.addValue("limite",pg.getPageSize()).addValue("offset",pg.getOffset()),MAP),pg,n);
    }
    @Transactional(readOnly=true,isolation=Isolation.REPEATABLE_READ)
    public Detalle obtener(String tipo,Long documento){
        return detalle(tipo,documento,p());
    }
    @Transactional(readOnly=true,isolation=Isolation.REPEATABLE_READ)
    public Detalle obtenerPropio(String tipo,Long documento){
        var i=accesos.identidadActual();return detalle(tipo,documento,new MapSqlParameterSource("inst",i.institucionId()).addValue("propietario",i.empleadoId()));
    }
    private Detalle detalle(String tipo,Long documento,MapSqlParameterSource par){
        par.addValue("tipo",tipo).addValue("doc",documento);
        if(!Set.of("PAGO","CERO","HISTORICO").contains(tipo)||documento==null||documento<=0)throw new AccessDeniedException("Recibo no autorizado");
        var fila=jdbc.query(EVENTOS+COLUMNAS+DESDE+" WHERE ev.tipo=:tipo AND ev.documento_id=:doc"+(par.hasValue("propietario")?" AND ne.empleado_id=:propietario":""),par,MAP).stream().findFirst().orElseThrow(()->new AccessDeniedException("Recibo no autorizado"));
        var datos=jdbc.queryForMap(EVENTOS+"SELECT i.nombre institucion,ne.sueldo,ne.percepciones,ne.deducciones,ne.aportaciones,ev.motivo,u.username "+DESDE+" JOIN usuario u ON u.id=ev.usuario_id WHERE ev.tipo=:tipo AND ev.documento_id=:doc",par);
        BigDecimal sueldo=(BigDecimal)datos.get("sueldo"),per=(BigDecimal)datos.get("percepciones"),ded=(BigDecimal)datos.get("deducciones");
        if(sueldo.add(per).subtract(ded).compareTo(fila.neto())!=0)throw new ReglaNegocioException("El importe histórico no coincide con su desglose. Conserva el registro y solicita revisión; no se generó un recibo incorrecto.");
        par.addValue("fila",fila.filaId()).addValue("nomina",fila.nominaId());
        String puestos=jdbc.queryForObject("SELECT coalesce(string_agg(DISTINCT (x->>'puesto_codigo')||' · '||(x->>'puesto_nombre'),'; '),'Sin puesto registrado en esta fotografía') FROM nomina_borrador_empleado ne CROSS JOIN LATERAL jsonb_array_elements(coalesce(ne.fuentes->'sueldos','[]'::jsonb)) x WHERE ne.id=:fila AND ne.institucion_id=:inst",par,String.class);
        String cancelacion="";
        if(fila.estado().equals("CANCELADO"))cancelacion=jdbc.queryForObject("SELECT to_char(r.ocurrido_en AT TIME ZONE i.zona_horaria,'DD/MM/YYYY HH24:MI')||' · '||r.referencia||' · '||r.motivo FROM nomina_pago_reversa r JOIN institucion i ON i.id=r.institucion_id WHERE r.pago_id=:doc AND r.institucion_id=:inst",par,String.class);
        var fuentes=fuentes(fila,par);
        var prestamos=jdbc.query("""
            SELECT 'PRE-'||lpad(x->>'id',6,'0') folio,(x->>'concepto_codigo')||' · '||(x->>'concepto_nombre') concepto,(x->>'cuota')::numeric cuota,q.capital,q.interes
            FROM nomina_borrador_empleado ne CROSS JOIN LATERAL jsonb_array_elements(coalesce(ne.fuentes->'prestamos','[]'::jsonb)) x
            LEFT JOIN LATERAL (SELECT sum((c->>'capital')::numeric) capital,sum((c->>'interes')::numeric) interes FROM jsonb_array_elements(coalesce(x->'cuotas_programadas','[]'::jsonb)) c)q ON true
            WHERE ne.id=:fila AND ne.institucion_id=:inst ORDER BY (x->>'id')::bigint
            """,par,(r,n)->new Prestamo(r.getString("folio"),r.getString("concepto"),r.getBigDecimal("cuota"),r.getBigDecimal("capital"),r.getBigDecimal("interes")));
        var lineas=jdbc.query("""
            SELECT l.codigo,l.nombre||CASE WHEN ne.tratamiento='NO_APLICAR' THEN ' · No aplicar' WHEN l.accion='POSPONER' THEN ' · Pospuesto' WHEN l.accion='NO_APLICAR' THEN ' · No aplicar' ELSE '' END nombre,l.naturaleza,CASE WHEN ne.tratamiento='NO_APLICAR' OR l.accion<>'APLICAR' THEN 0 ELSE l.importe END importe,l.fuente->>'fecha_inicio' inicio,l.fuente->>'fecha_fin' fin
            FROM nomina_linea_tratamiento l JOIN nomina_fila_efectiva ne ON ne.id=l.fila_id AND ne.institucion_id=l.institucion_id WHERE l.fila_id=:fila AND l.borrador_id=:nomina AND l.institucion_id=:inst
            UNION ALL SELECT x->>'concepto_codigo',x->>'concepto_nombre','DEDUCCION',(x->>'cuota')::numeric,x->>'primer_descuento',NULL FROM nomina_borrador_empleado ne CROSS JOIN LATERAL jsonb_array_elements(ne.fuentes->'prestamos') x WHERE ne.id=:fila AND ne.borrador_id=:nomina AND ne.institucion_id=:inst
            """,par,(r,n)->new PreparacionNominaService.Linea(r.getString("codigo"),r.getString("nombre"),r.getString("naturaleza"),r.getBigDecimal("importe"),r.getString("inicio"),r.getString("fin")));
        return new Detalle(fila,(Long)par.getValue("inst"),(String)datos.get("institucion"),sueldo,per,ded,(BigDecimal)datos.get("aportaciones"),(String)datos.get("motivo"),(String)datos.get("username"),cancelacion,puestos,lineas,fuentes,prestamos);
    }
    private List<Fuente> fuentes(Fila r,MapSqlParameterSource par){
        if(r.tipo().equals("CERO"))return List.of();
        if(r.tipo().equals("HISTORICO"))return jdbc.query("SELECT cuenta_codigo||' · '||cuenta_nombre cuenta FROM nomina_aplicacion WHERE borrador_id=:nomina AND institucion_id=:inst",par,(s,n)->new Fuente(s.getString("cuenta"),"Aplicación consolidada",r.neto(),null,null));
        var fuentes=jdbc.query("""
            SELECT f.cuenta_texto,f.medio,f.importe,ch.folio cheque,CASE WHEN cc.cheque_id IS NOT NULL THEN 'Cancelado · No reutilizable' WHEN ch.id IS NOT NULL THEN 'Emitido · no acredita cobro bancario' END estado_cheque
            FROM nomina_pago_fuente f JOIN nomina_pago_intento ni ON ni.id=f.pago_id AND ni.institucion_id=:inst
            LEFT JOIN cheque_nomina ch ON ch.pago_id=f.pago_id AND ch.ordinal=f.ordinal
            LEFT JOIN cheque_nomina_cancelacion cc ON cc.cheque_id=ch.id
            WHERE f.pago_id=:doc ORDER BY f.ordinal
            """,par,(s,n)->new Fuente(s.getString("cuenta_texto"),s.getString("medio"),s.getBigDecimal("importe"),s.getString("cheque"),s.getString("estado_cheque")));
        if(!fuentes.isEmpty())return fuentes;
        return jdbc.query("SELECT c.codigo||' · '||c.nombre cuenta,c.tipo,m.monto FROM nomina_pago_intento ni JOIN movimiento_financiero m ON m.id=ni.movimiento_id AND m.institucion_id=ni.institucion_id JOIN cuenta_financiera c ON c.id=m.cuenta_id WHERE ni.id=:doc AND ni.institucion_id=:inst",par,(s,n)->new Fuente(s.getString("cuenta"),s.getString("tipo").equals("CAJA")?"Efectivo":"Transferencia",s.getBigDecimal("monto"),null,null));
    }
    public List<OpcionAutocompletado> buscar(String q,boolean periodo){
        var par=p();String texto=q==null?"":q.strip();if(!texto.isEmpty()&&texto.length()<3)return List.of();par.addValue("q",texto.substring(0,Math.min(200,texto.length())));
        if(periodo)return jdbc.query("SELECT DISTINCT periodo_id,periodo_codigo,periodo_nombre FROM nomina_borrador_revision WHERE institucion_id=:inst AND strpos(lower(periodo_codigo||' '||periodo_nombre),lower(:q))>0 ORDER BY periodo_codigo,periodo_id LIMIT 11",par,(r,n)->new OpcionAutocompletado(r.getLong(1),r.getString(2)+" · "+r.getString(3),"Periodo histórico"));
        return jdbc.query("SELECT DISTINCT ON (empleado_id) empleado_id,empleado_codigo,empleado_nombre FROM nomina_borrador_empleado WHERE institucion_id=:inst AND strpos(lower(empleado_codigo||' '||empleado_nombre),lower(:q))>0 ORDER BY empleado_id,id DESC LIMIT 11",par,(r,n)->new OpcionAutocompletado(r.getLong(1),r.getString(2)+" · "+r.getString(3),"Empleado histórico"));
    }
}
