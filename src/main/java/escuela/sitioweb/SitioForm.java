package escuela.sitioweb;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class SitioForm {
 @NotBlank @Size(max=200) private String nombre;
 @NotBlank @Size(max=300) private String eslogan;
 @Size(max=2000) private String portada;
 @Size(max=5000) private String nosotros;
 @Size(max=5000) private String oferta;
 @Size(max=5000) private String talleres;
 @Size(max=5000) private String inscripciones;
 @Pattern(regexp="#[0-9a-fA-F]{6}") @NotNull private String principal="#2b5031";
 @Pattern(regexp="#[0-9a-fA-F]{6}") @NotNull private String acento="#ffc83e";
 @Positive private Long logoId;
 @Positive private Long portadaId;
 @Size(max=200) private String portadaAlt;
 @jakarta.validation.Valid private SitioDiseno diseno;
 @jakarta.validation.Valid @NotNull private SitioExtras extras=new SitioExtras();
 @NotNull @Min(0) private Long version=0L;
}
