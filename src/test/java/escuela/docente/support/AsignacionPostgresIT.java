package escuela.docente.support;

import escuela.docente.repository.AsignacionMaestroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.jpa.repository.Query;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;

/** Ejecutar explícitamente contra PostgreSQL temporal; jamás usa la configuración real. */
@EnabledIfEnvironmentVariable(named="QA_ASIGNACION_JDBC_URL",matches=".+")
class AsignacionPostgresIT {
 @Test void consultaRealConParametrosNulosYLimitesInclusivos()throws Exception{
  var consulta=AsignacionMaestroRepository.class.getMethod("existeTraslape",Long.class,Long.class,Long.class,LocalDate.class,LocalDate.class,Long.class).getAnnotation(Query.class).value();
  var nombres=new ArrayList<String>();var matcher=Pattern.compile(":([A-Za-z]+)").matcher(consulta);var sql=new StringBuffer();
  while(matcher.find()){nombres.add(matcher.group(1));matcher.appendReplacement(sql,"?");}matcher.appendTail(sql);
  try(var db=DriverManager.getConnection(System.getenv("QA_ASIGNACION_JDBC_URL"),"postgres","qa_asignacion_aislada");var st=db.createStatement()){
   st.execute("create temporary table asignacion_maestro(id bigint,maestro_id bigint,grupo_id bigint,materia_id bigint,activo boolean,fecha_inicio date,fecha_fin date)");
   st.execute("insert into asignacion_maestro values(1,10,20,30,true,'2026-11-01','2026-11-30'),(2,10,20,30,false,'2026-01-01',null)");
   assertThat(existe(db,sql.toString(),nombres,10L,"2026-10-01",null,0L)).isTrue();
   assertThat(existe(db,sql.toString(),nombres,10L,"2026-10-01","2026-10-31",0L)).isFalse();
   assertThat(existe(db,sql.toString(),nombres,10L,"2026-10-01","2026-11-01",0L)).isTrue();
   assertThat(existe(db,sql.toString(),nombres,10L,"2026-12-01",null,0L)).isFalse();
   assertThat(existe(db,sql.toString(),nombres,11L,"2026-11-01",null,0L)).isFalse();
   assertThat(existe(db,sql.toString(),nombres,10L,"2026-11-01",null,1L)).isFalse();
   st.execute("insert into asignacion_maestro values(3,10,20,30,true,'2027-01-01',null)");
   assertThat(existe(db,sql.toString(),nombres,10L,"2027-02-01",null,0L)).isTrue();
  }
 }
 private boolean existe(Connection db,String sql,List<String> nombres,Long maestro,String inicio,String fin,Long id)throws SQLException{
  Map<String,Object> datos=new HashMap<>();datos.put("maestroId",maestro);datos.put("grupoId",20L);datos.put("materiaId",30L);datos.put("id",id);datos.put("fechaInicio",LocalDate.parse(inicio));datos.put("fechaFin",fin==null?null:LocalDate.parse(fin));
  try(var p=db.prepareStatement(sql)){for(int i=0;i<nombres.size();i++){Object v=datos.get(nombres.get(i));if(v==null)p.setNull(i+1,Types.NULL);else p.setObject(i+1,v);}try(var r=p.executeQuery()){r.next();return r.getBoolean(1);}}
 }
}
