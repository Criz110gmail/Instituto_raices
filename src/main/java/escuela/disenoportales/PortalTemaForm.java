package escuela.disenoportales;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class PortalTemaForm {
 @Min(0) private long version;
 @NotNull @Pattern(regexp="#[0-9a-fA-F]{6}") private String principal="#153650";
 @NotNull @Pattern(regexp="#[0-9a-fA-F]{6}") private String fondo="#eef3f8";
 @NotNull @Pattern(regexp="#[0-9a-fA-F]{6}") private String acento="#39bda9";
 private boolean original=true;
 private boolean oscuroPersonalizado;
 private String oscuroPrincipal="#102b25",oscuroFondo="#0b1418",oscuroAcento="#39bda9";
 @AssertTrue(message="Revisa los tres colores del modo oscuro: usa # y seis dígitos.")
 public boolean isOscuroValido(){return !oscuroPersonalizado||(PortalColores.valido(oscuroPrincipal)&&PortalColores.valido(oscuroFondo)&&PortalColores.valido(oscuroAcento));}
}
