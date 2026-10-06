package escuela.admin.dto;

import escuela.cobranza.dto.request.GeneracionCargosRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class GeneracionCargosForm {
    @NotNull private Long institucionId;
    private Long plantelId;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaCorte = LocalDate.now();
    private java.util.UUID seleccionId;
    @jakarta.validation.constraints.Size(max=5000)
    private java.util.Set<String> excluidos=new java.util.HashSet<>();
    private boolean seleccionIndividual;
    @jakarta.validation.constraints.Size(max=5000) private java.util.Set<String> incluidos=new java.util.HashSet<>();

    public GeneracionCargosRequest request() {
        return new GeneracionCargosRequest(institucionId, plantelId, fechaCorte,seleccionId,excluidos,seleccionIndividual?incluidos:null);
    }
}
