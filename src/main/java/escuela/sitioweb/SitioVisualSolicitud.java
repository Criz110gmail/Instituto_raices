package escuela.sitioweb;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public record SitioVisualSolicitud(@NotNull @Min(0) Long version,@NotBlank @Size(max=200)String nombre,
 @NotBlank @Size(max=300)String eslogan,@NotNull @Pattern(regexp="#[0-9a-fA-F]{6}")String principal,
 @NotNull @Pattern(regexp="#[0-9a-fA-F]{6}")String acento,@Positive Long logoId,
 @NotNull @Valid SitioDiseno diseno,@Valid SitioExtras extras) {
 public SitioVisualSolicitud(Long version,String nombre,String eslogan,String principal,String acento,Long logoId,SitioDiseno diseno){this(version,nombre,eslogan,principal,acento,logoId,diseno,null);}
}
