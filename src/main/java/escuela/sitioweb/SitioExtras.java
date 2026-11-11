package escuela.sitioweb;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.*;

@Data
public class SitioExtras {
    @Valid @NotNull @Size(max=20) private List<Album> albumes=new ArrayList<>();
    @Valid @NotNull @Size(max=30) private List<Ubicacion> ubicaciones=new ArrayList<>();
    @Valid @NotNull private Contacto contacto=new Contacto();

    @Data public static class Album {
        @NotBlank @Size(max=40) private String id=UUID.randomUUID().toString();
        @NotBlank @Size(max=150) private String titulo;
        @Size(max=1000) private String descripcion;
        @Min(0) @Max(999) private int orden;
        private boolean activo=true;
        @Size(max=1000) private String motivoRetiro;
        @Valid @NotNull @Size(max=100) private List<Foto> fotos=new ArrayList<>();
        @com.fasterxml.jackson.annotation.JsonIgnore
        public List<Foto> getFotosPublicas(){return fotos.stream().filter(Foto::isActivo).sorted(Comparator.comparingInt(Foto::getOrden).thenComparing(Foto::getId)).toList();}
    }
    @Data public static class Foto {
        @NotBlank @Size(max=40) private String id=UUID.randomUUID().toString();
        @NotNull @Positive private Long imagenId;
        @NotBlank @Size(max=200) private String descripcion;
        @Min(0) @Max(999) private int orden;
        private boolean activo=true;
        @Size(max=1000) private String motivoRetiro;
    }
    @Data public static class Ubicacion {
        @NotBlank @Size(max=40) private String id=UUID.randomUUID().toString();
        @NotNull @Positive private Long plantelId;
        @NotBlank @Size(max=150) private String nombre;
        @NotBlank @Size(max=500) private String direccion;
        @NotBlank @Size(max=200) private String horario;
        @Pattern(regexp="[+0-9 ()-]{0,30}") private String telefono;
        @Min(0) @Max(999) private int orden;
        private boolean activo=true;
        @Size(max=1000) private String motivoRetiro;
        @com.fasterxml.jackson.annotation.JsonIgnore
        public String getMapaUrl(){return "https://www.google.com/maps/search/?api=1&query="+java.net.URLEncoder.encode(direccion,java.nio.charset.StandardCharsets.UTF_8);}
    }
    @Data public static class Contacto {
        @Email @Size(max=254) private String correo;
        @Pattern(regexp="[+0-9 ()-]{0,30}") private String telefono;
        private boolean whatsappActivo;
        @Pattern(regexp="(?:[1-9][0-9]{7,14})?") private String whatsapp;
        @Size(max=500) private String mensaje;
        @Size(max=5000) private String privacidad;
        @com.fasterxml.jackson.annotation.JsonIgnore
        public String getWhatsappUrl(){return whatsappActivo&&whatsapp!=null&&!whatsapp.isBlank()?"https://wa.me/"+whatsapp+"?text="+java.net.URLEncoder.encode(mensaje==null?"":mensaje,java.nio.charset.StandardCharsets.UTF_8):null;}
        @com.fasterxml.jackson.annotation.JsonIgnore
        public String getTelefonoUrl(){return telefono==null?"":"tel:"+telefono.replaceAll("[^+0-9]","");}
    }
}
