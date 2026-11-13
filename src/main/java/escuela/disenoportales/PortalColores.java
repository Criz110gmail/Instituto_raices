package escuela.disenoportales;
import java.util.*;
public final class PortalColores {
 private PortalColores(){}
 public static boolean valido(String c){return c!=null&&c.matches("#[0-9a-fA-F]{6}");}
 public static double luminancia(String c){double result=0;double[] w={.2126,.7152,.0722};for(int i=0;i<3;i++){double v=Integer.parseInt(c.substring(1+i*2,3+i*2),16)/255.;result+=w[i]*(v<=.04045?v/12.92:Math.pow((v+.055)/1.055,2.4));}return result;}
 public static String texto(String c){return luminancia(c)>.179?"#000000":"#ffffff";}
 public static String variables(String principal,String fondo,String acento){
  if(!valido(principal)||!valido(fondo)||!valido(acento))return "";
  return "--pi-principal:"+principal+";--pi-fondo:"+fondo+";--pi-acento:"+acento+";--pi-story-text:"+texto(principal)+";--pi-on-accent:"+texto(acento)+";--pi-link:"+(luminancia(acento)>.179?"#173d32":acento)+";--pi-link-dark:"+(luminancia(acento)>.179?acento:"#ffffff")+";";
 }
}
