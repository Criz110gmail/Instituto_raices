package escuela.sitioweb;

import escuela.seguridad.service.UsuarioPrincipal;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;
import static escuela.sitioweb.SitioExtras.*;

@Service @RequiredArgsConstructor @Transactional
public class SitioCatalogosService {
    private final SitioService sitios;
    private final JdbcTemplate jdbc;
    public Album album(UsuarioPrincipal p,String id){return sitios.borrador(p).getExtras().getAlbumes().stream().filter(a->a.getId().equals(id)).findFirst().orElseThrow(()->new ReglaNegocioException("El álbum no pertenece a este borrador."));}
    public Foto foto(UsuarioPrincipal p,String album,String id){return album(p,album).getFotos().stream().filter(f->f.getId().equals(id)).findFirst().orElseThrow(()->new ReglaNegocioException("La fotografía no pertenece al álbum."));}
    public Ubicacion ubicacion(UsuarioPrincipal p,String id){return sitios.borrador(p).getExtras().getUbicaciones().stream().filter(u->u.getId().equals(id)).findFirst().orElseThrow(()->new ReglaNegocioException("La ubicación no pertenece a este borrador."));}
    private SitioForm base(UsuarioPrincipal p,long version){var f=sitios.borrador(p);if(f.getVersion()!=version)throw new ReglaNegocioException("El borrador cambió. Recarga antes de guardar; tu captura no se ha aplicado.");if(version==0)throw new ReglaNegocioException("Guarda primero la identidad y el contenido del sitio.");return f;}
    public void guardarAlbum(UsuarioPrincipal p,String id,Album item,long version){var f=base(p,version);var lista=f.getExtras().getAlbumes();if(id==null){if(lista.size()>=20)throw new ReglaNegocioException("Puedes conservar hasta 20 álbumes.");item.setId(UUID.randomUUID().toString());item.setFotos(new ArrayList<>());lista.add(item);}else{var anterior=lista.stream().filter(a->a.getId().equals(id)).findFirst().orElseThrow(()->new ReglaNegocioException("Álbum no encontrado."));retirar(anterior.isActivo(),item.isActivo(),item.getMotivoRetiro());item.setId(id);item.setFotos(anterior.getFotos());lista.set(lista.indexOf(anterior),item);}sitios.guardar(p,f);}
    public void guardarFoto(UsuarioPrincipal p,String albumId,String id,Foto item,long version){var f=base(p,version);var album=f.getExtras().getAlbumes().stream().filter(a->a.getId().equals(albumId)).findFirst().orElseThrow(()->new ReglaNegocioException("Álbum no encontrado."));if(!album.isActivo())throw new ReglaNegocioException("Reactiva el álbum antes de cambiar sus fotografías.");var lista=album.getFotos();if(lista.stream().anyMatch(x->x.isActivo()&&item.isActivo()&&x.getImagenId().equals(item.getImagenId())&&!x.getId().equals(id)))throw new ReglaNegocioException("Esta imagen ya está activa dentro del álbum.");if(id==null){if(lista.size()>=100)throw new ReglaNegocioException("Se alcanzó el límite de 100 fotografías del álbum.");item.setId(UUID.randomUUID().toString());lista.add(item);}else{var anterior=lista.stream().filter(x->x.getId().equals(id)).findFirst().orElseThrow(()->new ReglaNegocioException("Fotografía no encontrada."));retirar(anterior.isActivo(),item.isActivo(),item.getMotivoRetiro());item.setId(id);lista.set(lista.indexOf(anterior),item);}sitios.guardar(p,f);}
    public void guardarUbicacion(UsuarioPrincipal p,String id,Ubicacion item,long version){var f=base(p,version);var lista=f.getExtras().getUbicaciones();if(lista.stream().anyMatch(x->x.isActivo()&&item.isActivo()&&x.getPlantelId().equals(item.getPlantelId())&&!x.getId().equals(id)))throw new ReglaNegocioException("Ese plantel ya tiene una ubicación activa.");if(id==null){if(lista.size()>=30)throw new ReglaNegocioException("Puedes conservar hasta 30 ubicaciones.");item.setId(UUID.randomUUID().toString());lista.add(item);}else{var anterior=lista.stream().filter(x->x.getId().equals(id)).findFirst().orElseThrow(()->new ReglaNegocioException("Ubicación no encontrada."));retirar(anterior.isActivo(),item.isActivo(),item.getMotivoRetiro());item.setId(id);lista.set(lista.indexOf(anterior),item);}sitios.guardar(p,f);}
    public void guardarContacto(UsuarioPrincipal p,Contacto contacto,long version){var f=base(p,version);if(contacto.getWhatsapp()!=null)contacto.setWhatsapp(contacto.getWhatsapp().replaceAll("[ +()\\-]",""));f.getExtras().setContacto(contacto);sitios.guardar(p,f);}
    private void retirar(boolean antes,boolean ahora,String motivo){if(antes&&!ahora&&(motivo==null||motivo.isBlank()))throw new ReglaNegocioException("Escribe el motivo para retirar este registro de una nueva publicación.");}
    public List<Map<String,Object>> planteles(UsuarioPrincipal p){sitios.autorizar(p,"SITIO_WEB_EDITAR");return jdbc.queryForList("SELECT id,codigo,nombre FROM plantel WHERE institucion_id=? AND activo=true ORDER BY nombre,id",p.institucionId());}
}
