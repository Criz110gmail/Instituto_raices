package escuela.disenoportales;
import escuela.inicio.controller.LoginController;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@ControllerAdvice(assignableTypes=LoginController.class) @RequiredArgsConstructor
public class PortalLoginAdvice {
 private final PortalIdentidadService service;
 @Value("${app.sitio.codigo:}") private String predeterminada;
 @ModelAttribute void identidad(HttpServletRequest r,HttpServletResponse response,Model m){String explicit=r.getParameter("institucion"),codigo=explicit==null?predeterminada:explicit;if(codigo==null||codigo.isBlank())return;String path=r.getRequestURI().substring(r.getContextPath().length());String portal=path.equals("/familias")?"familias":path.equals("/personal/acceso")?"personal":path.equals("/maestros/acceso")?"maestros":java.util.Set.of("personal","familias","maestros").contains(java.util.Objects.toString(r.getParameter("origen"),""))?r.getParameter("origen"):"admin";try{var t=service.publico(codigo,PortalTipo.de(portal));if(t.version()>0||explicit!=null||t.portal()==PortalTipo.ADMIN){PortalVista.preparar(m,t,false,"/accesos/"+org.springframework.web.util.UriUtils.encodePathSegment(codigo,java.nio.charset.StandardCharsets.UTF_8)+"/"+portal+"/logo.png",explicit!=null);response.setHeader("Cache-Control","no-store");}}catch(ResponseStatusException e){if(explicit!=null)throw e;}}
}
