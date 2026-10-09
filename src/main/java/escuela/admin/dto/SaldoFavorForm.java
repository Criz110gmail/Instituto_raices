package escuela.admin.dto;
import escuela.finanzas.dto.request.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.*;
@Getter @Setter
public class SaldoFavorForm {
    @NotNull private Long version;
    @NotBlank @Size(max=2000) private String motivo;
    @NotBlank @Pattern(regexp="[0-9a-fA-F-]{36}") private String clave=UUID.randomUUID().toString();
    @Valid @NotEmpty @Size(max=20) private List<SolicitudAplicacionPagoForm> cargos=new ArrayList<>();
    public AplicarSaldoFavorRequest request() {
        return new AplicarSaldoFavorRequest(version,clave,motivo,cargos.stream()
          .map(c->new SolicitudAplicacionPagoRequest(c.getCargoId(),c.getMontoSolicitado())).toList());
    }
}
