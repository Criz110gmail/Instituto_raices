package escuela.sitioweb;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.*;
import escuela.common.exception.ReglaNegocioException;

/** Documento estructurado. No admite HTML, CSS ni JavaScript aportados por el usuario. */
@Data
public class SitioDiseno {
    @Min(1) @Max(1) private int formato=1;
    @NotNull @Pattern(regexp="escolar|limpio") private String tema="escolar";
    @Pattern(regexp="#[0-9a-fA-F]{6}") @NotNull private String fondo="#fffef9";
    @Pattern(regexp="#[0-9a-fA-F]{6}") @NotNull private String tinta="#20362e";
    @Pattern(regexp="amigable|moderna|clasica") @NotNull private String fuente="amigable";
    @Min(0) @Max(36) private int radio=20;
    private boolean menuVisible=true;
    @NotNull @Size(min=1,max=60) @Valid private List<Bloque> bloques=new ArrayList<>();
    @Data public static class Objeto {
        @Min(-1500) @Max(1500) private int x;
        @Min(-1500) @Max(1500) private int y;
        @Min(0) @Max(1500) private int ancho;
        @Min(0) @Max(160) private int fuente;
        @Min(-180) @Max(180) private int giro;
        @Pattern(regexp="#[0-9a-fA-F]{6}") private String color;
        private boolean visible=true;
        @com.fasterxml.jackson.annotation.JsonIgnore public String getEstilo(){return "position:relative;display:inline-block;transform:translate("+x+"px,"+y+"px) rotate("+giro+"deg);"+(ancho>0?"width:"+ancho+"px;max-width:100%;":"")+(fuente>0?"font-size:"+fuente+"px;":"")+(color!=null?"color:"+color+";":"")+(!visible?"display:none;":"");}
    }
    @Data public static class Bloque {
        @NotBlank @Pattern(regexp="[a-zA-Z0-9-]{1,50}") private String id;
        @NotNull @Pattern(regexp="portada|titulo|texto|imagen|columnas|tarjetas|galeria|ubicaciones|contacto|espacio|separador|boton|cita|lista|escolar|seccion") private String tipo;
        @NotNull @Size(max=100) private List<@NotNull @Valid SitioComponente> componentes=new ArrayList<>();
        @NotNull @Pattern(regexp="encabezado|contenido|pie") private String papel="contenido";
        @NotNull @Pattern(regexp="original|vertical|horizontal|cuadricula|libre") private String distribucion="original";
        @Min(0) @Max(6000) private int altura;
        @NotNull @Size(max=100) private Map<@Pattern(regexp="(?:texto|imagen|enlace|libre)[0-9]{1,2}") String,@Valid Objeto> objetos=new LinkedHashMap<>();
        @Pattern(regexp="encabezado|portada|banda|nosotros|oferta|galeria|ubicaciones|accesos|pie") private String seccion;
        @NotNull @Size(max=100) private Map<@Pattern(regexp="[a-zA-Z0-9-]{1,50}") String,@NotNull @Size(max=5000) String> campos=new LinkedHashMap<>();
        @NotNull @Size(max=20) private Map<@Pattern(regexp="imagen[0-9]{1,2}") String,@Positive Long> imagenes=new LinkedHashMap<>();
        @NotNull @Size(max=30) private Map<@Pattern(regexp="enlace[0-9]{1,2}") String,@NotNull @Size(max=500) String> enlaces=new LinkedHashMap<>();
        @NotNull @Size(max=200) private String titulo="";
        @NotNull @Size(max=5000) private String texto="";
        @NotNull @Size(max=80) private String boton="";
        @NotNull @Size(max=500) private String enlace="";
        @Positive private Long imagenId;
        @Size(max=40) private String albumId;
        @NotNull @Pattern(regexp="#[0-9a-fA-F]{6}") private String fondo="#ffffff";
        @NotNull @Pattern(regexp="#[0-9a-fA-F]{6}") private String color="#20362e";
        @NotNull @Pattern(regexp="izquierda|centro|derecha") private String alineacion="izquierda";
        @NotNull @Pattern(regexp="normal|amplio|completo") private String ancho="normal";
        @NotNull @Pattern(regexp="pequeno|mediano|grande") private String tamano="mediano";
        @NotNull @Pattern(regexp="natural|panoramica|cuadrada|retrato") private String proporcion="natural";
        @NotNull @Pattern(regexp="cover|contain") private String encuadre="cover";
        @NotNull @Pattern(regexp="ninguna|aparecer|subir|zoom") private String animacion="ninguna";
        @Min(0) @Max(120) private int espacio=40;
        @Min(0) @Max(500) private int demora;
        private boolean visible=true;
        private boolean enMenu;
        @com.fasterxml.jackson.annotation.JsonIgnore public String getEstilo(){return "--bloque-fondo:"+fondo+";--bloque-tinta:"+color+";--bloque-espacio:"+espacio+"px;--bloque-demora:"+demora+"ms";}
        @com.fasterxml.jackson.annotation.JsonIgnore public List<String> getLineas(){return texto.lines().filter(s->!s.isBlank()).limit(12).toList();}
        @com.fasterxml.jackson.annotation.JsonIgnore public List<Map<String,String>> getTarjetas(){return getLineas().stream().limit(6).map(s->{var partes=s.split("\\|",2);return Map.of("titulo",partes[0].strip(),"texto",partes.length>1?partes[1].strip():"");}).toList();}
    }
    public void validar(boolean publicacion){
        if(bloques.stream().map(Bloque::getId).distinct().count()!=bloques.size())throw new ReglaNegocioException("Cada bloque debe tener un identificador distinto.");
        contraste(tinta,fondo);
        if(bloques.stream().anyMatch(b->"seccion".equals(b.tipo))&&bloques.stream().anyMatch(b->!"seccion".equals(b.tipo)))throw new ReglaNegocioException("Convierte toda la página al diseñador por secciones antes de guardar.");
        var ids=new HashSet<String>();var anclas=new HashSet<String>();var total=new int[]{0};
        for(var b:bloques)for(var c:b.componentes)validarComponente(c,ids,anclas,total,0,publicacion&&b.visible,false);

        for(var b:bloques){
            if(!"seccion".equals(b.tipo)&&!b.componentes.isEmpty())throw new ReglaNegocioException("Los componentes deben pertenecer a una sección del diseñador.");
            contraste(b.color,b.fondo);validarEnlace(b.enlace);for(var enlace:b.enlaces.values())validarEnlace(enlace);if("escolar".equals(b.tipo)&&b.seccion==null)throw new ReglaNegocioException("Selecciona una sección escolar válida.");
            if(publicacion&&b.visible){
                if(Set.of("portada","titulo","columnas").contains(b.tipo)&&b.titulo.isBlank())throw new ReglaNegocioException("Completa el título del bloque "+b.tipo+" antes de publicar.");
                if(Set.of("texto","cita","lista","tarjetas").contains(b.tipo)&&b.texto.isBlank())throw new ReglaNegocioException("Completa el contenido del bloque "+b.tipo+" antes de publicar.");
                if(b.tipo.equals("imagen")&&(b.imagenId==null||b.texto.isBlank()))throw new ReglaNegocioException("Selecciona la imagen y escribe su descripción antes de publicar.");
                if(b.tipo.equals("boton")&&(b.boton.isBlank()||b.enlace.isBlank()))throw new ReglaNegocioException("Completa el texto y destino del botón antes de publicar.");
                if(!b.boton.isBlank()&&b.enlace.isBlank())throw new ReglaNegocioException("Agrega el destino del botón «"+b.boton+"» antes de publicar.");
            }
        }
        if(publicacion&&bloques.stream().noneMatch(Bloque::isVisible))throw new ReglaNegocioException("Incluye al menos un bloque visible antes de publicar.");
    }
    private void validarComponente(SitioComponente c,Set<String> ids,Set<String> anclas,int[] total,int nivel,boolean publicacion,boolean dentroDeEnlace){
        if(nivel>12||++total[0]>800)throw new ReglaNegocioException("La página admite hasta 800 componentes y 12 niveles de contenedores.");
        if(!ids.add(c.getId()))throw new ReglaNegocioException("Cada componente debe tener un identificador distinto.");
        if(!c.getAncla().isEmpty()&&!anclas.add(c.getAncla()))throw new ReglaNegocioException("No se puede repetir el identificador de una sección o enlace interno.");
        validarEnlace(c.getEnlace());
        if(dentroDeEnlace&&c.getTag().equals("a"))throw new ReglaNegocioException("Un enlace no puede contener otro enlace.");
        if(Set.of("p","span","em","strong","a","h1","h2","h3","button","school-text","figcaption").contains(c.getTag())&&c.getHijos().stream().anyMatch(h->!Set.of("school-text","span","em","strong","br","img","a").contains(h.getTag())))throw new ReglaNegocioException("Los textos y enlaces sólo pueden contener textos, imágenes o acentos internos. Usa un contenedor para agrupar otros componentes.");

        if(c.getImagenId()!=null&&!c.getTag().equals("img"))throw new ReglaNegocioException("Las imágenes deben pertenecer a un componente de imagen o logotipo.");
        if(c.getTipo().equals("icono")&&(!c.getTag().equals("div")||!c.getHijos().isEmpty()||!c.getTexto().isEmpty()))throw new ReglaNegocioException("Selecciona un objeto escolar válido.");
        if(publicacion&&c.isVisible()&&c.getTipo().equals("icono")&&!c.isDecorativo()&&c.getDescripcion().isBlank())throw new ReglaNegocioException("Describe el objeto escolar «"+c.getNombre()+"» o márcalo como decorativo.");
        if(Set.of("imagen","logo").contains(c.getTipo())&&!c.getTag().equals("img"))throw new ReglaNegocioException("Selecciona un componente de imagen válido.");
        if(Set.of("img","br","hr").contains(c.getTag())&&(!c.getHijos().isEmpty()||!c.getTexto().isEmpty()))throw new ReglaNegocioException("Una imagen, línea o salto no puede contener otros objetos.");

        if(publicacion&&c.isVisible()&&Set.of("imagen","logo").contains(c.getTipo())&&c.getImagenId()==null&&c.getRecurso().equals("ninguno"))throw new ReglaNegocioException("Selecciona la imagen del componente «"+c.getNombre()+"» antes de publicar.");
        if(publicacion&&c.isVisible()&&Set.of("imagen","logo").contains(c.getTipo())&&c.getDescripcion().isBlank())throw new ReglaNegocioException("Describe la imagen «"+c.getNombre()+"» antes de publicar.");
        if(publicacion&&c.isVisible()&&Set.of("boton","enlace").contains(c.getTipo())&&c.getEnlace().isBlank())throw new ReglaNegocioException("Completa el destino del enlace «"+c.getNombre()+"».");
        for(var hijo:c.getHijos())validarComponente(hijo,ids,anclas,total,nivel+1,publicacion&&c.isVisible(),dentroDeEnlace||c.getTag().equals("a"));
    }
    @com.fasterxml.jackson.annotation.JsonIgnore public List<SitioComponente> getTodosComponentes(){var result=new ArrayList<SitioComponente>();for(var b:bloques)recolectar(b.componentes,result);return result;}
    private void recolectar(List<SitioComponente> list,List<SitioComponente> result){for(var n:list){result.add(n);recolectar(n.getHijos(),result);}}
    @com.fasterxml.jackson.annotation.JsonProperty(access=com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    public Set<Long> getImagenesComponentes(){var result=new LinkedHashSet<Long>();for(var b:bloques)if(b.visible)referencias(b.componentes,result,null);return result;}
    @com.fasterxml.jackson.annotation.JsonProperty(access=com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    public Set<String> getAlbumesComponentes(){var result=new LinkedHashSet<String>();for(var b:bloques)if(b.visible)referencias(b.componentes,null,result);return result;}
    private void referencias(List<SitioComponente> list,Set<Long> images,Set<String> albums){for(var n:list){if(!n.isVisible())continue;if(images!=null&&n.getImagenId()!=null)images.add(n.getImagenId());if(albums!=null&&n.getTipo().equals("galeria"))albums.add(n.getAlbumId()==null||n.getAlbumId().isBlank()?"*":n.getAlbumId());referencias(n.getHijos(),images,albums);}}
    static void contraste(String tinta,String fondo){double a=SitioService.luminancia(tinta),b=SitioService.luminancia(fondo);if((Math.max(a,b)+.05)/(Math.min(a,b)+.05)<4.5)throw new ReglaNegocioException("Una combinación de colores no tiene suficiente contraste. Elige texto más oscuro o un fondo más claro.");}
    static void validarEnlace(String valor){
        if(valor.isEmpty())return;
        if(valor.indexOf('\\')>=0||valor.chars().anyMatch(c->c<32))throw new ReglaNegocioException("El enlace no es válido.");
        if(valor.matches("#[a-zA-Z0-9_-]+"))return;
        try{var uri=new java.net.URI(valor);if(valor.startsWith("/")&&!valor.startsWith("//")&&uri.getHost()==null)return;
            if("https".equalsIgnoreCase(uri.getScheme())&&uri.getHost()!=null&&uri.getUserInfo()==null)return;
            if(valor.matches("tel:[+0-9-]{7,30}")||valor.matches("mailto:[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))return;
        }catch(java.net.URISyntaxException ignored){}
        throw new ReglaNegocioException("Usa un enlace https://, una sección #, un teléfono tel: o un correo mailto:.");
    }
}
