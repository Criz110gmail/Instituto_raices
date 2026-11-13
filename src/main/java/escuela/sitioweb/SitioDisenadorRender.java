package escuela.sitioweb;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import java.util.*;
import java.util.function.BiConsumer;

/** Renderiza únicamente el árbol validado; todos los textos y atributos se escapan. */
@Component("sitioDisenadorRender")
public class SitioDisenadorRender {
    private static String esc(Object value){return HtmlUtils.htmlEscape(Objects.toString(value,""),"UTF-8");}
    public String documento(SitioForm sitio,String imagenBase){
        var out=new StringBuilder("<main id=\"contenido\">");var css=new StringBuilder();
        for(var b:sitio.getDiseno().getBloques()){
            if(!b.isVisible())continue;
            out.append("<section class=\"nd-section\" id=\"b-").append(esc(b.getId())).append("\" data-section-id=\"").append(esc(b.getId())).append("\" data-empty=\"").append(b.getComponentes().stream().noneMatch(SitioComponente::isVisible)).append("\" data-layout=\"").append(esc(b.getDistribucion())).append("\" style=\"--section-bg:").append(esc(b.getFondo())).append(";--section-color:").append(esc(b.getColor())).append(";--section-space:").append(b.getEspacio()).append("px;--section-height:").append(b.getAltura()).append("px\"><div class=\"nd-section-content\">");
            for(var n:b.getComponentes())node(out,css,n,sitio,imagenBase);
            out.append("</div></section>");
        }
        return "<style>"+css+"</style>"+out+"</main>";
    }
    private void attr(StringBuilder out,String key,Object value){out.append(' ').append(key).append("=\"").append(esc(value)).append('"');}
    private void node(StringBuilder out,StringBuilder css,SitioComponente n,SitioForm sitio,String base){
        if(!n.isVisible())return;
        var tag=n.getTag();out.append('<').append(tag);
        attr(out,"class","nd-component "+(n.getTipo().equals("whatsapp")?"nd-whatsapp-button ":"")+(n.getTipo().equals("icono")?"nd-school-object ":"")+n.getClases()+(n.getPosicion().equals("libre")?" nd-free":"")+(n.getEstilos().values().stream().anyMatch(v->v.getX()!=null||v.getY()!=null||v.getGiro()!=null)?" nd-original-moved":"")+(n.getImagenId()!=null&&!n.getTipo().equals("logo")?" nd-owned-image":"")+(tag.equals("img")&&n.getImagenId()==null&&n.getRecurso().equals("ninguno")?" nd-empty-image":""));
        attr(out,"data-node-key",n.getId());attr(out,"data-component-id",n.getId());attr(out,"data-component-type",n.getTipo());attr(out,"data-position",n.getPosicion());if(!n.getAncla().isEmpty())attr(out,"id",n.getAncla());
        if(n.getTipo().equals("icono")){if(n.isDecorativo())attr(out,"aria-hidden","true");else{attr(out,"role","img");attr(out,"aria-label",n.getDescripcion().isBlank()?n.getNombre():n.getDescripcion());}}
        if(tag.equals("img")){if(n.getImagenId()!=null)attr(out,"src",base+n.getImagenId());else if(n.getRecurso().equals("logo"))attr(out,"src","/images/logo1.png");else if(n.getRecurso().equals("foto"))attr(out,"src","/images/imagen.png");attr(out,"alt",n.getDescripcion());attr(out,"loading","lazy");}
        if(n.getTipo().equals("whatsapp")){
            var link=SitioWhatsapp.url(n.getWhatsappNumero(),n.getWhatsappMensaje());attr(out,"data-whatsapp-layout",n.getWhatsappDiseno());attr(out,"aria-label",n.getTexto().isBlank()?"Escríbenos por WhatsApp":n.getTexto());attr(out,"title",link.isEmpty()?"Configura el número de WhatsApp en Propiedades":n.getTexto());
            if(link.isEmpty())attr(out,"aria-disabled","true");else{attr(out,"href",link);attr(out,"target","_blank");attr(out,"rel","noopener noreferrer");}
        }else if(tag.equals("a"))attr(out,"href",n.getEnlace());if(tag.equals("button"))attr(out,"type","button");
        var motion=n.getEstilos().get("escritorio");if(motion!=null&&motion.getAnimacion()!=null&&!motion.getAnimacion().equals("ninguna")){attr(out,"data-nd-animation",motion.getAnimacion());attr(out,"style","--nd-delay:"+(motion.getDemora()==null?0:motion.getDemora())+"ms");}
        out.append('>');var selector="[data-node-key=\""+n.getId()+"\"]";
        var baseStyle=map(n.getEstilos().get("escritorio"));var baseCss=declaraciones(baseStyle,n.getPosicion(),false);if(!baseCss.isEmpty())css.append(selector).append('{').append(baseCss).append('}');
        for(var device:List.of("tableta","movil")){var override=map(n.getEstilos().get(device));var merged=new LinkedHashMap<String,Object>(baseStyle);merged.putAll(override);boolean adaptive=n.getPosicion().equals("libre")&&!"libre".equals(override.get("posicion"));if(adaptive){merged.put("posicion","flujo");merged.remove("x");merged.remove("y");}var deviceCss=declaraciones(merged,n.getPosicion(),adaptive);if(deviceCss.isEmpty())continue;css.append("@media ").append(device.equals("movil")?"(max-width:700px)":"(min-width:701px) and (max-width:1000px)").append('{').append(selector).append('{').append(deviceCss).append("}}");}
        if(!Set.of("img","br","hr").contains(tag)){if(!Set.of("mapa","whatsapp").contains(n.getTipo()))out.append(esc(n.getTexto()));for(var child:n.getHijos())node(out,css,child,sitio,base);if(n.getTipo().equals("icono"))out.append("<svg viewBox=\"0 0 64 64\" aria-hidden=\"true\" focusable=\"false\"><use href=\"").append(esc(SitioObjetosEscolares.recurso(n.getIcono()))).append("\"></use></svg>");if(n.getTipo().equals("whatsapp"))out.append(SitioWhatsapp.contenido(n.getTexto()));if(n.getTipo().equals("mapa"))out.append(SitioMapa.html(n.getTexto(),n.getDireccionMapa(),n.getZoomMapa(),n.getDisenoMapa(),"",""));if(n.getTipo().equals("galeria"))galeria(out,n,sitio,base);if(n.getTipo().equals("ubicaciones"))ubicaciones(out,sitio);out.append("</").append(tag).append('>');}
    }
    private void galeria(StringBuilder out,SitioComponente n,SitioForm sitio,String base){
        boolean found=false;
        for(var album:sitio.getExtras().getAlbumes().stream().filter(a->a.isActivo()&&(n.getAlbumId()==null||n.getAlbumId().isEmpty()||n.getAlbumId().equals(a.getId()))).sorted(Comparator.comparingInt(SitioExtras.Album::getOrden)).toList()){
            if(album.getFotosPublicas().isEmpty())continue;
            found=true;boolean carousel=Set.of("carrusel","destacada","tira").contains(n.getPresentacion());
            out.append("<article class=\"nd-gallery-album\"><h3>").append(esc(album.getTitulo())).append("</h3><p>").append(esc(album.getDescripcion())).append("</p><div class=\"").append(carousel?"nd-carousel":"nd-gallery-grid").append("\" data-nd-gallery=\"").append(esc(n.getPresentacion())).append("\" data-album-id=\"").append(esc(album.getId())).append("\" data-effect=\"").append(esc(n.getEfectoFoto())).append("\" data-transition=\"").append(esc(n.getTransicion())).append("\" data-captions=\"").append(n.isMostrarDescripciones()).append("\" style=\"--nd-gallery-columns:").append(n.getColumnas()).append("\" data-auto=\"").append(n.isAutomatico()).append("\" data-interval=\"").append(n.getIntervalo()).append("\"><div class=\"").append(carousel?"nd-carousel-track":"nd-photo-grid").append("\" tabindex=\"0\" aria-label=\"Fotografías del álbum\">");
            for(var foto:album.getFotosPublicas())out.append("<figure><a data-nd-photo=\"").append(esc(foto.getId())).append("\" aria-label=\"Ampliar fotografía: ").append(esc(foto.getDescripcion())).append("\" href=\"").append(esc(base+foto.getImagenId())).append("\" target=\"_blank\" rel=\"noopener\"><img src=\"").append(esc(base+foto.getImagenId())).append("\" alt=\"").append(esc(foto.getDescripcion())).append("\" loading=\"lazy\" draggable=\"false\"></a><figcaption>").append(esc(foto.getDescripcion())).append("</figcaption></figure>");
            out.append("</div></div></article>");
        }
        if(!found)out.append("<div class=\"nd-gallery-empty\"></div>");
    }
    private void ubicaciones(StringBuilder out,SitioForm sitio){out.append("<div class=\"nd-location-grid\">");for(var u:sitio.getExtras().getUbicaciones().stream().filter(SitioExtras.Ubicacion::isActivo).sorted(Comparator.comparingInt(SitioExtras.Ubicacion::getOrden)).toList())out.append(SitioMapa.html(u.getNombre(),u.getDireccion(),16,"tarjeta",u.getHorario(),u.getTelefono()));out.append("</div>");}
    private static Map<String,Object> map(SitioComponente.Estilo s){
        var m=new LinkedHashMap<String,Object>();if(s==null)return m;
        Object[][] pairs={{"x",s.getX()},{"y",s.getY()},{"ancho",s.getAncho()},{"alto",s.getAlto()},{"fuente",s.getFuente()},{"giro",s.getGiro()},{"radio",s.getRadio()},{"borde",s.getBorde()},{"espacio",s.getEspacio()},{"relleno",s.getRelleno()},{"opacidad",s.getOpacidad()},{"color",s.getColor()},{"fondo",s.getFondo()},{"colorBorde",s.getColorBorde()},{"alineacion",s.getAlineacion()},{"familia",s.getFamilia()},{"peso",s.getPeso()},{"distribucion",s.getDistribucion()},{"sombra",s.getSombra()},{"posicion",s.getPosicion()},{"encuadre",s.getEncuadre()},{"animacion",s.getAnimacion()},{"demora",s.getDemora()}};for(var pair:pairs)if(pair[1]!=null)m.put((String)pair[0],pair[1]);return m;
    }
    static String declaraciones(Map<String,Object> s,String position,boolean adaptive){
        var css=new StringBuilder();BiConsumer<String,Object> put=(k,v)->css.append(k).append(':').append(v).append(';');String pos=Objects.toString(s.getOrDefault("posicion",position));
        if(pos.equals("libre")){put.accept("position","absolute");put.accept("left",s.getOrDefault("x",0)+"px");put.accept("top",s.getOrDefault("y",0)+"px");}else if(adaptive){put.accept("position","relative");put.accept("left","auto");put.accept("top","auto");put.accept("transform","none");put.accept("display","block");put.accept("margin-block","12px");}
        if(s.containsKey("ancho"))put.accept("width",s.get("ancho").equals(0)?"auto":"min("+s.get("ancho")+"px, 100%)");if(s.containsKey("alto"))put.accept("height",s.get("alto").equals(0)?"auto":s.get("alto")+"px");if(s.containsKey("fuente"))put.accept("font-size",s.get("fuente")+"px");
        for(var pair:List.of(new String[]{"color","color"},new String[]{"fondo","background"},new String[]{"colorBorde","border-color"}))if(s.containsKey(pair[0]))put.accept(pair[1],s.get(pair[0]));
        for(var pair:List.of(new String[]{"radio","border-radius"},new String[]{"borde","border-width"},new String[]{"espacio","gap"},new String[]{"relleno","padding"}))if(s.containsKey(pair[0])){put.accept(pair[1],s.get(pair[0])+"px");if(pair[0].equals("borde"))put.accept("border-style","solid");}
        if(s.containsKey("opacidad"))put.accept("opacity",((Number)s.get("opacidad")).doubleValue()/100);
        if(s.containsKey("alineacion"))put.accept("text-align",Map.of("izquierda","left","centro","center","derecha","right").get(s.get("alineacion")));
        if(s.containsKey("peso"))put.accept("font-weight",s.get("peso").equals("negrita")?700:400);
        if(s.containsKey("familia")&&!s.get("familia").equals("heredada"))put.accept("font-family",Map.of("amigable","'Trebuchet MS',Arial,sans-serif","moderna","system-ui,Arial,sans-serif","clasica","Georgia,serif").get(s.get("familia")));
        if(s.containsKey("encuadre"))put.accept("object-fit",s.get("encuadre"));if(s.containsKey("sombra"))put.accept("box-shadow",s.get("sombra").equals("suave")?"0 10px 30px #12372a20":"none");
        String layout=Objects.toString(s.get("distribucion"),"");if(Set.of("vertical","horizontal","cuadricula","libre").contains(layout)){if(!pos.equals("libre"))put.accept("position","relative");if(layout.equals("cuadricula")){put.accept("display","grid");put.accept("grid-template-columns","repeat(auto-fit,minmax(min(100%,220px),1fr))");}else if(layout.equals("libre"))put.accept("display","block");else{put.accept("display","flex");put.accept("flex-direction",layout.equals("vertical")?"column":"row");put.accept("flex-wrap","wrap");}}
        if(!adaptive&&(s.containsKey("x")||s.containsKey("y")||s.containsKey("giro"))){if(pos.equals("original")){if(s.containsKey("x")||s.containsKey("y"))put.accept("translate",s.getOrDefault("x",0)+"px "+s.getOrDefault("y",0)+"px");if(s.containsKey("giro"))put.accept("rotate",s.get("giro")+"deg");}else{if(pos.equals("flujo")){put.accept("position","relative");put.accept("display","inline-block");}put.accept("transform",(pos.equals("flujo")?"translate("+s.getOrDefault("x",0)+"px,"+s.getOrDefault("y",0)+"px) ":"")+"rotate("+s.getOrDefault("giro",0)+"deg)");}}if(s.containsKey("animacion"))put.accept("--nd-motion",s.get("animacion"));if(s.containsKey("demora"))put.accept("--nd-delay",s.get("demora")+"ms");if(adaptive&&s.containsKey("giro"))put.accept("transform","rotate("+s.get("giro")+"deg)");
        return css.toString();
    }
}
