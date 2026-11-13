package escuela.disenoportales;
import java.util.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
public enum PortalTipo {
 ADMIN("admin","Administración","Tu operación escolar, conectada.","/login","login","#214936","#f5f7f2","#72a629"),
 FAMILIAS("familias","Familias","Más cerca de su aprendizaje.","/familias","login-familias","#b85e48","#f8f4ee","#237d6a"),
 MAESTROS("maestros","Maestros","Un espacio para enseñar y crecer.","/maestros/acceso","login-maestros","#16526e","#edf5f8","#17698e"),
 PERSONAL("personal","Personal · Mis recibos","Tu nómina, clara y a tu alcance.","/personal/acceso","login-personal","#194f3d","#eff5ef","#176a55");
 public final String codigo,nombre,descripcion,ruta,plantilla,principal,fondo,acento;
 PortalTipo(String c,String n,String d,String r,String t,String p,String f,String a){codigo=c;nombre=n;descripcion=d;ruta=r;plantilla=t;principal=p;fondo=f;acento=a;}
 public String getCodigo(){return codigo;}public String getNombre(){return nombre;}public String getDescripcion(){return descripcion;}
 public static PortalTipo de(String s){return Arrays.stream(values()).filter(p->p.codigo.equals(s)).findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
}
