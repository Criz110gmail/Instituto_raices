package escuela.sitioweb;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.*;

/** Árbol de componentes tipados; nunca contiene HTML, CSS o scripts del usuario. */
@Data
public class SitioComponente {
    @NotBlank @Pattern(regexp="[a-zA-Z0-9-]{1,60}") private String id;
    @NotNull @Pattern(regexp="texto|titulo|imagen|logo|menu|enlace|boton|contenedor|tarjeta|forma|icono|mapa|whatsapp|linea|galeria|ubicaciones|salto") private String tipo="texto";
    @NotNull @Pattern(regexp="div|section|header|footer|nav|article|span|school-text|h1|h2|h3|p|em|strong|a|img|br|hr|ul|li|figure|figcaption|button") private String tag="div";
    @NotNull @Size(max=160) private String nombre="";
    @NotNull @Size(max=5000) private String texto="";
    @NotNull @Size(max=240) @Pattern(regexp="[a-zA-Z0-9_ -]*") private String clases="";
    @NotNull @Size(max=60) @Pattern(regexp="[a-zA-Z0-9_-]*") private String ancla="";
    @NotNull @Size(max=500) private String enlace="";
    @NotNull @Pattern(regexp="ninguno|logo|foto") private String recurso="ninguno";
    @Positive private Long imagenId;
    @NotNull @Size(max=300) private String descripcion="";
    @NotNull @Pattern(regexp=SitioObjetosEscolares.PATRON) private String icono="cuaderno";
    private boolean decorativo=true;
    @NotNull @Size(max=500) private String direccionMapa="";
    @Min(10) @Max(19) private int zoomMapa=16;
    @NotNull @Pattern(regexp="tarjeta|solo") private String disenoMapa="tarjeta";
    @NotNull @Size(max=40) private String whatsappNumero="";
    @NotNull @Size(max=1000) private String whatsappMensaje="";
    @NotNull @Pattern(regexp="etiqueta|icono") private String whatsappDiseno="etiqueta";
    @Size(max=40) private String albumId;
    @NotNull @Pattern(regexp="cuadricula|mosaico|muro|tarjetas|carrusel|destacada|tira") private String presentacion="cuadricula";
    @NotNull @Pattern(regexp="ninguno|acercar|elevar|monocromo") private String efectoFoto="ninguno";
    @NotNull @Pattern(regexp="deslizar|fundido|zoom") private String transicion="deslizar";
    @Min(2) @Max(5) private int columnas=3;
    private boolean mostrarDescripciones=true;
    private boolean automatico;
    @Min(3) @Max(15) private int intervalo=5;
    @NotNull @Pattern(regexp="original|flujo|libre") private String posicion="flujo";
    private boolean visible=true;
    @NotNull @Size(max=3) private Map<@Pattern(regexp="escritorio|tableta|movil") String,@NotNull @Valid Estilo> estilos=new LinkedHashMap<>();
    @NotNull @Size(max=100) private List<@NotNull @Valid SitioComponente> hijos=new ArrayList<>();

    @Data public static class Estilo {
        @Min(-2000) @Max(3000) private Integer x;
        @Min(-2000) @Max(6000) private Integer y;
        @Min(0) @Max(2000) private Integer ancho;
        @Min(0) @Max(3000) private Integer alto;
        @Min(8) @Max(180) private Integer fuente;
        @Min(-180) @Max(180) private Integer giro;
        @Min(0) @Max(200) private Integer radio;
        @Min(0) @Max(30) private Integer borde;
        @Min(0) @Max(160) private Integer espacio;
        @Min(0) @Max(160) private Integer relleno;
        @Min(0) @Max(100) private Integer opacidad;
        @Pattern(regexp="#[0-9a-fA-F]{6}") private String color;
        @Pattern(regexp="#[0-9a-fA-F]{6}|transparent") private String fondo;
        @Pattern(regexp="#[0-9a-fA-F]{6}") private String colorBorde;
        @Pattern(regexp="izquierda|centro|derecha") private String alineacion;
        @Pattern(regexp="heredada|amigable|moderna|clasica") private String familia;
        @Pattern(regexp="normal|negrita") private String peso;
        @Pattern(regexp="original|vertical|horizontal|cuadricula|libre") private String distribucion;
        @Pattern(regexp="ninguna|suave") private String sombra;
        @Pattern(regexp="original|flujo|libre") private String posicion;
        @Pattern(regexp="cover|contain") private String encuadre;
        @Pattern(regexp="ninguna|aparecer|subir|zoom") private String animacion;
        @Min(0) @Max(1000) private Integer demora;
    }
}
