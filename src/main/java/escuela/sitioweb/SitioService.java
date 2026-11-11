package escuela.sitioweb;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
@Service @RequiredArgsConstructor @Transactional
public class SitioService {
 private final JdbcTemplate jdbc;
 private static final tools.jackson.databind.json.JsonMapper JSON=tools.jackson.databind.json.JsonMapper.builder().build();
 public void autorizar(UsuarioPrincipal p,String permiso){
  if(p==null||p.accesoRecuperacion()||p.usuarioId()==null||p.institucionId()==null||!p.alcanceInstitucional()||p.authorities().stream().noneMatch(a->a.getAuthority().equals(permiso)))throw new AccessDeniedException("Se requiere permiso de diseño web y alcance institucional");
 }
 public SitioForm borrador(UsuarioPrincipal p){autorizar(p,"SITIO_WEB_EDITAR");var rows=jdbc.queryForList("SELECT * FROM sitio_web WHERE institucion_id=?",p.institucionId());
  if(rows.isEmpty()){var f=new SitioForm();f.setNombre(jdbc.queryForObject("SELECT nombre FROM institucion WHERE id=?",String.class,p.institucionId()));return f;}return convertir(rows.getFirst());}
 private SitioForm convertir(Map<String,Object> r){var f=new SitioForm();f.setNombre((String)r.get("nombre"));f.setEslogan((String)r.get("eslogan"));f.setPortada((String)r.get("portada"));f.setNosotros((String)r.get("nosotros"));f.setOferta((String)r.get("oferta"));f.setTalleres((String)r.get("talleres"));f.setInscripciones((String)r.get("inscripciones"));f.setPrincipal((String)r.get("principal"));f.setAcento((String)r.get("acento"));if(r.get("extras")!=null)f.setExtras(JSON.readValue(r.get("extras").toString(),SitioExtras.class));if(r.get("diseno")!=null)f.setDiseno(JSON.readValue(r.get("diseno").toString(),SitioDiseno.class));f.setLogoId((Long)r.get("logo_id"));f.setPortadaId((Long)r.get("portada_id"));f.setPortadaAlt((String)r.get("portada_alt"));f.setVersion(((Number)r.get("version")).longValue());return f;}
 public void guardar(UsuarioPrincipal p,SitioForm f){autorizar(p,"SITIO_WEB_EDITAR");validar(f);validarImagen(p,f.getLogoId());validarImagen(p,f.getPortadaId());
  if(f.getDiseno()!=null)for(var bloque:f.getDiseno().getBloques()){validarImagen(p,bloque.getImagenId());for(var id:bloque.getImagenes().values())validarImagen(p,id);if(bloque.getAlbumId()!=null&&!bloque.getAlbumId().isBlank()&&f.getExtras().getAlbumes().stream().noneMatch(a->a.getId().equals(bloque.getAlbumId())))throw new ReglaNegocioException("El álbum seleccionado no pertenece a tu institución.");}
  if(f.getDiseno()!=null)for(var n:f.getDiseno().getTodosComponentes()){validarImagen(p,n.getImagenId());if(n.getAlbumId()!=null&&!n.getAlbumId().isBlank()&&f.getExtras().getAlbumes().stream().noneMatch(a->a.getId().equals(n.getAlbumId())))throw new ReglaNegocioException("El álbum seleccionado no pertenece a tu institución.");}
  var imagenesGaleria=new HashSet<Long>();for(var album:f.getExtras().getAlbumes())for(var foto:album.getFotos())imagenesGaleria.add(foto.getImagenId());for(var imagenId:imagenesGaleria)validarImagen(p,imagenId);
  for(var u:f.getExtras().getUbicaciones())if(!Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM plantel WHERE id=? AND institucion_id=? AND (activo=true OR ?=false))",Boolean.class,u.getPlantelId(),p.institucionId(),u.isActivo())))throw new ReglaNegocioException("Selecciona un plantel activo de tu institución para la ubicación publicada.");
  jdbc.queryForObject("SELECT id FROM institucion WHERE id=? FOR UPDATE",Long.class,p.institucionId());
  var versiones=jdbc.queryForList("SELECT version FROM sitio_web WHERE institucion_id=?",Long.class,p.institucionId());
  if((versiones.isEmpty()?0L:versiones.getFirst())!=f.getVersion().longValue())throw new ReglaNegocioException("El borrador cambió. Recarga antes de guardar.");
  jdbc.update("""
   INSERT INTO sitio_web(institucion_id,nombre,eslogan,portada,nosotros,oferta,talleres,inscripciones,principal,acento,version) VALUES (?,?,?,?,?,?,?,?,?,?,?)
   ON CONFLICT(institucion_id) DO UPDATE SET nombre=EXCLUDED.nombre,eslogan=EXCLUDED.eslogan,portada=EXCLUDED.portada,nosotros=EXCLUDED.nosotros,oferta=EXCLUDED.oferta,talleres=EXCLUDED.talleres,inscripciones=EXCLUDED.inscripciones,principal=EXCLUDED.principal,acento=EXCLUDED.acento,version=EXCLUDED.version
   """,p.institucionId(),f.getNombre(),f.getEslogan(),(f.getPortada()==null?"":f.getPortada()),(f.getNosotros()==null?"":f.getNosotros()),(f.getOferta()==null?"":f.getOferta()),(f.getTalleres()==null?"":f.getTalleres()),(f.getInscripciones()==null?"":f.getInscripciones()),f.getPrincipal(),f.getAcento(),f.getVersion()+1);
  jdbc.update("UPDATE sitio_web SET logo_id=?,portada_id=?,portada_alt=? WHERE institucion_id=?",f.getLogoId(),f.getPortadaId(),f.getPortadaAlt(),p.institucionId());
  jdbc.update("UPDATE sitio_web SET extras=?::jsonb WHERE institucion_id=?",JSON.writeValueAsString(f.getExtras()),p.institucionId());
  jdbc.update("UPDATE sitio_web SET diseno=?::jsonb WHERE institucion_id=?",f.getDiseno()==null?null:JSON.writeValueAsString(f.getDiseno()),p.institucionId());
  jdbc.update("INSERT INTO sitio_web_revision(institucion_id,version,contenido,guardado_por) SELECT institucion_id,version,to_jsonb(s),? FROM sitio_web s WHERE institucion_id=?",p.usuarioId(),p.institucionId());
 }
 public void publicar(UsuarioPrincipal p,long version){autorizar(p,"SITIO_WEB_PUBLICAR");autorizar(p,"SITIO_WEB_EDITAR");
  jdbc.queryForObject("SELECT id FROM institucion WHERE id=? FOR UPDATE",Long.class,p.institucionId());
  var actual=borrador(p);if(actual.getVersion()!=version)throw new ReglaNegocioException("El borrador cambió. Recarga antes de publicar.");
  if(actual.getDiseno()!=null)actual.getDiseno().validar(true);
  int n=jdbc.update("""
   INSERT INTO sitio_web_publicacion(institucion_id,nombre,eslogan,portada,nosotros,oferta,talleres,inscripciones,principal,acento,version,publicado_por,logo_id,portada_id,portada_alt,extras,diseno)
   SELECT institucion_id,nombre,eslogan,portada,nosotros,oferta,talleres,inscripciones,principal,acento,version,?,logo_id,portada_id,portada_alt,extras,diseno FROM sitio_web WHERE institucion_id=? AND version=? ON CONFLICT DO NOTHING
   """,p.usuarioId(),p.institucionId(),version);
  if(n!=1)throw new ReglaNegocioException("El borrador cambió o esta versión ya está publicada. Recarga y revisa.");
 }
 @Transactional(readOnly=true) public List<Map<String,Object>> historial(UsuarioPrincipal p){autorizar(p,"SITIO_WEB_EDITAR");return fechas(jdbc.queryForList("SELECT version,publicado_en FROM sitio_web_publicacion WHERE institucion_id=? ORDER BY id DESC LIMIT 50",p.institucionId()),p,"publicado_en");}
 @Transactional(readOnly=true) public SitioForm publico(String codigo){var r=jdbc.queryForList("SELECT s.* FROM sitio_web_publicacion s JOIN institucion i ON i.id=s.institucion_id WHERE i.codigo=? AND i.activo=true ORDER BY s.id DESC LIMIT 1",codigo);if(r.isEmpty())throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);return convertir(r.getFirst());}

 private List<Map<String,Object>> fechas(List<Map<String,Object>> filas,UsuarioPrincipal p,String campo){
  var zona=java.time.ZoneId.of(jdbc.queryForObject("SELECT zona_horaria FROM institucion WHERE id=?",String.class,p.institucionId()));
  filas.forEach(f->f.computeIfPresent(campo,(k,v)->v instanceof java.sql.Timestamp t?t.toInstant().atZone(zona):v));return filas;
 }
 private void validar(SitioForm f){
  try(var factory=jakarta.validation.Validation.buildDefaultValidatorFactory()){
   if(!factory.getValidator().validate(f).isEmpty())throw new ReglaNegocioException("Completa los campos obligatorios y revisa los colores.");
  }
  var extras=f.getExtras();
  if(extras.getAlbumes().stream().map(SitioExtras.Album::getId).distinct().count()!=extras.getAlbumes().size())throw new ReglaNegocioException("Cada álbum debe tener un identificador distinto.");
  for(var album:extras.getAlbumes()){
   if(album.getFotos().stream().map(SitioExtras.Foto::getId).distinct().count()!=album.getFotos().size())throw new ReglaNegocioException("Cada fotografía del álbum debe ser distinta.");
   var activas=album.getFotos().stream().filter(SitioExtras.Foto::isActivo).toList();if(activas.stream().map(SitioExtras.Foto::getImagenId).distinct().count()!=activas.size())throw new ReglaNegocioException("Esta imagen ya está activa dentro del álbum.");
  }
  var sedes=extras.getUbicaciones().stream().filter(SitioExtras.Ubicacion::isActivo).toList();if(sedes.stream().map(SitioExtras.Ubicacion::getPlantelId).distinct().count()!=sedes.size())throw new ReglaNegocioException("Un plantel sólo puede tener una ubicación activa.");
  if(f.getExtras().getContacto().isWhatsappActivo()&&(f.getExtras().getContacto().getWhatsapp()==null||f.getExtras().getContacto().getWhatsapp().isBlank()))throw new ReglaNegocioException("Captura el número internacional para activar WhatsApp.");
  if(f.getDiseno()!=null)f.getDiseno().validar(false);
  else if(java.util.stream.Stream.of((f.getPortada()==null?"":f.getPortada()),(f.getNosotros()==null?"":f.getNosotros()),(f.getOferta()==null?"":f.getOferta()),(f.getTalleres()==null?"":f.getTalleres()),f.getInscripciones()).anyMatch(t->t==null||t.isBlank()))throw new ReglaNegocioException("Completa los textos obligatorios de la página.");
  contraste(f.getPrincipal());
  if(luminancia(f.getAcento())<.35)throw new ReglaNegocioException("Elige un acento más claro para mantener legibles las etiquetas decorativas.");
  if(f.getPortadaId()!=null&&(f.getPortadaAlt()==null||f.getPortadaAlt().isBlank()))throw new ReglaNegocioException("Describe la fotografía de portada para quienes utilizan lectores de pantalla.");
 }
 private void validarImagen(UsuarioPrincipal p,Long id){if(id!=null&&!Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM sitio_web_imagen WHERE id=? AND institucion_id=?)",Boolean.class,id,p.institucionId())))throw new org.springframework.security.access.AccessDeniedException("La imagen no pertenece a tu institución");}
 public String codigo(UsuarioPrincipal p){autorizar(p,"SITIO_WEB_EDITAR");return jdbc.queryForObject("SELECT codigo FROM institucion WHERE id=?",String.class,p.institucionId());}
 public List<Map<String,Object>> imagenes(UsuarioPrincipal p){autorizar(p,"SITIO_WEB_EDITAR");return jdbc.queryForList("SELECT id,creado_en FROM sitio_web_imagen WHERE institucion_id=? ORDER BY id DESC LIMIT 100",p.institucionId());}
 public List<Map<String,Object>> revisiones(UsuarioPrincipal p){autorizar(p,"SITIO_WEB_EDITAR");return fechas(jdbc.queryForList("SELECT version,guardado_en FROM sitio_web_revision WHERE institucion_id=? ORDER BY version DESC LIMIT 50",p.institucionId()),p,"guardado_en");}
 public void restaurar(UsuarioPrincipal p,long revision,long versionActual){var anterior=revision(p,revision);anterior.setVersion(versionActual);guardar(p,anterior);}
 public SitioForm revision(UsuarioPrincipal p,long version){autorizar(p,"SITIO_WEB_EDITAR");var rows=jdbc.queryForList("SELECT (jsonb_populate_record(NULL::sitio_web,contenido)).* FROM sitio_web_revision WHERE institucion_id=? AND version=?",p.institucionId(),version);if(rows.isEmpty())throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);return convertir(rows.getFirst());}
 public long subir(UsuarioPrincipal p,org.springframework.web.multipart.MultipartFile archivo){
  autorizar(p,"SITIO_WEB_EDITAR");
  if(archivo.isEmpty()||archivo.getSize()>5*1024*1024)throw new ReglaNegocioException("Selecciona una imagen PNG o JPEG de hasta 5 MB.");
  jdbc.queryForObject("SELECT id FROM institucion WHERE id=? FOR UPDATE",Long.class,p.institucionId());
  if(jdbc.queryForObject("SELECT count(*) FROM sitio_web_imagen WHERE institucion_id=?",Long.class,p.institucionId())>=100)throw new ReglaNegocioException("Se alcanzó el límite de 100 imágenes del sitio.");
  try(var input=javax.imageio.ImageIO.createImageInputStream(archivo.getInputStream())){
   var readers=javax.imageio.ImageIO.getImageReaders(input);if(!readers.hasNext())throw new ReglaNegocioException("El archivo no es una imagen válida.");
   var reader=readers.next();try{reader.setInput(input);var formato=reader.getFormatName();if(!Set.of("png","jpeg","jpg").contains(formato.toLowerCase(java.util.Locale.ROOT)))throw new ReglaNegocioException("Sólo se permiten imágenes PNG o JPEG.");
    int w=reader.getWidth(0),h=reader.getHeight(0);if((long)w*h>25_000_000||w<1||h<1)throw new ReglaNegocioException("La imagen no debe superar 25 megapíxeles.");
    var original=reader.read(0);double scale=Math.min(1,2000.0/Math.max(w,h));int nw=Math.max(1,(int)(w*scale)),nh=Math.max(1,(int)(h*scale));
    var limpia=new java.awt.image.BufferedImage(nw,nh,java.awt.image.BufferedImage.TYPE_INT_ARGB);var g=limpia.createGraphics();g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);g.drawImage(original,0,0,nw,nh,null);g.dispose();var out=new java.io.ByteArrayOutputStream();javax.imageio.ImageIO.write(limpia,"png",out);if(out.size()>5*1024*1024)throw new ReglaNegocioException("La imagen procesada es demasiado grande. Usa una imagen más pequeña.");
    return jdbc.queryForObject("INSERT INTO sitio_web_imagen(institucion_id,contenido,mime,creado_por) VALUES(?,?,'image/png',?) RETURNING id",Long.class,p.institucionId(),out.toByteArray(),p.usuarioId());
   }finally{reader.dispose();}
  }catch(java.io.IOException e){throw new ReglaNegocioException("No se pudo leer la imagen. Selecciona otro archivo.");}
 }
 public byte[] imagenPrivada(UsuarioPrincipal p,long id){autorizar(p,"SITIO_WEB_EDITAR");validarImagen(p,id);return jdbc.queryForObject("SELECT contenido FROM sitio_web_imagen WHERE id=?",byte[].class,id);}
 public byte[] imagenPublica(String codigo,long id){var rows=jdbc.queryForList("SELECT im.contenido FROM sitio_web_imagen im JOIN institucion i ON i.id=im.institucion_id WHERE i.codigo=? AND i.activo=true AND im.id=? AND (im.id IN (SELECT jsonb_array_elements_text(coalesce(diseno->'imagenesComponentes','[]'::jsonb))::bigint FROM sitio_web_publicacion WHERE institucion_id=i.id) OR im.id IN (SELECT logo_id FROM sitio_web_publicacion WHERE institucion_id=i.id AND (diseno->'bloques'->0->>'tipo') IS DISTINCT FROM 'seccion' UNION SELECT portada_id FROM sitio_web_publicacion WHERE institucion_id=i.id AND diseno IS NULL) OR EXISTS(SELECT 1 FROM sitio_web_publicacion pub CROSS JOIN LATERAL jsonb_array_elements(coalesce(pub.extras->'albumes','[]'::jsonb)) album CROSS JOIN LATERAL jsonb_array_elements(coalesce(album->'fotos','[]'::jsonb)) foto WHERE pub.institucion_id=i.id AND (pub.diseno IS NULL OR jsonb_exists(coalesce(pub.diseno->'albumesComponentes','[]'::jsonb),'*') OR jsonb_exists(coalesce(pub.diseno->'albumesComponentes','[]'::jsonb),album->>'id') OR EXISTS(SELECT 1 FROM jsonb_array_elements(coalesce(pub.diseno->'bloques','[]'::jsonb)) gb WHERE (gb->>'tipo'='galeria' OR (gb->>'tipo'='escolar' AND gb->>'seccion'='galeria')) AND gb->>'visible'='true' AND (gb->>'albumId' IS NULL OR gb->>'albumId'='' OR gb->>'albumId'=album->>'id'))) AND album->>'activo'='true' AND foto->>'activo'='true' AND (foto->>'imagenId')::bigint=im.id) OR EXISTS(SELECT 1 FROM sitio_web_publicacion pub CROSS JOIN LATERAL jsonb_array_elements(coalesce(pub.diseno->'bloques','[]'::jsonb)) bloque WHERE pub.institucion_id=i.id AND bloque->>'tipo'<>'seccion' AND bloque->>'visible'='true' AND ((bloque->>'imagenId')::bigint=im.id OR EXISTS(SELECT 1 FROM jsonb_each_text(coalesce(bloque->'imagenes','{}'::jsonb)) objeto WHERE objeto.value::bigint=im.id AND (bloque->'objetos'->objeto.key->>'visible') IS DISTINCT FROM 'false'))))",codigo,id);if(rows.isEmpty())throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);return (byte[])rows.getFirst().get("contenido");}
 static double luminancia(String color){double lum=0;double[] pesos={.2126,.7152,.0722};for(int i=0;i<3;i++){double v=Integer.parseInt(color.substring(1+i*2,3+i*2),16)/255.;lum+=pesos[i]*(v<=.04045?v/12.92:Math.pow((v+.055)/1.055,2.4));}return lum;}
 static void contraste(String color){if(1.05/(luminancia(color)+.05)<4.5)throw new ReglaNegocioException("Elige un color principal más oscuro para que los botones con texto blanco se lean bien.");}
}
