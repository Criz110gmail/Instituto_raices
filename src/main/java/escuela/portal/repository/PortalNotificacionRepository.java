package escuela.portal.repository;

import escuela.portal.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.*;

@Repository @RequiredArgsConstructor
public class PortalNotificacionRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public void sincronizar(Long usuarioId,Long institucionId,LocalDate hoy,Instant desdeEventos,Instant desdePagos,Instant ahora){
        var p=new MapSqlParameterSource().addValue("usuarioId",usuarioId).addValue("institucionId",institucionId)
                .addValue("hoy",hoy).addValue("desdeEventos",Timestamp.from(desdeEventos))
                .addValue("desdePagos",Timestamp.from(desdePagos)).addValue("ahora",Timestamp.from(ahora));
        jdbc.update("""
            INSERT INTO notificacion_usuario(usuario_id,tipo,titulo,mensaje,evento_id,clave_deduplicacion)
            SELECT :usuarioId,'EVENTO',e.titulo,left(concat(e.tipo,' · ',e.ubicacion),1000),e.id,concat('EVENTO:',e.id)
            FROM evento_escolar e JOIN institucion inst ON inst.id=e.institucion_id
            WHERE e.institucion_id=:institucionId AND e.estado='PUBLICADO' AND e.fin_en>=:desdeEventos
              AND EXISTS(SELECT 1 FROM tutor t JOIN alumno_tutor at ON at.tutor_id=t.id
                JOIN alumno al ON al.id=at.alumno_id JOIN inscripcion i ON i.alumno_id=al.id
                JOIN grado gr ON gr.id=i.grado_id
                WHERE t.usuario_id=:usuarioId AND t.activo=true AND al.activo=true AND at.activo=true
                  AND at.puede_recibir_notificaciones=true AND at.fecha_inicio<=:hoy
                  AND (at.fecha_fin IS NULL OR at.fecha_fin>=:hoy)
                  AND i.ciclo_escolar_id=e.ciclo_escolar_id AND i.estado<>'CANCELADA'
                  AND (e.plantel_id IS NULL OR i.plantel_id=e.plantel_id)
                  AND (e.alcance IN ('INSTITUCION','PLANTEL') OR EXISTS(
                    SELECT 1 FROM destinatario_evento de WHERE de.evento_id=e.id
                      AND (de.alumno_id=al.id OR de.grado_id=i.grado_id OR de.nivel_educativo_id=gr.nivel_educativo_id
                        OR (de.grupo_id IS NOT NULL AND EXISTS(SELECT 1 FROM asignacion_grupo ag
                          WHERE ag.inscripcion_id=i.id AND ag.grupo_id=de.grupo_id
                            AND ag.fecha_inicio<=timezone(inst.zona_horaria,e.inicio_en)::date
                            AND (ag.fecha_fin IS NULL OR ag.fecha_fin>=timezone(inst.zona_horaria,e.inicio_en)::date))))))
              ) ON CONFLICT(usuario_id,clave_deduplicacion) DO NOTHING
            """,p);
        jdbc.update("""
            INSERT INTO notificacion_usuario(usuario_id,tipo,titulo,mensaje,pago_id,clave_deduplicacion)
            SELECT :usuarioId,concat('PAGO_',pago.estado),
                   CASE pago.estado WHEN 'VALIDADO' THEN 'Pago validado'
                     WHEN 'RECHAZADO' THEN 'Pago rechazado' ELSE 'Pago cancelado' END,
                   left(CASE pago.estado
                     WHEN 'VALIDADO' THEN concat('El pago ',pago.folio,' por ',pago.monto,' ',pago.moneda,' fue validado correctamente.')
                     WHEN 'RECHAZADO' THEN concat('El pago ',pago.folio,' por ',pago.monto,' ',pago.moneda,' fue rechazado. Motivo: ',pago.motivo_rechazo_cancelacion)
                     ELSE concat('El pago ',pago.folio,' por ',pago.monto,' ',pago.moneda,' fue cancelado. Motivo: ',pago.motivo_rechazo_cancelacion)
                   END,1000),pago.id,concat('PAGO_',pago.estado,':',pago.id)
            FROM pago JOIN tutor t ON t.id=pago.tutor_id
            WHERE pago.institucion_id=:institucionId AND pago.estado IN ('VALIDADO','RECHAZADO','CANCELADO')
              AND pago.actualizado_en>=:desdePagos AND t.usuario_id=:usuarioId AND t.activo=true
              AND EXISTS(SELECT 1 FROM alumno_tutor at JOIN alumno al ON al.id=at.alumno_id
                WHERE at.tutor_id=t.id AND at.activo=true AND al.activo=true
                  AND at.es_responsable_financiero=true AND at.puede_ver_finanzas=true
                  AND at.puede_recibir_notificaciones=true AND at.fecha_inicio<=:hoy
                  AND (at.fecha_fin IS NULL OR at.fecha_fin>=:hoy))
            ON CONFLICT(usuario_id,clave_deduplicacion) DO NOTHING
            """,p);
        jdbc.update("""
            INSERT INTO notificacion_usuario(usuario_id,tipo,titulo,mensaje,aviso_id,clave_deduplicacion)
            SELECT :usuarioId,'AVISO',a.titulo,left(a.contenido,1000),a.id,concat('AVISO:',a.id)
            FROM aviso a WHERE a.institucion_id=:institucionId AND a.estado='PUBLICADO'
              AND a.publicado_en<=:ahora AND (a.expira_en IS NULL OR a.expira_en>:ahora)
              AND EXISTS(SELECT 1 FROM tutor t JOIN alumno_tutor at ON at.tutor_id=t.id JOIN alumno al ON al.id=at.alumno_id
                WHERE t.usuario_id=:usuarioId AND t.activo=true AND al.activo=true AND at.activo=true
                  AND at.puede_recibir_notificaciones=true AND at.fecha_inicio<=:hoy
                  AND (at.fecha_fin IS NULL OR at.fecha_fin>=:hoy)
                  AND (a.plantel_id IS NULL OR EXISTS(SELECT 1 FROM inscripcion i WHERE i.alumno_id=al.id
                    AND i.plantel_id=a.plantel_id AND i.estado IN ('PREINSCRITA','ACTIVA')
                    AND i.fecha_inicio<=:hoy AND (i.fecha_fin IS NULL OR i.fecha_fin>=:hoy))))
              ON CONFLICT(usuario_id,clave_deduplicacion) DO NOTHING
            """,p);
    }

    public PortalNotificaciones consultar(Long usuarioId,String zona,int pagina,int tamanio){
        var p=new MapSqlParameterSource().addValue("usuarioId",usuarioId).addValue("zona",zona)
                .addValue("limite",tamanio).addValue("offset",(long)pagina*tamanio);
        String base=" FROM notificacion_usuario n LEFT JOIN pago pago_actual ON pago_actual.id=n.pago_id "
                + "WHERE n.usuario_id=:usuarioId AND (n.pago_id IS NULL OR n.tipo=concat('PAGO_',pago_actual.estado))";
        Long total=jdbc.queryForObject("SELECT count(*)"+base,p,Long.class);
        Long pendientes=jdbc.queryForObject("SELECT count(*)"+base+" AND n.leida_en IS NULL",p,Long.class);
        var filas=jdbc.query("""
            SELECT n.id,n.tipo,n.titulo,n.mensaje,timezone(:zona,n.creado_en) creada_local,n.leida_en,
                   CASE WHEN n.tipo='EVENTO' THEN '#agenda' WHEN n.tipo='AVISO' THEN '#avisos' ELSE '#cuenta' END destino
            """+base+" ORDER BY (n.leida_en IS NULL) DESC,n.creado_en DESC,n.id DESC LIMIT :limite OFFSET :offset",
                p,(rs,n)->new PortalNotificacionFila(rs.getLong("id"),rs.getString("tipo"),rs.getString("titulo"),
                rs.getString("mensaje"),rs.getObject("creada_local",LocalDateTime.class),rs.getObject("leida_en")!=null,rs.getString("destino")));
        return new PortalNotificaciones(pendientes==null?0:pendientes,new PageImpl<>(filas,PageRequest.of(pagina,tamanio),total==null?0:total));
    }

    public boolean eventoAccesible(Long usuarioId,Long institucionId,Long eventoId,LocalDate hoy){return Boolean.TRUE.equals(jdbc.queryForObject("""
        SELECT EXISTS(SELECT 1 FROM evento_escolar e JOIN institucion inst ON inst.id=e.institucion_id
          WHERE e.id=:eventoId AND e.institucion_id=:institucionId AND e.estado='PUBLICADO' AND EXISTS(SELECT 1 FROM tutor t
            JOIN alumno_tutor at ON at.tutor_id=t.id JOIN alumno al ON al.id=at.alumno_id
            JOIN inscripcion i ON i.alumno_id=al.id JOIN grado gr ON gr.id=i.grado_id
            WHERE t.usuario_id=:usuarioId AND t.activo=true AND al.activo=true AND at.activo=true
              AND at.fecha_inicio<=:hoy AND (at.fecha_fin IS NULL OR at.fecha_fin>=:hoy)
              AND i.ciclo_escolar_id=e.ciclo_escolar_id AND i.estado<>'CANCELADA'
              AND (e.plantel_id IS NULL OR i.plantel_id=e.plantel_id)
              AND (e.alcance IN ('INSTITUCION','PLANTEL') OR EXISTS(SELECT 1 FROM destinatario_evento de
                WHERE de.evento_id=e.id AND (de.alumno_id=al.id OR de.grado_id=i.grado_id
                  OR de.nivel_educativo_id=gr.nivel_educativo_id
                  OR (de.grupo_id IS NOT NULL AND EXISTS(SELECT 1 FROM asignacion_grupo ag
                    WHERE ag.inscripcion_id=i.id AND ag.grupo_id=de.grupo_id
                      AND ag.fecha_inicio<=timezone(inst.zona_horaria,e.inicio_en)::date
                      AND (ag.fecha_fin IS NULL OR ag.fecha_fin>=timezone(inst.zona_horaria,e.inicio_en)::date))))))))
        """,new MapSqlParameterSource().addValue("usuarioId",usuarioId).addValue("institucionId",institucionId)
                .addValue("eventoId",eventoId).addValue("hoy",hoy),Boolean.class));}

    public boolean avisoAccesible(Long usuarioId,Long institucionId,Long avisoId,LocalDate hoy,Instant ahora){return Boolean.TRUE.equals(jdbc.queryForObject("""
        SELECT EXISTS(SELECT 1 FROM aviso a WHERE a.id=:avisoId AND a.institucion_id=:institucionId AND a.estado='PUBLICADO'
          AND a.publicado_en<=:ahora AND (a.expira_en IS NULL OR a.expira_en>:ahora)
          AND EXISTS(SELECT 1 FROM tutor t JOIN alumno_tutor at ON at.tutor_id=t.id JOIN alumno al ON al.id=at.alumno_id
            WHERE t.usuario_id=:usuarioId AND t.activo=true AND al.activo=true AND at.activo=true
              AND at.fecha_inicio<=:hoy AND (at.fecha_fin IS NULL OR at.fecha_fin>=:hoy)
              AND (a.plantel_id IS NULL OR EXISTS(SELECT 1 FROM inscripcion i WHERE i.alumno_id=al.id
                AND i.plantel_id=a.plantel_id AND i.estado IN ('PREINSCRITA','ACTIVA')
                AND i.fecha_inicio<=:hoy AND (i.fecha_fin IS NULL OR i.fecha_fin>=:hoy)))))
        """,new MapSqlParameterSource().addValue("usuarioId",usuarioId).addValue("institucionId",institucionId).addValue("avisoId",avisoId)
                .addValue("hoy",hoy).addValue("ahora",Timestamp.from(ahora)),Boolean.class));}

    public boolean pagoAccesible(Long usuarioId,Long institucionId,Long pagoId,String estado,LocalDate hoy){return Boolean.TRUE.equals(jdbc.queryForObject("""
        SELECT EXISTS(SELECT 1 FROM pago p JOIN tutor t ON t.id=p.tutor_id
          WHERE p.id=:pagoId AND p.institucion_id=:institucionId AND p.estado=:estado
            AND t.usuario_id=:usuarioId AND t.activo=true
            AND EXISTS(SELECT 1 FROM alumno_tutor at JOIN alumno al ON al.id=at.alumno_id
              WHERE at.tutor_id=t.id AND at.activo=true AND al.activo=true
                AND at.es_responsable_financiero=true AND at.puede_ver_finanzas=true
                AND at.fecha_inicio<=:hoy AND (at.fecha_fin IS NULL OR at.fecha_fin>=:hoy)))
        """,new MapSqlParameterSource().addValue("usuarioId",usuarioId).addValue("institucionId",institucionId)
                .addValue("pagoId",pagoId).addValue("estado",estado).addValue("hoy",hoy),Boolean.class));}
}
