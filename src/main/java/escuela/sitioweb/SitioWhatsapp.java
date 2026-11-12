package escuela.sitioweb;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.web.util.HtmlUtils;

/** Enlace Click to Chat de destino fijo; el visitante confirma el envío en WhatsApp. */
public final class SitioWhatsapp {
    private SitioWhatsapp() {}
    public static String numero(String raw){
        var value=Objects.toString(raw,"").trim();if(value.length()>40||!value.matches("\\+?[0-9 ()-]+"))return "";
        var digits=value.replaceAll("[ ()-]","").replaceFirst("^\\+","");return digits.matches("[1-9][0-9]{7,14}")?digits:"";
    }
    public static String url(String phone,String message){
        var digits=numero(phone);if(digits.isEmpty())return "";var text=Objects.toString(message,"");return "https://wa.me/"+digits+(text.isEmpty()?"":"?text="+URLEncoder.encode(text.substring(0,Math.min(1000,text.length())),StandardCharsets.UTF_8));
    }
    public static String contenido(String label){
        var text=label==null||label.isBlank()?"Escríbenos por WhatsApp":label;
        return "<span class=\"nd-whatsapp-symbol\"><svg viewBox=\"0 0 24 24\" aria-hidden=\"true\" focusable=\"false\"><path d=\"M20.5 11.8a8.5 8.5 0 0 1-12.7 7.4L3 20.5l1.3-4.7A8.5 8.5 0 1 1 20.5 11.8Z\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.7\" stroke-linejoin=\"round\"/><path d=\"M8.1 7.4c-.6 0-1.2.8-1.2 1.6 0 2.6 4.1 6.7 6.8 6.7.9 0 1.8-.6 1.8-1.3 0-.3-1.8-1.2-2.1-1.2-.3 0-.7.8-1 .8-.8 0-3.1-2.1-3.1-3 0-.3.7-.7.7-1s-1-2.6-1.9-2.6Z\" fill=\"currentColor\"/></svg></span><span class=\"nd-whatsapp-label\">"+HtmlUtils.htmlEscape(text,"UTF-8")+"</span>";
    }
}
