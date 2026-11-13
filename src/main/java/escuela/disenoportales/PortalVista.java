package escuela.disenoportales;
import org.springframework.ui.Model;
public final class PortalVista {
 private PortalVista(){}
 public static void preparar(Model m,PortalIdentidadService.Tema t,boolean previa,String logoUrl,boolean vinculado){m.addAttribute("portalIdentidad",t);m.addAttribute("portalPrevia",previa);m.addAttribute("portalLogo",t.isTieneLogo()?logoUrl:null);m.addAttribute("portalInstitucionAcceso",vinculado?t.codigo():null);}
}
