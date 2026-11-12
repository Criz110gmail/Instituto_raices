package escuela.sitioweb;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.web.util.HtmlUtils;

/** El mapa se construye desde una dirección; no admite iframe, HTML ni URL de usuario. */
public final class SitioMapa {
    private SitioMapa() {}
    private static String esc(String s){return HtmlUtils.htmlEscape(Objects.toString(s,""),"UTF-8");}
    private static String direccion(String s){var value=Objects.toString(s,"").trim();return value.substring(0,Math.min(500,value.length()));}
    public static String embed(String s,int zoom){return "https://maps.google.com/maps?q="+URLEncoder.encode(direccion(s),StandardCharsets.UTF_8)+"&z="+Math.max(10,Math.min(19,zoom))+"&output=embed&hl=es";}
    public static String comoLlegar(String s){return "https://www.google.com/maps/dir/?api=1&destination="+URLEncoder.encode(direccion(s),StandardCharsets.UTF_8);}
    public static String html(String titulo,String direccion,int zoom,String diseno,String horario,String telefono){
        var place=direccion(direccion);var out=new StringBuilder("<article class=\"nd-map-card\" data-nd-map=\"true\" data-map-layout=\"").append("solo".equals(diseno)?"solo":"tarjeta").append("\"><div class=\"nd-map-info\"><span class=\"nd-map-eyebrow\">CERCA DE TI</span><h3 class=\"nd-map-title\">").append(esc(titulo==null||titulo.isBlank()?"Ven a conocernos":titulo)).append("</h3><p class=\"nd-map-address\">").append(esc(place.isBlank()?"Agrega la dirección de tu plantel en Propiedades.":place)).append("</p>");
        for(var info:new String[]{horario,telefono})if(info!=null&&!info.isBlank())out.append("<p class=\"nd-map-detail\">").append(esc(info)).append("</p>");
        if(!place.isBlank())out.append("<a class=\"nd-map-directions\" href=\"").append(esc(comoLlegar(place))).append("\" target=\"_blank\" rel=\"noopener noreferrer\">Cómo llegar ↗</a>");
        out.append("</div><div class=\"nd-map-surface\">");if(!place.isBlank())out.append("<iframe class=\"nd-map-frame\" src=\"").append(esc(embed(place,zoom))).append("\" title=\"Mapa de ").append(esc(place)).append("\" loading=\"lazy\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>");else out.append("<div class=\"nd-map-empty\">⌖</div><p class=\"nd-map-placeholder\">Tu ubicación aparecerá aquí</p>");
        return out.append("</div></article>").toString();
    }
}
